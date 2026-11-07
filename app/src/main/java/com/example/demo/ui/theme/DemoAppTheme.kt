package com.example.demo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = Back,
    tertiary = Pink40,
    background = DustyBlush,
    surface = SoftIvory,
    onSurface = AntiqueRose,
    onTertiary = StormyBlue,
    onPrimary = DarkPlum,
    onBackground = ParchmentCream,
    onSecondary = InkNavi,
    onSecondaryContainer = BurgundyLeather,
    onPrimaryContainer = Oxblood,
    onTertiaryContainer = Wine,
    onSurfaceVariant = AntiqueGold,
    tertiaryContainer = Candlelight,
    secondaryContainer = NearBlack,
)

@Composable
fun DemoAppTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}