package UI

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TimerHours(
    seconds: Int,
    paused: Boolean,
    onTick: () -> Unit
) {
    val currentSeconds by rememberUpdatedState(seconds)
    LaunchedEffect(paused) {
        while (!paused && currentSeconds > 0) {
            delay(1000L)
            onTick()
        }
    }

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    Text(
        text = "%02d:%02d:%02d".format(hours, minutes, secs),
        fontSize = 80.sp,
        fontFamily = FontFamily.Monospace,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}