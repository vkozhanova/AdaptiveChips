package com.example.adaptivechips.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.adaptivechips.theme.AdaptiveChipTheme

@Composable
@Suppress("LongParameterList")
public fun AdaptiveChip(
    title: String,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes iconResId: Int? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val dimens = AdaptiveChipTheme.dimens
    val colors = AdaptiveChipTheme.colors

    Box(
        modifier = modifier.wrapContentWidth(),
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(dimens.cornerRadius))
                .height(dimens.height)
                .wrapContentWidth()
                .background(
                    color = if (isSelected) colors.selectedContainer else colors.unselectedContainer,
                    shape = RoundedCornerShape(dimens.cornerRadius),
                )
                .border(
                    width = if (isSelected) 1.5.dp else 0.dp,
                    color = if (isSelected) colors.selectedBorder else Color.Transparent,
                    shape = RoundedCornerShape(dimens.cornerRadius),
                )
                .clickable(enabled = enabled) { onSelectionChange(!isSelected) },
        ) {
            Row(
                modifier = Modifier
                    .height(dimens.height)
                    .wrapContentWidth()
                    .padding(horizontal = dimens.horizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            ) {
                when {
                    leadingIcon != null -> leadingIcon()
                    iconResId != null -> Icon(
                        painter = painterResource(iconResId),
                        contentDescription = title,
                        modifier = Modifier.size(dimens.iconSize),
                        tint = Color.Unspecified,
                    )

                    else -> Spacer(Modifier.width(dimens.iconSize))
                }
                Text(
                    text = title,
                    style = AdaptiveChipTheme.typography.label,
                    color = colors.label,
                )
            }
        }

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
public fun AdaptiveChipPreview() {
    AdaptiveChipTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                AdaptiveChip(
                    title = "Чип первый",
                    isSelected = false,
                    onSelectionChange = {},
                    iconResId = R.drawable.ic_chip_placeholder,
                )
                AdaptiveChip(
                    title = "Чип второй",
                    isSelected = true,
                    onSelectionChange = {},
                    iconResId = R.drawable.ic_chip_placeholder,
                )
            }
            AdaptiveChip(
                title = "Без иконки",
                isSelected = false,
                onSelectionChange = {},
            )
            AdaptiveChip(
                title = "Выключен",
                isSelected = false,
                onSelectionChange = {},
                enabled = false,
                iconResId = R.drawable.ic_chip_placeholder,
            )
        }
    }
}