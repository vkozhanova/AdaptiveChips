package com.example.adaptivechips.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adaptivechips.R
import com.example.adaptivechips.theme.AdaptiveChipsTheme

@Immutable
public data class AdaptiveChipItem(
    val id: String,
    val title: String,
    @DrawableRes val iconResId: Int,
)

@Composable
public fun ExpandedChipGrid(
    items: List<AdaptiveChipItem>,
    state: AdaptiveChipState,
    modifier: Modifier = Modifier,
) {
    val dimens = AdaptiveChipsTheme.dimens

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = dimens.maxCollapsedHeight)
            .verticalScroll(rememberScrollState()),
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.horizontalScreenPadding),
            horizontalArrangement = Arrangement.spacedBy(dimens.itemSpacing),
            verticalArrangement = Arrangement.spacedBy(dimens.rowSpacing),
            maxItemsInEachRow = Int.MAX_VALUE,
        ) {
            items.forEach { item ->
                AdaptiveChip(
                    title = item.title,
                    iconResId = item.iconResId,
                    isSelected = state.isSelected(item.id),
                    onSelectionChange = { state.toggle(item.id) },
                )
            }
        }
    }
}

@Composable
public fun CollapsedChipGrid(
    items: List<AdaptiveChipItem>,
    state: AdaptiveChipState,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = AdaptiveChipsTheme.dimens
    var scrollInfo by remember { mutableStateOf(ScrollInfo(0, 0, 0)) }

    val canScroll = scrollInfo.maxScroll > 0

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AdaptiveChipScroller(
            items = items,
            state = state,
            onScrollInfo = { scrollInfo = it },
        )
        if (scrollInfo.pairCount > 1) {
            ChipPagerIndicator(
                pairCount = scrollInfo.pairCount,
                currentPair = scrollInfo.currentPair,
            )
        }
        if (canScroll) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = dimens.horizontalScreenPadding),
                color = MaterialTheme.colorScheme.background,
            )
            ExpandCollapseButton(
                text = stringResource(R.string.adaptive_chips_expand),
                onClick = onExpandClick,
                iconResId = R.drawable.ic_chip_arrow_down,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ExpandCollapseButton(
    text: String,
    onClick: () -> Unit,
    @DrawableRes iconResId: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = AdaptiveChipsTheme.typography.sectionTitle
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Icon(
            painter = painterResource(iconResId),
            contentDescription = null,
            modifier = modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

/**
 * Карточка выбора чипов с разворачиванием.
 *
 * @param title заголовок блока. Игнорируется, если задан [titleContent].
 * @param items список чипов.
 * @param state состояние выделения.
 * @param isExpanded режим: свёрнут (две строки со скроллом) или развёрнут (FlowRow).
 * @param onToggleExpanded колбэк переключения режима.
 * @param onEditCategory колбэк кнопки редактирования. Если null — иконка не показывается.
 * @param modifier модификатор корневого Column.
 * @param titleContent слот заголовка. Заменяет [title], если задан.
 * @param emptyContent слот пустого состояния. Показывается, если [items] пустой.
 */

@Suppress("LongParameterList")
@Composable
public fun AdaptiveChipSelectBlock(
    title: String,
    items: List<AdaptiveChipItem>,
    state: AdaptiveChipState,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier,
    onEditCategory: (() -> Unit)? = null,
    titleContent: (@Composable () -> Unit)? = null,
    emptyContent: (@Composable () -> Unit)? = null,
) {
    val dimens = AdaptiveChipsTheme.dimens

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(vertical = 17.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.horizontalScreenPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (titleContent != null) {
                titleContent()
            } else {
                Text(
                    text = title,
                    style = AdaptiveChipsTheme.typography.sectionTitle,
                )
            }
            if (onEditCategory != null) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_chip_edit),
                    contentDescription = title,
                    modifier = Modifier
                        .size(15.dp)
                        .clickable(onClick = onEditCategory),
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (items.isEmpty() && emptyContent != null) {
            emptyContent()
            return@Column
        }

        if (isExpanded) {
            ExpandedChipGrid(
                items = items,
                state = state,
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = dimens.horizontalScreenPadding),
                color = MaterialTheme.colorScheme.background,
            )
            Spacer(modifier = Modifier.height(12.dp))
            ExpandCollapseButton(
                text = stringResource(R.string.adaptive_chips_collapse),
                onClick = onToggleExpanded,
                iconResId = R.drawable.ic_chip_arrow_down,
                modifier = Modifier.rotate(180f)
            )
        } else {
            CollapsedChipGrid(
                items = items,
                state = state,
                onExpandClick = onToggleExpanded,
            )
        }
    }
}

@Preview(showBackground = true, locale = "ru")
@Composable
private fun AdaptiveChipSelectBlockPreview() {
    AdaptiveChipsTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            var isExpanded by remember { mutableStateOf(false) }
            val state = rememberAdaptiveChipsState(
                initialSelectedIds = setOf("1", "3"),
                selectionMode = SelectionMode.Multiple,
            )

            AdaptiveChipSelectBlock(
                title = "Чипы разной длины",
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
                isExpanded = isExpanded,
                onToggleExpanded = { isExpanded = !isExpanded },
                onEditCategory = {},
            )
        }
    }
}