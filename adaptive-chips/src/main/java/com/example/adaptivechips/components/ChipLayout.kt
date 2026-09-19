package com.example.adaptivechips.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints

internal data class LaidChip<T>(
    val item: T,
    val x: Int,
    val width: Int,
)

internal data class ChipLayout<T>(
    val rows: List<List<LaidChip<T>>>,
    val totalWidth: Int,
    val snapTargets: List<Int>,
)

internal fun <T> layoutChips(
    items: List<T>,
    rowCount: Int,
    chipWidthPx: (T) -> Int,
    spacingPx: Int,
): ChipLayout<T> {
    val rows: List<MutableList<LaidChip<T>>> = List(rowCount) { mutableListOf() }

    items.forEachIndexed { index, item ->
        val rowIndex = index % rowCount
        val row = rows[rowIndex]
        val w = chipWidthPx(item)
        val x = if (row.isEmpty()) 0 else row.last().x + row.last().width + spacingPx
        row.add(LaidChip(item = item, x = x, width = w))
    }

    val widths: List<Int> = rows.map { row ->
        if (row.isEmpty()) 0 else row.last().x + row.last().width
    }
    val totalWidth = widths.maxOrNull() ?: 0
    val snapTargets: List<Int> = rows.firstOrNull()?.map { it.x } ?: emptyList()

    return ChipLayout(rows = rows, totalWidth = totalWidth, snapTargets = snapTargets)
}

internal fun measureChipWidthPx(
    title: String,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    leadingIconSizePx: Int,
    horizontalPaddingPx: Int,
    contentSpacingPx: Int,
    checkOverhangPx: Int,
): Int {
    val textWidth = textMeasurer.measure(
        text = AnnotatedString(title),
        style = textStyle,
        constraints = Constraints(maxWidth = Int.MAX_VALUE),
    ).size.width
    return leadingIconSizePx + contentSpacingPx + textWidth + horizontalPaddingPx * 2 + checkOverhangPx
}