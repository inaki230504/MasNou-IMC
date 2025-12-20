package UI

import ViewModel.TorneoViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import business.finalizarPartida
import player
import tables

@Composable
fun table(
    seconds: Int, paused: Boolean,
    onVisible: (Boolean) -> Unit,
    actVisble: Boolean,
    onTick: () -> Unit,
    vm: TorneoViewModel
){
    Column (verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight()){
        TimerHours(seconds, paused, onTick = onTick)
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Column {
                Text("Last mach")
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFE0E0E0)
                    )
                ) {
                    Text("Deshacer")
                }
            }

            Text(
                "Partidas",
                fontSize = 30.sp
            )

            Image(
                painter =  painterResource("Logo_Mercantil_1.png"),
                contentDescription = "",
                modifier = Modifier.size(100.dp).clickable{
                    if(actVisble == false){
                        onVisible(true)
                    }
                },
                colorFilter = ColorFilter.tint(
                    Color(0xFFFFF5E1),
                    blendMode = BlendMode.Multiply
                )

            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black).background(color = Color(0xFFFFA500))
        ) {
            Text(
                "Mesa",
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center,
                fontSize = 25.sp,

                )
            Text(
                "Blancas",
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.Start,
                fontSize = 25.sp
            )
            Text(
                "Negras",
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 25.sp
            )
        }
        LazyColumn(modifier = Modifier.fillMaxHeight(0.90f).border(1.dp, Color.Black).fillMaxWidth(),

            ) {

            itemsIndexed(vm.mesas) { index ,mesa ->
                Card{

                    Row (modifier = Modifier.fillMaxWidth().padding(start = 25.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){

                        Text("${index+1}.",
                            modifier = Modifier.weight(0.5f),
                            fontSize = 25.sp
                        )
                        //Jugador 1 ganador blancas
                        Text((" ${mesa.match?.jugador1?.nombre}"),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.weight(2f),
                            fontSize = 25.sp
                        )
                        Row(   horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(4f)) {
                            Button(
                                onClick = {mesa.match?.let {
                                    finalizarPartida(mesa, "1-0",vm)

                                }},
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.White
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource("whiteking.jpg"),
                                        contentDescription = "",
                                        modifier = Modifier.size(20.dp),
                                        colorFilter = ColorFilter.tint(
                                            Color.White,
                                            blendMode = BlendMode.Multiply
                                        )
                                    )
                                    Text(
                                        "1-0",
                                        color = Color.Black
                                    )
                                }
                            }
                            Button(
                                onClick = {  mesa.match?.let {
                                    finalizarPartida(mesa, "1/2-1/2", vm)
                                }},
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFFFFA500)
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource("draw.jpg"),
                                        contentDescription = "",
                                        modifier = Modifier.size(20.dp),
                                        colorFilter = ColorFilter.tint(
                                            Color(0xFFFFA500),
                                            blendMode = BlendMode.Multiply
                                        )
                                    )
                                    Text(
                                        " 1/2-1/2 "
                                    )
                                }
                            }
                            Button(
                                onClick = {mesa.match?.let {
                                    finalizarPartida(mesa, "0-1", vm)
                                }},
                                colors = ButtonDefaults.buttonColors( Color(0xFF4F4F4F))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource("blacKing.jpg"),
                                        contentDescription = "",
                                        modifier = Modifier.size(20.dp),
                                        colorFilter = ColorFilter.tint(
                                            Color(0xFF4F4F4F),
                                            blendMode = BlendMode.Multiply
                                        )
                                    )
                                    Text(
                                        "0-1"
                                    )
                                }
                            }
                        }
                        //Jugador 2 negras retador
                        Text((" ${mesa.match?.jugador2?.nombre}"),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.weight(2f),
                            fontSize = 25.sp
                        )

                    }
                }

            }
        }

    }
}