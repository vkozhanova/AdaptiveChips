package com.example.adaptivechips.components

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
import com.example.adaptivechips.theme.AdaptiveChipsTheme

@Composable
public fun ChipPagerIndicator(
    pairCount: Int,
    currentPair: Int,
    modifier: Modifier = Modifier,
    maxVisibleDots: Int = 7,
) {
    if (pairCount <= 1) return

    val primary = AdaptiveChipsTheme.colors.indicatorActive
    val inactive = AdaptiveChipsTheme.colors.indicatorInactive
    val hint = AdaptiveChipsTheme.colors.indicatorHint

    val window: List<Int> = remember(pairCount, currentPair, maxVisibleDots) {
        buildWindow(pairCount, currentPair, maxVisibleDots)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        window.forEachIndexed { visibleIndex, actualIndex ->
            val isActive = actualIndex == currentPair
            val isRightmostVisible = visibleIndex == window.lastIndex
            val hasMoreToTheRight = actualIndex < pairCount - 1
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
    pairCount: Int,
    currentPair: Int,
    maxVisibleDots: Int,
): List<Int> {
    if (pairCount <= maxVisibleDots) return (0 until pairCount).toList()
    val half = maxVisibleDots / 2
    val start = (currentPair - half).coerceIn(0, pairCount - maxVisibleDots)
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
            ChipPagerIndicator(pairCount = 3, currentPair = 0)
            ChipPagerIndicator(pairCount = 3, currentPair = 2)
            ChipPagerIndicator(pairCount = 15, currentPair = 0)
            ChipPagerIndicator(pairCount = 15, currentPair = 7)
            ChipPagerIndicator(pairCount = 15, currentPair = 14)
        }
    }
}