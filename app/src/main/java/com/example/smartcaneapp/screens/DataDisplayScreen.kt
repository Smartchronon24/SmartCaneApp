package com.example.smartcaneapp.screens

import android.graphics.Color
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.smartcaneapp.DataViewModel
import com.example.smartcaneapp.R
import com.example.smartcaneapp.ui.theme.DataCard
import com.example.smartcaneapp.ui.theme.OnDataCard

@Composable
fun DataDisplayScreen(viewModel: DataViewModel) {
    val alpha = remember { Animatable(0f) }
    val context = LocalContext.current
    val font = FontFamily(
        Font(R.font.notosansmonocondensedregular)  // Ensure the file is in res/font/
    )

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(durationMillis = 1000, easing = LinearEasing))
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha.value),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            DataCard("Hardware"){
                Row (
                   horizontalArrangement = Arrangement.SpaceBetween
                ){
                    OnDataCard(
                        content1 = "Device",
                        content2 = viewModel.getstatus(viewModel.espstatus.value),
                        color = if (viewModel.espstatus.value == true)
                            MaterialTheme.colorScheme.onError
                        else
                            MaterialTheme.colorScheme.error

                    )
/*                    OnDataCard(
                        content1 = "Fall",
                        content2 = viewModel.getstatus(viewModel.gyrostatus.value),
                        color = if (viewModel.gyrostatus.value == true)
                            MaterialTheme.colorScheme.onError
                        else
                            MaterialTheme.colorScheme.error
                    )*/

                }

/*                Row(horizontalArrangement = Arrangement.SpaceBetween){
                    OnDataCard(
                        content1 = "GPS",
                        content2 = viewModel.getstatus(viewModel.gpsstatus.value),
                        color = if (viewModel.gpsstatus.value == true)
                            MaterialTheme.colorScheme.onError
                        else
                            MaterialTheme.colorScheme.error

                    )

                    OnDataCard(
                        content1 = "UltraSonic",
                        content2 = viewModel.getstatus(viewModel.ultrasonicstatus.value),
                        color = if (viewModel.ultrasonicstatus.value == true)
                            MaterialTheme.colorScheme.onError
                        else
                            MaterialTheme.colorScheme.error

                    )
                }*/
            }
        }
    }
}
