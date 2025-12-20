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

class TorneoViewModel {

    var jugadores by mutableStateOf(getListaPlayers())
        private set

    var mesas by mutableStateOf(getMesas())
        private set

    var jugadoresCola by mutableStateOf( ArrayDeque<player>())

    val jugadoresInactivos: List<player>
        get() = jugadores.filter { it.estado.value == stadoPrimary.Inactivo }


    fun agregarJugador(nuevo: String) {

        jugadoresCola = ArrayDeque(jugadoresCola).apply {
            addLast(player(insertarJugador(nuevo),nuevo,mutableStateOf(stadoPrimary.Cola)
            ))
        }
        jugadores = getListaPlayers()

    }
    fun cambiarEstado(jugador: player, nuevoEstado: stadoPrimary) {
        // 1️⃣ Persistencia
        updatePlayer(
            player(jugador.id, jugador.nombre, mutableStateOf(nuevoEstado))
        )

        // 2️⃣ Refrescamos snapshot
        jugadores = getListaPlayers()

        // 3️⃣ Obtenemos la instancia REAL
        val actualizado = jugadores.firstOrNull { it.id == jugador.id } ?: return

        // 4️⃣ Actualizamos FIFO (SIEMPRE reasignando)
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
                        jugador.copy(estado = ( mutableStateOf(stadoPrimary.Activo)))
                jugador.estado == mutableStateOf( stadoPrimary.Activo) ->
                    jugador.copy(estado = mutableStateOf(stadoPrimary.Cola))
            }


            if (jugador.id in activos) {
                jugador.copy(estado = ( mutableStateOf(stadoPrimary.Activo)))
            } else jugador
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
        val cola = ArrayDeque(jugadores.filter { it.estado.value == stadoPrimary.Cola })

        // Si no hay mesas suficientes, creamos mesas vacías hasta llegar al número
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
            if (it.id in activosIds) it.copy(estado = mutableStateOf( stadoPrimary.Activo)) else it
        }

        mesas.filter { it.match != null }.forEach { insertarMach(it) }
        jugadores.filter { it.id in activosIds }.forEach { updatePlayer(it) }
    }
    fun reiniciar(){
        removeAllMesas()
        removeAllPlayers()

    }
}
