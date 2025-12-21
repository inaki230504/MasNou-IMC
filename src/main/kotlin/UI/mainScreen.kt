package UI

import ViewModel.TorneoViewModel
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import insertMesas
import insertarMach
import player
import removeAllMesas
import removeAllPlayers
import stadoMatch
import stadoPrimary
import updatePlayer

@Composable
@Preview
fun mainScreen(modifier: Modifier,vm: TorneoViewModel) {
    var paused by remember { mutableStateOf(true) }
    var second by remember { mutableStateOf(1) }
    var numMesas by remember { mutableStateOf(0) }
    var compNMesa by remember { mutableStateOf("") }
    var dropmenu by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(true) }
    var showdialog by remember { mutableStateOf(false) }
    var hora by remember { mutableStateOf("") }
    var minuto by remember { mutableStateOf("") }
    var segundos by remember { mutableStateOf("") }
    var inicio by remember { mutableStateOf(true) }
    var startTournament by remember { mutableStateOf(0) }
    var mesasIniciadas by remember { mutableStateOf(false) }
    var cola by remember { mutableStateOf(true) }

    LaunchedEffect(startTournament) {
        if (!mesasIniciadas && vm.jugadoresCola.size >= 2) {
            vm.iniciarTorneo(numMesas)
            mesasIniciadas = true
        }
    }
    if (cola) {
        cola = false
        val nuevaCola = ArrayDeque(vm.jugadoresCola)
        nuevaCola.addAll(vm.jugadores.filter { it.estado == stadoPrimary.Cola })
        vm.jugadoresCola = nuevaCola
    }





    Column(modifier.background(Color(0xFFFFF5E1))) {

        if (visible) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row {
                    Button(
                        onClick = {
                            dropmenu = !dropmenu
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White
                        )
                    ) {
                        Text(
                            "Ajustes"
                        )
                    }
                }
                DropdownMenu(
                    expanded = dropmenu,
                    onDismissRequest = { dropmenu = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            showdialog = !showdialog
                            dropmenu = !dropmenu
                        }
                    ) {
                        Text(
                            "Cambiar Parametros"
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            visible = !visible
                            dropmenu = false
                        }
                    ) {
                        Text("Ocultar")
                    }
                    DropdownMenuItem(
                        onClick = {}
                    ) {
                        Text(
                            "Estadisticas"
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            inicio = false
                            mesasIniciadas = false  // 🔹 Resetear para que LaunchedEffect cree mesas
                            startTournament++       // Dispara LaunchedEffect
                            dropmenu = false
                        }
                    ) {
                        Text("Iniciar")
                    }

                    DropdownMenuItem(
                        onClick = {
                          vm.reiniciar()

                            inicio = false
                        }
                    ) {
                        Text("Reiniciar")
                    }

                }
            }
        }
        if (showdialog) {
            DialogWindow(
                onCloseRequest = { showdialog = false },
                title = "Parametros Generales",
                resizable = false
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(16.dp)
                ) {
                    LazyColumn {
                        item {
                            Text("Número de Mesas")
                            OutlinedTextField(
                                value = compNMesa,
                                onValueChange = { compNMesa = it },
                                label = { "NºMesas" },
                                placeholder = { "Ingresa su número de Mesas" },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tiempo del Torneo:")
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Hora
                                OutlinedTextField(
                                    value = hora,
                                    onValueChange = { hora = it },
                                    label = { Text("Horas") },
                                    placeholder = { Text("HH") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                // Minutos
                                OutlinedTextField(
                                    value = minuto,
                                    onValueChange = { minuto = it },
                                    label = { Text("Minutos") },
                                    placeholder = { Text("MM") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                // Segundos
                                OutlinedTextField(
                                    value = segundos,
                                    onValueChange = { segundos = it },
                                    label = {
                                        Text(
                                            "Segundos",
                                            fontSize = 15.sp
                                        )
                                    },
                                    placeholder = { Text("SS") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                            }
                            Button(onClick = {
                                showdialog = false

                                // ⏱ Tiempo
                                if (
                                    hora.toIntOrNull() != null &&
                                    minuto.toIntOrNull() != null &&
                                    segundos.toIntOrNull() != null
                                ) {
                                    second = hora.toInt() * 3600 +
                                            minuto.toInt() * 60 +
                                            segundos.toInt()
                                }

                                // 🪑 Mesas
                                if (compNMesa.toIntOrNull() != null) {
                                    val nuevoNum = compNMesa.toInt()

                                    if (mesasIniciadas && nuevoNum > vm.mesas.size) {
                                        startTournament++   // 🔥 SOLO ESTO
                                    }
                                    insertMesas( nuevoNum)
                                    numMesas = nuevoNum
                                }
                            }) {
                                Text("Aplicar")
                            }

                        }
                    }
                }
            }

        }
        topBar(modifier = Modifier.fillMaxHeight(0.1f), paused, onTogglePause = { paused = !paused })
        Row(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ladderBoard( vm)


            //mesa
            table(
                second, paused,
                onVisible = { isVisible: Boolean ->
                    visible = isVisible
                },
                visible,
                onTick = { if (second > 0) second-- },
                vm
            )
            tablaEspera(

                vm
            )

        }
    }
}
@Composable
fun topBar(modifier: Modifier, paused: Boolean, onTogglePause: () -> Unit){
    Row (modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text("Mercantil",
            fontSize = 80.sp,
            fontFamily = FontFamily.Monospace
        )
        Button(onClick = onTogglePause,
            colors = ButtonDefaults.buttonColors(Color(0xFFFFA500))
        ) {
            Text(if (paused) "Reanudar" else "Parar")
        }
    }
}