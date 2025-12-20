import UI.mainScreen
import ViewModel.TorneoViewModel
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.window.*

@Composable
@Preview
fun App(modifier: Modifier) {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            getListaPlayers().forEach{
               text += it.nombre
            }
        },
            modifier = modifier
        ) {
            Text(text)
        }

    }
}









fun main() = application {
    Window(onCloseRequest = ::exitApplication,
        title = "Masnou IMC",
        resizable = true,
        icon = painterResource("odin.PNG"),
        state = WindowState(
            placement = WindowPlacement.Maximized
        )

        ) {
        val vwm : TorneoViewModel = TorneoViewModel()
        mainScreen(modifier = Modifier.fillMaxSize(),vwm )
    }
}
