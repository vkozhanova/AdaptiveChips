package io.github.vkozhanova.adaptivechipselect.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.vkozhanova.adaptivechipselect.components.AdaptiveChip
import io.github.vkozhanova.adaptivechipselect.components.AdaptiveChipSelectBlock
import io.github.vkozhanova.adaptivechipselect.components.AdaptiveChipScroller

/**
 * Colors used by [AdaptiveChip].
 *
 * Not to be confused with [AdaptiveChipsColors] — that one is for the container
 * (card, dividers, indicator).
 *
 * Where to get the values:
 * - [AdaptiveChipDefaults.colors] — neutral baseline colors independent
 *   of `MaterialTheme`.
 * - [AdaptiveChipDefaults.materialColors] — derived from the current
 *   `MaterialTheme.colorScheme`; must be called inside `MaterialTheme`.
 */

@Immutable
public data class AdaptiveChipColors(
    val selectedContainer: Color,
    val unselectedContainer: Color,
    val selectedBorder: Color,
    val checkBackground: Color,
    val checkBorder: Color,
    val checkIconTint: Color,
    val label: Color,
)

/**
 * Dimensions and spacing of [AdaptiveChip]: height, corner radius, icon
 * padding, selection checkmark size and offsets.
 *
 * Defaults are tuned so the chip matches the visual rhythm of a Material 3
 * `FilterChip`. Change only if you want a different visual language.
 */

@Immutable
public data class AdaptiveChipDimens(
    val height: Dp = 48.dp,
    val cornerRadius: Dp = 24.dp,
    val horizontalPadding: Dp = 15.dp,
    val contentSpacing: Dp = 6.dp,
    val iconSize: Dp = 24.dp,
    val checkSize: Dp = 23.dp,
    val checkBorderWidth: Dp = 2.dp,
    val checkIconSize: Dp = 9.dp,
    val checkOffsetX: Dp = 2.dp,
    val checkOffsetY: Dp = (-2).dp,
)

/**
 * Typography of [AdaptiveChip]. Currently only the label style — there are
 * no other text elements on the chip.
 */

@Immutable
public data class AdaptiveChipTypography(
    val label: TextStyle,
)

/**
 * Entry points for the default chip theme values.
 *
 * - [colors] / [dimens] / [typography] — baseline, independent of Material;
 * - [materialColors] — colors derived from `MaterialTheme.colorScheme`;
 *   must be called inside `MaterialTheme`.
 */

public object AdaptiveChipDefaults {

    public fun colors(): AdaptiveChipColors = BaselineChipColors

    @Composable
    public fun materialColors(): AdaptiveChipColors {
        val scheme = MaterialTheme.colorScheme
        return AdaptiveChipColors(
            selectedContainer = scheme.primary.copy(alpha = 0.12f),
            unselectedContainer = scheme.secondary.copy(alpha = 0.15f),
            selectedBorder = scheme.secondary,
            checkBackground = scheme.secondary,
            checkBorder = scheme.onPrimary,
            checkIconTint = scheme.onPrimary,
            label = scheme.onSurface.copy(alpha = 0.75f),
        )
    }

    public fun dimens(): AdaptiveChipDimens = AdaptiveChipDimens()

    public fun typography(): AdaptiveChipTypography = AdaptiveChipTypography(
        label = TextStyle(fontSize = 14.sp),
    )
}

private val BaselineChipColors = AdaptiveChipColors(
    selectedContainer = Color(0xFF6750A4).copy(alpha = 0.12f),
    unselectedContainer = Color(0xFF625B71).copy(alpha = 0.15f),
    selectedBorder = Color(0xFF625B71),
    checkBackground = Color(0xFF625B71),
    checkBorder = Color(0xFFFFFFFF),
    checkIconTint = Color(0xFFFFFFFF),
    label = Color(0xFF1C1B1F).copy(alpha = 0.75f),
)

internal val LocalAdaptiveChipColors = staticCompositionLocalOf<AdaptiveChipColors> {
    error("AdaptiveChipTheme is not provided")
}
internal val LocalAdaptiveChipDimens = staticCompositionLocalOf<AdaptiveChipDimens> {
    error("AdaptiveChipTheme is not provided")
}
internal val LocalAdaptiveChipTypography = staticCompositionLocalOf<AdaptiveChipTypography> {
    error("AdaptiveChipTheme is not provided")
}

/**
 * Access to the current chip theme values from any `@Composable`.
 * Values come from the nearest [AdaptiveChipTheme] in the composition tree.
 *
 * Usage:
 * ```
 * val dimens = AdaptiveChipTheme.dimens
 * val colors = AdaptiveChipTheme.colors
 * ```
 *
 * If [AdaptiveChipTheme] is not wrapped around, a runtime error
 * "AdaptiveChipTheme is not provided" is thrown.
 */

public object AdaptiveChipTheme {
    public val colors: AdaptiveChipColors
        @Composable get() = LocalAdaptiveChipColors.current
    public val dimens: AdaptiveChipDimens
        @Composable get() = LocalAdaptiveChipDimens.current
    public val typography: AdaptiveChipTypography
        @Composable get() = LocalAdaptiveChipTypography.current
}

/**
 * Theme provider for [AdaptiveChip] and other chip-specific components.
 *
 * Wrap your tree if you use [AdaptiveChip] **standalone**, outside of
 * [AdaptiveChipsTheme]. When you use [AdaptiveChipSelectBlock] or
 * [AdaptiveChipScroller], [AdaptiveChipsTheme] is enough — it also provides
 * the chip theme.
 *
 * @param colors chip colors
 * @param dimens chip dimensions
 * @param typography chip typography
 * @param content content to which the theme is applied
 */

@Composable
public fun AdaptiveChipTheme(
    colors: AdaptiveChipColors = AdaptiveChipDefaults.colors(),
    dimens: AdaptiveChipDimens = AdaptiveChipDefaults.dimens(),
    typography: AdaptiveChipTypography = AdaptiveChipDefaults.typography(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAdaptiveChipColors provides colors,
        LocalAdaptiveChipDimens provides dimens,
        LocalAdaptiveChipTypography provides typography,
        content = content,
    )
}