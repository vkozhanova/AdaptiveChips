package io.github.vkozhanova.adaptivechipselect.components

import androidx.compose.runtime.Immutable

/**
 * Информация о позиции скролла, публикуемая [AdaptiveChipScroller].
 * Используется индикатором [ChipPagerIndicator].
 *
 * @param currentGroup индекс текущей группы (0-based).
 * @param groupCount общее число групп.
 * @param maxScroll максимально возможное значение скролла в пикселях.
 */
@Immutable
public data class ScrollInfo(
    public val currentGroup: Int,
    public val groupCount: Int,
    public val maxScroll: Int,
)