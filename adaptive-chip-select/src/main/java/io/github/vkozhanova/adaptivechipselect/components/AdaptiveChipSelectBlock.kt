package io.github.vkozhanova.adaptivechipselect.components

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
import io.github.vkozhanova.adaptivechipselect.R
import io.github.vkozhanova.adaptivechipselect.theme.AdaptiveChipsTheme
import io.github.vkozhanova.adaptivechipselect.theme.AdaptiveChipsDimens

/**
 * Expanded mode: chips are laid out with `FlowRow` and wrap to the next row
 * as the container width fills up.
 *
 * Unlike [CollapsedChipGrid], there is no limit on the number of rows —
 * the count is determined by the container width. If the content does not
 * fit vertically, a vertical scroll kicks in, capped by
 * [AdaptiveChipsDimens.maxCollapsedHeight].
 *
 * Typically used through [AdaptiveChipSelectBlock] rather than directly.
 *
 * @param items list of chips
 * @param state selection state
 * @param modifier modifier for the root Box
 *
 * @see AdaptiveChipSelectBlock
 * @see CollapsedChipGrid
 */

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

/**
 * Collapsed mode: N rows of chips with synchronized horizontal scrolling
 * and magnetic snap to groups.
 *
 * Delegates layout and scrolling behavior to [AdaptiveChipScroller].
 * Below the scroller, shows [ChipPagerIndicator] — but only when there is
 * more than one group. The expand button (divider + arrow) appears only
 * when the content actually doesn't fit horizontally
 * (`maxScroll > 0` in [ScrollInfo]).
 *
 * Typically used through [AdaptiveChipSelectBlock], which toggles between
 * [CollapsedChipGrid] and [ExpandedChipGrid].
 *
 * @param items list of chips
 * @param state selection state
 * @param onExpandClick callback invoked when the "Expand" button is clicked
 * @param modifier modifier for the root Column
 *
 * @see AdaptiveChipSelectBlock
 * @see ExpandedChipGrid
 */

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
        if (scrollInfo.groupCount > 1) {
            ChipPagerIndicator(
                groupCount = scrollInfo.groupCount,
                currentGroup = scrollInfo.currentGroup,
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
 * Chip selection card with collapse/expand.
 *
 * @param title block title. Ignored when [titleContent] is provided.
 * @param items list of chips.
 * @param state selection state.
 * @param isExpanded mode: collapsed (two scrolling rows) or expanded (FlowRow).
 * @param onToggleExpanded callback that toggles the mode.
 * @param onEditCategory edit-button callback. When null, the icon is hidden.
 * @param modifier modifier for the root Column.
 * @param titleContent title slot. Replaces [title] when provided.
 * @param emptyContent empty-state slot. Shown when [items] is empty.
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
                    AdaptiveChipItem("1", "Chip one", R.drawable.ic_chip_placeholder),
                    AdaptiveChipItem("2", "Chip two", R.drawable.ic_chip_placeholder),
                    AdaptiveChipItem("3", "Chip three", R.drawable.ic_chip_placeholder),
                    AdaptiveChipItem("4", "Chip four", R.drawable.ic_chip_placeholder),
                    AdaptiveChipItem("5", "Chip five", R.drawable.ic_chip_placeholder),
                    AdaptiveChipItem("6", "Chip six", R.drawable.ic_chip_placeholder),
                    AdaptiveChipItem("7", "Chip seven", R.drawable.ic_chip_placeholder),
                    AdaptiveChipItem("8", "Chip eight", R.drawable.ic_chip_placeholder),
                ),
                state = state,
                isExpanded = isExpanded,
                onToggleExpanded = { isExpanded = !isExpanded },
                onEditCategory = {},
            )
        }
    }
}