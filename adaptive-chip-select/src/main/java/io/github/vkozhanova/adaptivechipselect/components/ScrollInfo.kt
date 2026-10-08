package io.github.vkozhanova.adaptivechipselect.components

import androidx.compose.runtime.Immutable

/**
 * Scroll position information published by [AdaptiveChipScroller].
 * Consumed by [ChipPagerIndicator].
 *
 * @param currentGroup index of the current group (0-based).
 * @param groupCount total number of groups.
 * @param maxScroll maximum scroll value in pixels.
 */
@Immutable
public data class ScrollInfo(
    public val currentGroup: Int,
    public val groupCount: Int,
    public val maxScroll: Int,
)