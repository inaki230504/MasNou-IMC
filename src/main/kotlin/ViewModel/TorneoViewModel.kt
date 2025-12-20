package ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var jugadoresCola = mutableStateOf(ArrayDeque<player>(jugadores.filter { it -> it.estado.value == stadoPrimary.Cola }))
    var jugadoresInactivos = mutableStateOf(jugadores.filter { it -> it.estado.value == stadoPrimary.Inactivo })

    fun agregarJugador(nuevo: player) {
        insertarJugador(nuevo.nombre)
    }
    fun cambiarEstado(jugadorId: Int?, nuevoEstado: stadoPrimary) {
        jugadores = jugadores.map {
            if (it.id == jugadorId) it.copy(estado = mutableStateOf(nuevoEstado))
            else it
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

        if (mesasAcrear <= 0 || jugadoresCola.value.size < 2) return

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
