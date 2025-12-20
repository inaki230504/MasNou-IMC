package business

import ViewModel.TorneoViewModel
import insertarMach
import match
import player
import tables
import updatePlayer


fun finalizarPartida(
    mesa: tables,
    resultado: String,
    vm: TorneoViewModel
){
    val matchActual = mesa.match ?: return

    val nuevoMatch: match?
    when (resultado) {
        "1-0" -> {
            val ganador = matchActual.jugador1
            val perdedor = matchActual.jugador2

            ganador.estado.value = stadoPrimary.Activo
            perdedor.estado.value = stadoPrimary.Cola
            updatePlayer(ganador)
            updatePlayer(perdedor)

            nuevoMatch = match(ganador, vm.jugadores.firstOrNull { it.estado.value == stadoPrimary.Cola } ?: return)
        }

        "0-1" -> {
            val ganador = matchActual.jugador2
            val perdedor = matchActual.jugador1

            ganador.estado.value = stadoPrimary.Activo
            perdedor.estado.value = stadoPrimary.Cola
            updatePlayer(ganador)
            updatePlayer(perdedor)

            nuevoMatch = match(ganador, vm.jugadores.firstOrNull { it.estado.value == stadoPrimary.Cola } ?: return)
        }

        "1/2-1/2" -> {
            val blancoAnterior = matchActual.jugador1
            val negroAnterior = matchActual.jugador2

            negroAnterior.estado.value = stadoPrimary.Activo
            blancoAnterior.estado.value = stadoPrimary.Cola
            updatePlayer(blancoAnterior)
            updatePlayer(negroAnterior)

            nuevoMatch =
                match(negroAnterior, vm.jugadores.firstOrNull { it.estado.value == stadoPrimary.Cola } ?: return)
        }

        else -> return
    }

    mesa.match = nuevoMatch
    mesa.estado = stadoMatch.EnProgreso
    insertarMach(mesa)
    vm.refrescarMesas()
}

