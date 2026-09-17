package com.example.adaptivechips.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.adaptivechips.R
import com.example.adaptivechips.theme.AdaptiveChipsTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Immutable
data class NoteItemData(
    val id: String,
    val title: String,
    @DrawableRes val iconResId: Int,
    val isSelected: Boolean = false,
)

// Суть логики переноса: рассчитывается ширина каждого чипа, чипы складываются в строки,
// пока они помещаются по ширине контейнера, если ее недостаточно, чип перемещается в следующую строку.
// Развернутое состояние
@Composable
fun ExpandedStateGrid(
    items: List<NoteItemData>,
    onItemSelectionChanged: (String, Boolean) -> Unit,
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
                NoteItem(
                    title = item.title,
                    iconResId = item.iconResId,
                    isSelected = item.isSelected,
                    onSelectionChange = { onItemSelectionChanged(item.id, it) },
                )
            }
        }
    }
}

// Свернутое состояние
@Composable
fun CollapsedNoteGrid(
    items: List<NoteItemData>,
    onItemSelectionChanged: (String, Boolean) -> Unit,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = AdaptiveChipsTheme.dimens

    val firstRowState = rememberLazyListState()
    val secondRowState = rememberLazyListState()

    val indicatorIndex by remember {
        derivedStateOf {
            when {
                !firstRowState.canScrollBackward -> 0
                !firstRowState.canScrollForward -> 2
                else -> 1
            }
        }
    }
    val firstRow = remember(items) {
        items.filterIndexed { index, _ -> index % 2 == 0 }
    }
    val secondRow = remember(items) {
        items.filterIndexed { index, _ -> index % 2 == 1 }
    }
    val canScroll by remember {
        derivedStateOf {
            firstRowState.canScrollForward || secondRowState.canScrollForward
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(dimens.rowSpacing)) {
            LazyRow(
                state = firstRowState,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.itemSpacing),
                contentPadding = PaddingValues(start = dimens.horizontalScreenPadding),
            ) {
                items(firstRow) { item ->
                    NoteItem(
                        title = item.title,
                        iconResId = item.iconResId,
                        isSelected = item.isSelected,
                        onSelectionChange = {
                            onItemSelectionChanged(item.id, it)
                        },
                    )
                }
            }
            LazyRow(
                state = secondRowState,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.itemSpacing),
                contentPadding = PaddingValues(start = dimens.horizontalScreenPadding),
            ) {
                items(secondRow) { item ->
                    NoteItem(
                        title = item.title,
                        iconResId = item.iconResId,
                        isSelected = item.isSelected,
                        onSelectionChange = {
                            onItemSelectionChanged(item.id, it)
                        },
                    )
                }
            }
        }
// Индикатор
        if (canScroll) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == indicatorIndex) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                },
                            ),
                    )
                    if (index < 2) Spacer(modifier = Modifier.width(4.dp))
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = dimens.horizontalScreenPadding),
                color = MaterialTheme.colorScheme.background,
            )

            ExpandCollapseButton(
                text = stringResource(R.string.adaptive_chips_expand),
                onClick = onExpandClick,
                iconResId = R.drawable.ic_chip_arrow_down,
                modifier = Modifier.rotate(180f),
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

@Suppress("LongParameterList")
@Composable
fun NoteSelectBlock(
    title: String,
    noteItems: List<NoteItemData>,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onEditCategory: () -> Unit,
    onItemSelectionChanged: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = AdaptiveChipsTheme.dimens

    Column(
        modifier = Modifier
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
            Text(
                text = title,
                style = AdaptiveChipsTheme.typography.sectionTitle,
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_chip_edit),
                contentDescription = title,
                modifier = Modifier
                    .size(15.dp)
                    .clickable(onClick = onEditCategory),
                tint = MaterialTheme.colorScheme.secondary,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (isExpanded) {
            ExpandedStateGrid(
                items = noteItems,
                onItemSelectionChanged = onItemSelectionChanged,
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
                modifier = modifier,
            )
        } else {
            CollapsedNoteGrid(
                items = noteItems,
                onItemSelectionChanged = onItemSelectionChanged,
                onExpandClick = onToggleExpanded,
            )
        }
    }
}

@Preview(showBackground = true, locale = "ru")
@Composable
fun NoteSelectBlockPreview() {
    AdaptiveChipsTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            NoteSelectBlock(
                title = "Чипы разной длины",
                noteItems = listOf(
                    NoteItemData(
                        id = "1",
                        title = "Чип первый",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = true,
                    ),
                    NoteItemData(
                        id = "2",
                        title = "Чип второй",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = false,
                    ),
                    NoteItemData(
                        id = "3",
                        title = "Чип третий",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = true,
                    ),
                    NoteItemData(
                        id = "4",
                        title = "Чип четвертый",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = false,
                    ),
                    NoteItemData(
                        id = "5",
                        title = "Чип пятый",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = false,
                    ),
                    NoteItemData(
                        id = "6",
                        title = "Чип шестой",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = true,
                    ),
                ).toPersistentList(),
                isExpanded = false,
                onToggleExpanded = {},
                onEditCategory = {},
                onItemSelectionChanged = { _, _ -> },
            )
            // Пример с развернутым состоянием
            var isExpanded by remember { mutableStateOf(true) }
            NoteSelectBlock(
                title = "Чипы разной длины",
                noteItems = listOf(
                    NoteItemData(
                        id = "1",
                        title = "Чип первый",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = false,
                    ),
                    NoteItemData(
                        id = "2",
                        title = "Чип второй",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = false,
                    ),
                    NoteItemData(
                        id = "3",
                        title = "Чип третий",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = true,
                    ),
                    NoteItemData(
                        id = "4",
                        title = "Чип четвертый",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = false,
                    ),
                    NoteItemData(
                        id = "5",
                        title = "Чип пятый",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = false,
                    ),
                    NoteItemData(
                        id = "6",
                        title = "Чип шестой",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = true,
                    ),
                    NoteItemData(
                        id = "7",
                        title = "Чип седьмой",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = false,
                    ),
                    NoteItemData(
                        id = "8",
                        title = "Чип восьмой",
                        iconResId = R.drawable.ic_chip_placeholder,
                        isSelected = true,
                    ),
                ).toPersistentList(),
                isExpanded = isExpanded,
                onToggleExpanded = { isExpanded = !isExpanded },
                onEditCategory = {},
                onItemSelectionChanged = { _, _ -> },
            )
        }
    }
}