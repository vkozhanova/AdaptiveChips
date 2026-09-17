package com.example.adaptivechips

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adaptivechips.components.NoteItem
import com.example.adaptivechips.components.NoteItemData
import com.example.adaptivechips.theme.AdaptiveChipsTheme
import java.util.Collections.emptyList
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SynchronizedChipScroller(
    items: List<NoteItemData>,
    onItemSelectionChanged: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    rowCount: Int = 2,
    onScrollInfo: (ScrollInfo) -> Unit = {},
) {
    val dimens = AdaptiveChipsTheme.dimens
    val typography = AdaptiveChipsTheme.typography
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val scrollState = rememberScrollState()

    val layout: ChipLayout<NoteItemData> = remember(items, rowCount, density, typography) {
        val horizontalPaddingPx = with(density) { dimens.chipHorizontalPadding.roundToPx() }
        val iconSizePx = with(density) { dimens.iconSize.roundToPx() }
        val contentSpacingPx = with(density) { dimens.chipContentSpacing.roundToPx() }
        val spacingPx = with(density) { dimens.itemSpacing.roundToPx() }
        val checkOverhangPx = with(density) { 2.dp.roundToPx() }

        layoutChips(
            items = items,
            rowCount = rowCount,
            chipWidthPx = { item: NoteItemData ->
                measureChipWidthPx(
                    title = item.title,
                    textMeasurer = textMeasurer,
                    textStyle = typography.chipLabel,
                    leadingIconSizePx = iconSizePx,
                    horizontalPaddingPx = horizontalPaddingPx,
                    contentSpacingPx = contentSpacingPx,
                    checkOverhangPx = checkOverhangPx,
                )
            },
            spacingPx = spacingPx,
        )
    }
    val reachableTargets: List<Int> by remember {
        derivedStateOf {
            val max = scrollState.maxValue
            if(max <= 0) return@derivedStateOf emptyList()
            val reachable = layout.snapTargets.filter { it in 0..max }
            if (reachable.lastOrNull() != max) reachable + max else reachable
        }
    }
    // Магнитный снап: после остановки скролла подтягиваемся к ближайшей цели.
    LaunchedEffect(scrollState, layout.snapTargets) {
        snapshotFlow { scrollState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling) return@collect

                val  max = scrollState.maxValue
                val current = scrollState.value

                if(current <= 2) return@collect
                if (current >= max - 2) {
                if (abs(max - current) < 2) return@collect
                scrollState.animateScrollTo(
                    value = max,
                    animationSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioNoBouncy,
                    ),
                )
                    return@collect
            }
                val reachable = layout.snapTargets.filter { it <= max }
                val  target = reachable.minByOrNull { abs(it - current)} ?: return@collect
                if (abs(target - current) < 2) return@collect
                scrollState.animateScrollTo(
                    value = target,
                    animationSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioNoBouncy,
                    ),
                )
            }
    }
    // Публикуем ScrollInfo на основе reachableTargets.
    LaunchedEffect(scrollState, reachableTargets) {
        snapshotFlow { scrollState.value }
            .collect { value ->
                val targets = reachableTargets
                val pairCount = targets.size
                val pair = if (targets.isEmpty()) 0
                else targets.indexOfLast { it <= value }.coerceAtLeast(0)
                onScrollInfo(
                    ScrollInfo(
                        currentPair = pair,
                        pairCount = pairCount,
                        maxScroll = scrollState.maxValue,
                    ),
                )
            }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(dimens.rowSpacing)) {
            layout.rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(dimens.itemSpacing)) {
                    row.forEach { laid ->
                        Box(
                            modifier = Modifier.width(
                                with(density) { laid.width.toDp() },
                            ),
                        ) {
                            NoteItem(
                                title = laid.item.title,
                                iconResId = laid.item.iconResId,
                                isSelected = laid.item.isSelected,
                                onSelectionChange = { isSelected ->
                                    onItemSelectionChanged(laid.item.id, isSelected)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

data class ScrollInfo(
    val currentPair: Int,
    val pairCount: Int,
    val maxScroll: Int,
)

@Preview(showBackground = true)
@Composable
private fun SynchronizedChipScrollerPreview() {
    AdaptiveChipsTheme {
        SynchronizedChipScroller(
            items = listOf(
                NoteItemData("1", "Головокружение", R.drawable.ic_chip_placeholder, true),
                NoteItemData("2", "Головная боль", R.drawable.ic_chip_placeholder, false),
                NoteItemData("3", "Тошнота", R.drawable.ic_chip_placeholder, true),
                NoteItemData("4", "Слабость", R.drawable.ic_chip_placeholder, false),
                NoteItemData("5", "Температура", R.drawable.ic_chip_placeholder, false),
                NoteItemData("6", "Кашель", R.drawable.ic_chip_placeholder, true),
                NoteItemData("7", "Боль в груди", R.drawable.ic_chip_placeholder, false),
                NoteItemData("8", "Одышка", R.drawable.ic_chip_placeholder, false),
            ),
            onItemSelectionChanged = { _, _ -> },
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}