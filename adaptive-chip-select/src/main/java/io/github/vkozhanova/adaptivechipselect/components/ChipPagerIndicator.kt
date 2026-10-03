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
 * Индикатор позиции скролла: ряд точек, где активная соответствует
 * текущей группе чипов.
 *
 * При большом числе групп ([groupCount] > [maxVisibleDots]) показывается
 * не всё, а **скользящее окно** из [maxVisibleDots] точек, центрированное
 * на текущей позиции и прижимающееся к краям. Крайняя правая точка окна
 * уменьшена, пока пользователь не доскроллил до конца — это визуальная
 * подсказка, что контент продолжается.
 *
 * Если [groupCount] ≤ 1, компонент ничего не рисует.
 *
 * Обычно используется вместе с [AdaptiveChipScroller] через [ScrollInfo]:
 * скроллер публикует `ScrollInfo`, а индикатор получает из него
 * `groupCount` и `currentGroup`.
 *
 * @param groupCount общее число групп чипов (точек на полном индикаторе)
 * @param currentGroup индекс активной группы (0-based)
 * @param modifier модификатор корневого Row
 * @param maxVisibleDots максимальное число одновременно видимых точек;
 *        при превышении включается скользящее окно
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