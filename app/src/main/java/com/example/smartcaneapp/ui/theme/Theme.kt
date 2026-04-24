package com.example.smartcaneapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.smartcaneapp.R


/*@Composable
fun SmartCaneAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        },
        typography = Typography,
        content = content
    )
}*/

fun blendColors(foreground: Color, background: Color, alpha: Float): Color {
    val r = (alpha * foreground.red + (1 - alpha) * background.red)
    val g = (alpha * foreground.green + (1 - alpha) * background.green)
    val b = (alpha * foreground.blue + (1 - alpha) * background.blue)
    return Color(r, g, b, 1f) // Return fully opaque blended color
}
@Composable
fun SmartCaneAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+

    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            val backgroundColor = colorScheme.surface
            val blendedNavBarColor = blendColors(primaryContainer, backgroundColor, 0.16f)
            window.navigationBarColor = blendedNavBarColor.toArgb()
            window.statusBarColor = colorScheme.surface.toArgb()

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun FormalButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun DataCard(text: String, OnformalcardFunction:@Composable () -> Unit) {
    val font = FontFamily(Font(R.font.notosansmonocondensedregular)) // Ensure font is in res/font/

    Card (
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),


    ) {
        //Spacer(modifier = Modifier.height(8.dp))
        Text(
            "${text}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 8.dp),
            fontFamily = font,
            color = MaterialTheme.colorScheme.onSurface
        )

        OnformalcardFunction()
    }
}

@Composable
fun OnDataCard(content1: String, content2: String, color: Color = MaterialTheme.colorScheme.onPrimaryContainer) {
    val font = FontFamily(Font(R.font.notosansmonocondensedregular)) // Ensure font is in res/font/

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        //elevation = CardDefaults.elevatedCardElevation(8.dp),
        modifier = Modifier
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(
                text = content1,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 10.dp),
                fontFamily = font,
                color = MaterialTheme.colorScheme.onPrimaryContainer, // Uses Primary for a formal look
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content2,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 10.dp),
                color = color,
                fontFamily = font,
                //color = MaterialTheme.colorScheme.onPrimaryContainer, // Adds contrast
            )
        }
    }
}

@Composable
fun SettingsCard(text: String, SwitchFunction: @Composable () -> Unit) {
    val font = FontFamily(Font(R.font.juliettbold))

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        //elevation = CardDefaults.elevatedCardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = font,
                color = MaterialTheme.colorScheme.onPrimaryContainer, // Gives a subtle colorful accent
                modifier = Modifier.weight(1f)
            )
            SwitchFunction()
        }
    }
}
