package io.github.vkozhanova.adaptivechipselect.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vkozhanova.adaptivechipselect.R
import io.github.vkozhanova.adaptivechipselect.theme.AdaptiveChipsTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.Collections.emptyList
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Синхронный скроллер чипов в несколько рядов.
 *
 * Не знает, как выглядит чип: рендер делегируется в [chip].
 * Позиции для магнитного снапа берутся из реального layout верхнего ряда.
 *
 * @param items элементы
 * @param state состояние выделения
 * @param key стабильный ключ элемента (используется как id в state и как ключ кэша позиций)
 * @param rowCount сколько рядов
 * @param onScrollInfo публикует информацию о скролле — для индикатора
 * @param chip слот: потребитель сам решает, как отрисовать чип
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun <T> AdaptiveChipScroller(
    items: List<T>,
    state: AdaptiveChipState,
    key: (T) -> String,
    modifier: Modifier = Modifier,
    rowCount: Int = 2,
    onScrollInfo: (ScrollInfo) -> Unit = {},
    chip: @Composable (
        item: T,
        isSelected: Boolean,
        onToggle: () -> Unit,
    ) -> Unit,
) {
    val dimens = AdaptiveChipsTheme.dimens
    val scrollState = rememberScrollState()

    val chipX = remember { mutableStateMapOf<String, Int>() }

    val rows: List<List<T>> = remember(items, rowCount) {
        List(rowCount) { r -> items.filterIndexed { i, _ -> i % rowCount == r } }
    }

    val topRowKeys: List<String> = remember(rows, key) {
        rows.firstOrNull()?.map(key).orEmpty()
    }

    val rawSnapTargets: List<Int> by remember {
        derivedStateOf {
            topRowKeys.mapNotNull { chipX[it] }.sorted()
        }
    }

    val reachableTargets: List<Int> by remember {
        derivedStateOf {
            val max = scrollState.maxValue
            if (max <= 0) return@derivedStateOf emptyList()
            val reachable = rawSnapTargets.filter { it in 0..max }
            if (reachable.lastOrNull() != max) reachable + max else reachable
        }
    }

    LaunchedEffect(scrollState, rawSnapTargets) {
        snapshotFlow { scrollState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling) return@collect

                val max = scrollState.maxValue
                val current = scrollState.value

                if (current <= 2) return@collect
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
                val reachable = rawSnapTargets.filter { it <= max }
                val target = reachable.minByOrNull { abs(it - current) } ?: return@collect
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

    LaunchedEffect(scrollState, reachableTargets) {
        snapshotFlow {
            val targets = reachableTargets
            val value = scrollState.value
            val max = scrollState.maxValue
            val groupCount = targets.size
            val group = if (targets.isEmpty()) 0
            else targets.indexOfLast { it <= value }.coerceAtLeast(0)
            ScrollInfo(
                currentGroup = group,
                groupCount = groupCount,
                maxScroll = max,
            )
        }
            .distinctUntilChanged()
            .collect { onScrollInfo(it) }

    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = dimens.horizontalScreenPadding),
            verticalArrangement = Arrangement.spacedBy(dimens.rowSpacing),
        ) {
            rows.forEachIndexed { rowIndex, rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(dimens.itemSpacing)) {
                    rowItems.forEach { item ->
                        val itemKey = key(item)
                        Box(
                            modifier = Modifier.onGloballyPositioned { coords ->
                                if (rowIndex == 0) {
                                    val x = coords.positionInParent().x.roundToInt()
                                    if (chipX[itemKey] != x) chipX[itemKey] = x
                                }
                            },
                        ) {
                            chip(
                                item,
                                state.isSelected(itemKey),
                                { state.toggle(itemKey) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Удобный overload для стандартного [AdaptiveChipItem]:
 * использует [AdaptiveChip] как чип по умолчанию.
 */

@Composable
internal fun AdaptiveChipScroller(
    items: List<AdaptiveChipItem>,
    state: AdaptiveChipState,
    modifier: Modifier = Modifier,
    rowCount: Int = 2,
    onScrollInfo: (ScrollInfo) -> Unit = {},
) = AdaptiveChipScroller(
    items = items,
    state = state,
    key = { it.id },
    modifier = modifier,
    rowCount = rowCount,
    onScrollInfo = onScrollInfo,
    chip = { item, isSelected, onToggle ->
        AdaptiveChip(
            title = item.title,
            iconResId = item.iconResId,
            isSelected = isSelected,
            onSelectionChange = { onToggle() },
        )
    },
)

@Preview(showBackground = true)
@Composable
private fun SynchronizedChipScrollerPreview() {
    AdaptiveChipsTheme {
        val state = rememberAdaptiveChipsState(
            initialSelectedIds = setOf("1", "3", "6"),
            selectionMode = SelectionMode.Multiple,
        )
        AdaptiveChipScroller(
            items = listOf(
                AdaptiveChipItem("1", "Чип первый", R.drawable.ic_chip_placeholder),
                AdaptiveChipItem("2", "Чип второй", R.drawable.ic_chip_placeholder),
                AdaptiveChipItem("3", "Чип третий", R.drawable.ic_chip_placeholder),
                AdaptiveChipItem("4", "Чип четвертый", R.drawable.ic_chip_placeholder),
                AdaptiveChipItem("5", "Чип пятый", R.drawable.ic_chip_placeholder),
                AdaptiveChipItem("6", "Чип шестой", R.drawable.ic_chip_placeholder),
                AdaptiveChipItem("7", "Чип седьмой", R.drawable.ic_chip_placeholder),
                AdaptiveChipItem("8", "Чип восьмой", R.drawable.ic_chip_placeholder),
            ),
            state = state,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}