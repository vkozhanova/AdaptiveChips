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
 * Цвета, которые используют контейнерные компоненты — [AdaptiveChipSelectBlock],
 * [CollapsedChipGrid], [ChipPagerIndicator] и разделители.
 *
 * Не путать с [AdaptiveChipColors] — та отвечает за сам чип.
 *
 * Получить значения:
 * - [AdaptiveChipsDefaults.colors] — нейтральные baseline;
 * - [AdaptiveChipsDefaults.materialColors] — из текущей `MaterialTheme.colorScheme`.
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
 * Размеры и отступы контейнера: промежутки между чипами, между рядами,
 * отступ от края экрана и максимальная высота развёрнутого режима.
 *
 * Не путать с [AdaptiveChipDimens] — там размеры самого чипа.
 */

@Immutable
public data class AdaptiveChipsDimens(
    val itemSpacing: Dp = 8.dp,
    val rowSpacing: Dp = 8.dp,
    val horizontalScreenPadding: Dp = 16.dp,
    val maxCollapsedHeight: Dp = 400.dp,
)

/**
 * Типографика контейнера. Сейчас содержит стиль заголовка карточки —
 * того, что передаётся в `title` у [AdaptiveChipSelectBlock].
 */

@Immutable
public data class AdaptiveChipsTypography(
    val sectionTitle: TextStyle,
)

/**
 * Точки входа для дефолтных значений темы контейнера.
 *
 * - [colors] / [dimens] / [typography] — baseline, не зависят от Material;
 * - [materialColors] — цвета, выведенные из `MaterialTheme.colorScheme`;
 *   обязательно вызывать внутри `MaterialTheme`.
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
 * Доступ к текущим значениям темы контейнера из любого `@Composable`.
 * Значения берутся из ближайшего [AdaptiveChipsTheme] в дереве композиции.
 *
 * Используйте:
 * ```
 * val dimens = AdaptiveChipsTheme.dimens
 * val colors = AdaptiveChipsTheme.colors
 * ```
 *
 * Если [AdaptiveChipsTheme] не обёрнут вокруг — рантайм-ошибка
 * «AdaptiveChipsTheme is not provided».
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
 * Единая точка входа для настройки темы библиотеки.
 *
 * Прокидывает **оба** уровня сразу:
 * - container-тему ([AdaptiveChipsColors], [AdaptiveChipsDimens],
 *   [AdaptiveChipsTypography]) — для [AdaptiveChipSelectBlock],
 *   [CollapsedChipGrid], [ChipPagerIndicator];
 * - chip-тему ([AdaptiveChipColors], [AdaptiveChipDimens],
 *   [AdaptiveChipTypography]) — для [AdaptiveChip].
 *
 * Если нужен только один уровень — используйте [AdaptiveChipTheme]
 * отдельно.
 *
 * Все параметры по умолчанию — baseline-значения. Для связки с Material 3
 * передайте `materialColors()` из [AdaptiveChipsDefaults] и
 * [AdaptiveChipDefaults]:
 * ```
 * AdaptiveChipsTheme(
 *     colors = AdaptiveChipsDefaults.materialColors(),
 *     chipColors = AdaptiveChipDefaults.materialColors(),
 * ) {
 *     // ...
 * }
 * ```
 *
 * @param colors цвета контейнера
 * @param dimens размеры контейнера
 * @param typography типографика контейнера
 * @param chipColors цвета чипа
 * @param chipDimens размеры чипа
 * @param chipTypography типографика чипа
 * @param content содержимое, к которому применяется тема
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