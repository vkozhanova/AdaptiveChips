package io.github.vkozhanova.adaptivechipselect.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vkozhanova.adaptivechipselect.theme.AdaptiveChipsTheme

/**
 * Scroll position indicator: a row of dots where the active one corresponds
 * to the current chip group.
 *
 * When there are many groups ([groupCount] > [maxVisibleDots]), not all dots
 * are shown — instead a **sliding window** of [maxVisibleDots] dots is used,
 * centered on the current position and clamped to the edges. The rightmost
 * dot in the window is smaller until the user reaches the end — a visual hint
 * that content continues.
 *
 * When [groupCount] ≤ 1, the component renders nothing.
 *
 * Usually used together with [AdaptiveChipScroller] via [ScrollInfo]:
 * the scroller publishes `ScrollInfo`, and the indicator extracts
 * `groupCount` and `currentGroup` from it.
 *
 * @param groupCount total number of chip groups (dots on a full indicator)
 * @param currentGroup index of the active group (0-based)
 * @param modifier modifier for the root Row
 * @param maxVisibleDots maximum number of simultaneously visible dots;
 *        beyond that, the sliding window kicks in
 *
 * @see ScrollInfo
 */

@Composable
public fun ChipPagerIndicator(
    groupCount: Int,
    currentGroup: Int,
    modifier: Modifier = Modifier,
    maxVisibleDots: Int = 7,
) {
    if (groupCount <= 1) return

    val primary = AdaptiveChipsTheme.colors.indicatorActive
    val inactive = AdaptiveChipsTheme.colors.indicatorInactive
    val hint = AdaptiveChipsTheme.colors.indicatorHint

    val window: List<Int> = remember(groupCount, currentGroup, maxVisibleDots) {
        buildWindow(groupCount, currentGroup, maxVisibleDots)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        window.forEachIndexed { visibleIndex, actualIndex ->
            val isActive = actualIndex == currentGroup
            val isRightmostVisible = visibleIndex == window.lastIndex
            val hasMoreToTheRight = actualIndex < groupCount - 1
            val isEdgeHint = isRightmostVisible && hasMoreToTheRight && !isActive

            val size by animateDpAsState(
                targetValue = when {
                    isActive -> 8.dp
                    isEdgeHint -> 4.dp
                    else -> 6.dp
                },
                label = "dotSize",
            )
            val color = when {
                isActive -> primary
                isEdgeHint -> hint
                else -> inactive
            }

            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(color),
            )
            if (visibleIndex < window.lastIndex) Spacer(Modifier.width(6.dp))
        }
    }
}

private fun buildWindow(
    groupCount: Int,
    currentGroup: Int,
    maxVisibleDots: Int,
): List<Int> {
    if (groupCount <= maxVisibleDots) return (0 until groupCount).toList()
    val half = maxVisibleDots / 2
    val start = (currentGroup - half).coerceIn(0, groupCount - maxVisibleDots)
    return (start until start + maxVisibleDots).toList()
}

@Preview(showBackground = true)
@Composable
private fun ChipPagerIndicatorPreview() {
    AdaptiveChipsTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ChipPagerIndicator(groupCount = 3, currentGroup = 0)
            ChipPagerIndicator(groupCount = 3, currentGroup = 2)
            ChipPagerIndicator(groupCount = 15, currentGroup = 0)
            ChipPagerIndicator(groupCount = 15, currentGroup = 7)
            ChipPagerIndicator(groupCount = 15, currentGroup = 14)
        }
    }
}