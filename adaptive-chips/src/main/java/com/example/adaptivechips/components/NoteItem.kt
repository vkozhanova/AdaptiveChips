package com.example.adaptivechips.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adaptivechips.R
import com.example.adaptivechips.theme.AdaptiveChipsTheme

@Composable
@Suppress("LongParameterList")
fun NoteItem(
    title: String,
    @DrawableRes iconResId: Int,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = AdaptiveChipsTheme.dimens
    val colors = AdaptiveChipsTheme.colors

    Box(
        modifier = modifier.wrapContentWidth(),
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(dimens.chipCornerRadius))
                .height(dimens.chipHeight)
                .wrapContentWidth()
                .background(
                    color = if (isSelected) colors.selectedContainer else colors.unselectedContainer,
                    shape = RoundedCornerShape(dimens.chipCornerRadius),
                )
                .border(
                    width = if (isSelected) 1.5.dp else 0.dp,
                    color = if (isSelected) colors.selectedBorder else Color.Transparent,
                    shape = RoundedCornerShape(dimens.chipCornerRadius),
                )
                .clickable { onSelectionChange(!isSelected) },
        ) {
            Row(
                modifier = Modifier
                    .height(dimens.chipHeight)
                    .wrapContentWidth()
                    .padding(horizontal = dimens.chipHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.chipContentSpacing),
            ) {
                Icon(
                    painter = painterResource(iconResId),
                    contentDescription = title,
                    modifier = Modifier.size(dimens.iconSize),
                    tint = Color.Unspecified,
                )
                Text(
                    text = title,
                    style = AdaptiveChipsTheme.typography.chipLabel,
                    color = colors.label,
                )
            }
        }

        // Иконка галочки снаружи чипа, позиционируется относительно внешнего Box
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = dimens.checkOffsetX, y = dimens.checkOffsetY)
                    .size(dimens.checkSize)
                    .background(
                        color = colors.checkBackground,
                        shape = CircleShape,
                    )
                    .border(
                        width = dimens.checkBorderWidth,
                        color = colors.checkBorder,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chip_check),
                    contentDescription = title,
                    modifier = Modifier.size(dimens.checkIconSize),
                    tint = colors.checkIconTint,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {
    AdaptiveChipsTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                NoteItem(
                    title = "Чип первый",
                    iconResId = R.drawable.ic_chip_placeholder,
                    isSelected = false,
                    onSelectionChange = {},
                )
                NoteItem(
                    title = "Чип второй",
                    iconResId = R.drawable.ic_chip_placeholder,
                    isSelected = true,
                    onSelectionChange = {},
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                NoteItem(
                    title = "Чип третий",
                    iconResId =R.drawable.ic_chip_placeholder,
                    isSelected = true,
                    onSelectionChange = {},
                )
                NoteItem(
                    title = "Чип четвертый",
                    iconResId = R.drawable.ic_chip_placeholder,
                    isSelected = false,
                    onSelectionChange = {},
                )
            }
            NoteItem(
                title = "Чип с самым длинным названием",
                iconResId = R.drawable.ic_chip_placeholder,
                isSelected = true,
                onSelectionChange = {},
            )
        }
    }
}
