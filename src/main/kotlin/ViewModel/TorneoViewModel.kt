package ViewModel

import androidx.compose.runtime.*
import getListaPlayers
import getMesas
import insertarJugador
import insertarMach
import match
import player
import removeAllMesas
import removeAllPlayers
import stadoPrimary
import tables
import updatePlayer
import updateResultado
import updateScore

class TorneoViewModel {

    var jugadores by mutableStateOf(getListaPlayers())
        private set

    var mesas by mutableStateOf(getMesas())
        private set

    var jugadoresCola by mutableStateOf( ArrayDeque<player>())

    val jugadoresInactivos: List<player>
        get() = jugadores.filter { it.estado == stadoPrimary.Inactivo }


    fun agregarJugador(nuevo: String) {

        jugadoresCola = ArrayDeque(jugadoresCola).apply {
            addLast(player(insertarJugador(nuevo),nuevo,(stadoPrimary.Cola)
            ))
        }
        jugadores = getListaPlayers()

    }
    fun cambiarEstado(jugador: player, nuevoEstado: stadoPrimary) {
        updatePlayer(
            player(jugador.id, jugador.nombre, (nuevoEstado))
        )

        jugadores = getListaPlayers()

        val actualizado = jugadores.firstOrNull { it.id == jugador.id } ?: return

        jugadoresCola = ArrayDeque(jugadoresCola).apply {
            removeIf { it.id == actualizado.id }

            if (nuevoEstado == stadoPrimary.Cola) {
                addLast(actualizado)
            }
        }
    }





    fun sincronizarEstadosConMesas() {
        val activos = mesas
            .flatMap { listOfNotNull(it.match?.jugador1, it.match?.jugador2) }
            .map { it.id }
            .toSet()

        jugadores = jugadores.map { jugador ->
            when{
                jugador.id in activos ->
                        jugador.copy(estado = ( (stadoPrimary.Activo)))
                jugador.estado == ( stadoPrimary.Activo) ->
                    jugador.copy(estado = (stadoPrimary.Cola))
            }


            if (jugador.id in activos) {
                jugador.copy(estado = ( (stadoPrimary.Activo)))
            } else {
                jugador.copy(estado = ((stadoPrimary.Cola)))
            }
        }
    }

    fun refrescarMesas() {
        mesas = getMesas()
        sincronizarEstadosConMesas()
    }
    fun iniciarTorneo(numMesas: Int) {
        val mesasActivas = mesas.size
        val mesasAcrear = (numMesas - mesasActivas).coerceAtLeast(0)

        if (mesasAcrear <= 0 || jugadoresCola.size < 2) return

        generarMesas(mesasAcrear)
    }

    fun generarMesas(numMesas: Int) {
        val cola = ArrayDeque(jugadores.filter { it.estado == stadoPrimary.Cola })

        while (mesas.size < numMesas) {
            val rank = mesas.size + 1
            mesas = mesas + tables(id = rank, match = null, estado = stadoMatch.EnProgreso)
        }

        mesas = mesas.map { mesa ->
            if (mesa.match == null) {
                val j1 = cola.removeFirst()
                val j2 = cola.removeFirst()
                mesa.copy(match = match(j1, j2), estado = stadoMatch.EnProgreso)
            } else mesa
        }

        val activosIds = mesas
            .flatMap { listOfNotNull(it.match?.jugador1, it.match?.jugador2) }
            .mapNotNull { it.id }
            .toSet()

        jugadores = jugadores.map {
            if (it.id in activosIds) it.copy(estado = ( stadoPrimary.Activo)) else it
        }

        mesas.filter { it.match != null }.forEach { insertarMach(it) }
        jugadores.filter { it.id in activosIds }.forEach { updatePlayer(it) }
    }
    fun reiniciar(){
        removeAllMesas()
        removeAllPlayers()
        refrescarMesas()
        jugadores = getListaPlayers()
        jugadoresCola = ArrayDeque()
    }
    fun finalizarPartida(mesa: tables, resultado: String) {
        val match = mesa.match ?: return
        var puntos : Double = 0.0
        updateResultado(mesa.id, resultado)

        val (ganador, perdedor) = when (resultado) {
            "1-0" -> match.jugador1 to match.jugador2
            "0-1" -> match.jugador2 to match.jugador1
            "1/2-1/2" -> match.jugador2 to match.jugador1
            else -> return
        }

        when(resultado){
            "1-0" -> puntos = 1.0
            "0-1" -> puntos = 1.0
            "1/2-1/2" -> puntos = 0.5
            else -> return
        }
        updateScore(ganador,puntos)
        if(resultado.equals("1/2-1/2")){
            updateScore(perdedor,puntos)
        }

        cambiarEstado(perdedor, stadoPrimary.Cola)

        cambiarEstado(ganador, stadoPrimary.Activo)

        val siguiente = jugadoresCola.firstOrNull()
        if (siguiente != null) {
            jugadoresCola = ArrayDeque(jugadoresCola).apply { removeFirst() }
            cambiarEstado(siguiente, stadoPrimary.Activo)
            mesa.match = match(ganador, siguiente)
        } else {
            mesa.match = null
        }

        mesa.estado = stadoMatch.EnProgreso
        insertarMach(mesa)

        refrescarMesas()
    }


}
