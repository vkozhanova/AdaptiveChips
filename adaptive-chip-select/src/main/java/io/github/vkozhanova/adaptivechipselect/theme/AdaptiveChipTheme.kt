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
 * Цвета, которые использует [AdaptiveChip].
 *
 * Не путать с [AdaptiveChipsColors] — та отвечает за контейнер
 * (карточку, разделители, индикатор).
 *
 * Получить значения:
 * - [AdaptiveChipDefaults.colors] — нейтральные baseline-цвета,
 *   не зависящие от `MaterialTheme`;
 * - [AdaptiveChipDefaults.materialColors] — производные от текущей
 *   `MaterialTheme.colorScheme` (вызывать внутри `MaterialTheme`).
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
 * Размеры и отступы [AdaptiveChip]: высота, скругление, отступы под иконку,
 * размер галочки выделения и её смещения.
 *
 * Значения по умолчанию подобраны так, чтобы чип смотрелся в одном ряду
 * с Material 3 FilterChip. Меняйте только если нужен другой визуальный язык.
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
 * Типографика [AdaptiveChip]. Сейчас содержит только стиль подписи —
 * на чипе больше нет текстовых элементов.
 */

@Immutable
public data class AdaptiveChipTypography(
    val label: TextStyle,
)

/**
 * Точки входа для дефолтных значений темы чипа.
 *
 * - [colors] / [dimens] / [typography] — baseline, не зависят от Material;
 * - [materialColors] — цвета, выведенные из `MaterialTheme.colorScheme`;
 *   обязательно вызывать внутри `MaterialTheme`.
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
 * Доступ к текущим значениям темы чипа из любого `@Composable`.
 * Значения берутся из ближайшего [AdaptiveChipTheme] в дереве композиции.
 *
 * Используйте:
 * ```
 * val dimens = AdaptiveChipTheme.dimens
 * val colors = AdaptiveChipTheme.colors
 * ```
 *
 * Если [AdaptiveChipTheme] не обёрнут вокруг — рантайм-ошибка
 * «AdaptiveChipTheme is not provided».
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
 * Провайдер темы для [AdaptiveChip] и других чип-специфичных компонентов.
 *
 * Оборачивайте дерево, если используете [AdaptiveChip] **отдельно** от
 * [AdaptiveChipsTheme]. Когда используете [AdaptiveChipSelectBlock]
 * или [AdaptiveChipScroller], достаточно [AdaptiveChipsTheme] — он
 * прокидывает и chip-тему тоже.
 *
 * @param colors цвета чипа
 * @param dimens размеры чипа
 * @param typography типографика чипа
 * @param content содержимое, к которому применяется тема
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