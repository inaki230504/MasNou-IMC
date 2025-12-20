package UI

import ViewModel.TorneoViewModel
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import getPlayer
import insertarJugador
import player

@Composable
fun tablaEspera(
    vm: TorneoViewModel
) {
    var name by remember { mutableStateOf("") }
    Column (modifier = Modifier.fillMaxWidth(0.6f).padding(end = 20.dp).fillMaxHeight()) {
        Text(
            "Añadir jugador",
            fontSize = 30.sp,

            )
        OutlinedTextField(
            value = name,
            onValueChange = { newValue -> name = newValue },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(Color.White)
        )
        Button(
            onClick = {

                    vm.agregarJugador(name)
                name = ""

            },
            colors = ButtonDefaults.buttonColors(Color(0xFFFFA500))
        ) {
            Text("Añadir")
        }
        Text(
            "Jugadores en Cola",
            fontSize = 30.sp
        )
        LazyColumn(
            modifier = Modifier.border(1.dp, Color.Black).fillMaxHeight(0.7f).fillMaxWidth().weight(0.5f)

        ) {
            itemsIndexed(vm.jugadoresCola) { index, play ->

                Card(modifier = Modifier.fillMaxWidth()){
                    Row {
                        Text(
                            "#${index + 1} ",
                            fontSize = 25.sp
                        )
                        Text(
                            "${play.nombre}",
                            fontSize = 25.sp
                        )
                    }
                }
            }
        }
        Column (modifier = Modifier.padding(bottom = 20.dp)){
            Text(
                "Inactivos",
                fontSize = 30.sp
            )
            LazyColumn(
                modifier = Modifier.border(1.dp, Color.Black).fillMaxHeight(0.15f).fillMaxWidth()

            ) {
                itemsIndexed(vm.jugadoresInactivos) { index, play ->

                    Card(modifier = Modifier.fillMaxWidth()

                    ) {
                        Row {
                            Text(
                                "#${index + 1} ",
                                fontSize = 25.sp
                            )
                            Text(
                                "${play.nombre}",
                                fontSize = 25.sp
                            )
                        }
                    }
                }
            }
        }
    }

}