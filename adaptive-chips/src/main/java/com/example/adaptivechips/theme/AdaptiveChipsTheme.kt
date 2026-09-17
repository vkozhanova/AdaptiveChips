package com.example.adaptivechips.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class AdaptiveChipsColors(
    val selectedContainer: Color,
    val unselectedContainer: Color,
    val selectedBorder: Color,
    val checkBackground: Color,
    val checkBorder: Color,
    val checkIconTint: Color,
    val label: Color,
)

@Immutable
data class AdaptiveChipsDimens(
    val chipHeight: Dp = 48.dp,
    val chipCornerRadius: Dp = 24.dp,
    val chipHorizontalPadding: Dp = 15.dp,
    val chipContentSpacing: Dp = 6.dp,
    val iconSize: Dp = 24.dp,
    val checkSize: Dp = 23.dp,
    val checkBorderWidth: Dp = 2.dp,
    val checkIconSize: Dp = 9.dp,
    val checkOffsetX: Dp = 2.dp,
    val checkOffsetY: Dp = (-2).dp,
    val itemSpacing: Dp = 8.dp,
    val rowSpacing: Dp = 8.dp,
    val horizontalScreenPadding: Dp = 16.dp,
    val maxCollapsedHeight: Dp = 400.dp,
)

@Immutable
data class AdaptiveChipsTypography(
    val chipLabel: TextStyle,
    val sectionTitle: TextStyle,
)

object AdaptiveChipsDefaults {
    @Composable
    fun colors(
        selectedContainer: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f),
        unselectedContainer: Color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
        selectedBorder: Color = MaterialTheme.colorScheme.secondary,
        checkBackground: Color = MaterialTheme.colorScheme.secondary,
        checkBorder: Color = MaterialTheme.colorScheme.onPrimary,
        checkIconTint: Color = MaterialTheme.colorScheme.onPrimary,
        label: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
    ) = AdaptiveChipsColors(
        selectedContainer = selectedContainer,
        unselectedContainer = unselectedContainer,
        selectedBorder = selectedBorder,
        checkBackground = checkBackground,
        checkBorder = checkBorder,
        checkIconTint = checkIconTint,
        label = label,
    )

    fun dimens() = AdaptiveChipsDimens()

    @Composable
    fun typography(
        chipLabel: TextStyle = TextStyle(fontSize = 14.sp),
        sectionTitle: TextStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        ),
    ) = AdaptiveChipsTypography(
        chipLabel = chipLabel,
        sectionTitle = sectionTitle,
    )
}

val LocalAdaptiveChipColors = staticCompositionLocalOf<AdaptiveChipsColors> {
    error("AdaptiveChipsTheme is not provided")
}

val LocalAdaptiveChipsDimens = staticCompositionLocalOf<AdaptiveChipsDimens> {
    error("AdaptiveChipsTheme is not provided")
}

val LocalAdaptiveChipsTypography = staticCompositionLocalOf<AdaptiveChipsTypography> {
    error("AdaptiveChipsTheme is not provided")
}

object AdaptiveChipsTheme {
    val colors: AdaptiveChipsColors
        @Composable get() = LocalAdaptiveChipColors.current
    val dimens: AdaptiveChipsDimens
        @Composable get() = LocalAdaptiveChipsDimens.current
    val typography: AdaptiveChipsTypography
        @Composable get() = LocalAdaptiveChipsTypography.current
}

@Composable
fun AdaptiveChipsTheme(
    colors: AdaptiveChipsColors = AdaptiveChipsDefaults.colors(),
    dimens: AdaptiveChipsDimens = AdaptiveChipsDefaults.dimens(),
    typography: AdaptiveChipsTypography = AdaptiveChipsDefaults.typography(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAdaptiveChipColors provides colors,
        LocalAdaptiveChipsDimens provides dimens,
        LocalAdaptiveChipsTypography provides typography,
        content = content,
    )
}