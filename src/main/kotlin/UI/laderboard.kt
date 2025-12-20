package UI

import ViewModel.TorneoViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import stadoPrimary
import updatePlayer

@Composable
fun ladderBoard( vm: TorneoViewModel) {
    Column (modifier = Modifier.padding(start = 10.dp).fillMaxWidth(0.25f)){
        Text(
            "Clasificación",
            fontSize = 50.sp,
            fontFamily = FontFamily.Monospace
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black)
                .background(color = Color(0xFFFFA500))
        ) {
            Text(
                "Rk.",
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
            Text(
                "Nombre",
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1.5f),
                textAlign = TextAlign.Start,
                fontSize = 15.sp
            )
            Text(
                "Puntos",
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
            Text(
                "Estado",
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
        }
        LazyColumn(modifier = Modifier.fillMaxHeight(0.95f).border(1.dp, Color.Black).fillMaxWidth(),

            ) {

            itemsIndexed(vm.jugadores.sortedByDescending { it.puntos }) { index, players ->
                Card{

                    Row (modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){

                        Text("${index+1}.",
                            modifier = Modifier.weight(0.5f),
                            fontSize = 25.sp
                        )
                        Text((" ${players.nombre}"),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.weight(2f),
                            fontSize = 25.sp
                        )

                        Text(players.puntos.toString(),
                            modifier = Modifier.weight(1f),
                            fontSize = 25.sp)

                        Button(
                            onClick = {
                                if (players.estado.value != stadoPrimary.Activo) {
                                    val nuevoEstado = if (players.estado.value == stadoPrimary.Cola) {
                                        stadoPrimary.Inactivo
                                    } else {
                                        stadoPrimary.Cola
                                    }

                                    vm.cambiarEstado(players.id, nuevoEstado)


                                    updatePlayer(players)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = when (players.estado.value) {
                                    stadoPrimary.Activo -> Color.Green
                                    stadoPrimary.Cola -> Color.Gray
                                    stadoPrimary.Inactivo -> Color.Red
                                }
                            )
                        ) {
                            Text(
                                players.estado.value.name,
                                fontSize = 10.sp
                            )
                        }

                    }
                }

            }
        }
    }
}