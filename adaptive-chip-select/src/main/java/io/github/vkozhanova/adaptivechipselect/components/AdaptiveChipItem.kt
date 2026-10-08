package io.github.vkozhanova.adaptivechipselect.components

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

/**
 * Chip model for [AdaptiveChipSelectBlock] and [AdaptiveChipScroller].
 *
 * @param id stable identifier. Used as the key in [AdaptiveChipState]
 *           and as the cache key for X-positions in the scroller.
 *           Must be unique within a single list and must not change
 *           between recompositions.
 * @param title text shown on the chip.
 * @param iconResId drawable resource id for the leading icon.
 */
@Immutable
public data class AdaptiveChipItem(
    public val id: String,
    public val title: String,
    @DrawableRes public val iconResId: Int,
)