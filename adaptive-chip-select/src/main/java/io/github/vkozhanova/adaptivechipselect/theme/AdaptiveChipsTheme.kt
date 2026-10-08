package io.github.vkozhanova.adaptivechipselect.theme

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
import io.github.vkozhanova.adaptivechipselect.components.AdaptiveChip
import io.github.vkozhanova.adaptivechipselect.components.AdaptiveChipSelectBlock
import io.github.vkozhanova.adaptivechipselect.components.CollapsedChipGrid
import io.github.vkozhanova.adaptivechipselect.components.ChipPagerIndicator

/**
 * Colors used by container components — [AdaptiveChipSelectBlock],
 * [CollapsedChipGrid], [ChipPagerIndicator], and dividers.
 *
 * Not to be confused with [AdaptiveChipColors] — that one is for the chip itself.
 *
 * Where to get the values:
 * - [AdaptiveChipsDefaults.colors] — neutral baseline.
 * - [AdaptiveChipsDefaults.materialColors] — derived from the current
 *   `MaterialTheme.colorScheme`; must be called inside `MaterialTheme`.
 */

@Immutable
public data class AdaptiveChipsColors(
    val cardBackground: Color,
    val divider: Color,
    val editIconTint: Color,
    val expandCollapseTint: Color,
    val indicatorActive: Color,
    val indicatorInactive: Color,
    val indicatorHint: Color,
)

/**
 * Container dimensions and spacing: gaps between chips, between rows,
 * horizontal screen padding, and the maximum height of the expanded mode.
 *
 * Not to be confused with [AdaptiveChipDimens] — that one is for the chip itself.
 */

@Immutable
public data class AdaptiveChipsDimens(
    val itemSpacing: Dp = 8.dp,
    val rowSpacing: Dp = 8.dp,
    val horizontalScreenPadding: Dp = 16.dp,
    val maxCollapsedHeight: Dp = 400.dp,
)

/**
 * Container typography. Currently only the card title style — the text
 * passed as `title` to [AdaptiveChipSelectBlock].
 */

@Immutable
public data class AdaptiveChipsTypography(
    val sectionTitle: TextStyle,
)

/**
 * Entry points for the default container theme values.
 *
 * - [colors] / [dimens] / [typography] — baseline, independent of Material;
 * - [materialColors] — colors derived from `MaterialTheme.colorScheme`;
 *   must be called inside `MaterialTheme`.
 */

public object AdaptiveChipsDefaults {

    public fun colors(): AdaptiveChipsColors = BaselineColors

    @Composable
    public fun materialColors(): AdaptiveChipsColors {
        val scheme = MaterialTheme.colorScheme
        return AdaptiveChipsColors(
            cardBackground = scheme.surface,
            divider = scheme.surfaceVariant,
            editIconTint = scheme.secondary,
            expandCollapseTint = scheme.onSurface,
            indicatorActive = scheme.primary,
            indicatorInactive = scheme.onSurface.copy(alpha = 0.2f),
            indicatorHint = scheme.onSurface.copy(alpha = 0.1f),
        )
    }

    public fun dimens(): AdaptiveChipsDimens = AdaptiveChipsDimens()

    public fun typography(): AdaptiveChipsTypography = AdaptiveChipsTypography(
        sectionTitle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        ),
    )
}

private val BaselineColors = AdaptiveChipsColors(
    cardBackground = Color(0xFFFFFBFE),
    divider = Color(0xFFE7E0EC),
    editIconTint = Color(0xFF625B71),
    expandCollapseTint = Color(0xFF1C1B1F),
    indicatorActive = Color(0xFF6750A4),
    indicatorInactive = Color(0xFF1C1B1F).copy(alpha = 0.2f),
    indicatorHint = Color(0xFF1C1B1F).copy(alpha = 0.1f),
)

internal val LocalAdaptiveChipsColors = staticCompositionLocalOf<AdaptiveChipsColors> {
    error("AdaptiveChipsTheme is not provided")
}
internal val LocalAdaptiveChipsDimens = staticCompositionLocalOf<AdaptiveChipsDimens> {
    error("AdaptiveChipsTheme is not provided")
}
internal val LocalAdaptiveChipsTypography = staticCompositionLocalOf<AdaptiveChipsTypography> {
    error("AdaptiveChipsTheme is not provided")
}

/**
 * Access to the current container theme values from any `@Composable`.
 * Values come from the nearest [AdaptiveChipsTheme] in the composition tree.
 *
 * Usage:
 * ```
 * val dimens = AdaptiveChipsTheme.dimens
 * val colors = AdaptiveChipsTheme.colors
 * ```
 *
 * If [AdaptiveChipsTheme] is not wrapped around, a runtime error
 * "AdaptiveChipsTheme is not provided" is thrown.
 */

public object AdaptiveChipsTheme {
    public val colors: AdaptiveChipsColors
        @Composable get() = LocalAdaptiveChipsColors.current
    public val dimens: AdaptiveChipsDimens
        @Composable get() = LocalAdaptiveChipsDimens.current
    public val typography: AdaptiveChipsTypography
        @Composable get() = LocalAdaptiveChipsTypography.current
}

/**
 * Single entry point for configuring the library theme.
 *
 * Provides **both** levels at once:
 * - container theme ([AdaptiveChipsColors], [AdaptiveChipsDimens],
 *   [AdaptiveChipsTypography]) — for [AdaptiveChipSelectBlock],
 *   [CollapsedChipGrid], [ChipPagerIndicator];
 * - chip theme ([AdaptiveChipColors], [AdaptiveChipDimens],
 *   [AdaptiveChipTypography]) — for [AdaptiveChip].
 *
 * If only one level is needed, use [AdaptiveChipTheme] separately.
 *
 * All parameters default to baseline values. To bind to Material 3, pass
 * `materialColors()` from [AdaptiveChipsDefaults] and [AdaptiveChipDefaults]:
 * ```
 * AdaptiveChipsTheme(
 *     colors = AdaptiveChipsDefaults.materialColors(),
 *     chipColors = AdaptiveChipDefaults.materialColors(),
 * ) {
 *     // ...
 * }
 * ```
 *
 * @param colors container colors
 * @param dimens container dimensions
 * @param typography container typography
 * @param chipColors chip colors
 * @param chipDimens chip dimensions
 * @param chipTypography chip typography
 * @param content content to which the theme is applied
 */

@Composable
public fun AdaptiveChipsTheme(
    colors: AdaptiveChipsColors = AdaptiveChipsDefaults.colors(),
    dimens: AdaptiveChipsDimens = AdaptiveChipsDefaults.dimens(),
    typography: AdaptiveChipsTypography = AdaptiveChipsDefaults.typography(),
    chipColors: AdaptiveChipColors = AdaptiveChipDefaults.colors(),
    chipDimens: AdaptiveChipDimens = AdaptiveChipDefaults.dimens(),
    chipTypography: AdaptiveChipTypography = AdaptiveChipDefaults.typography(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAdaptiveChipsColors provides colors,
        LocalAdaptiveChipsDimens provides dimens,
        LocalAdaptiveChipsTypography provides typography,
        LocalAdaptiveChipColors provides chipColors,
        LocalAdaptiveChipDimens provides chipDimens,
        LocalAdaptiveChipTypography provides chipTypography,
        content = content,
    )
}