package com.example.adaptivechips.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Политика выделения чипов.
 *
 * @param allowMultiple можно ли выделить больше одного чипа
 * @param minSelected минимальное число выделенных (0 — можно снять все)
 * @param maxSelected максимальное число выделенных
 */

@Immutable
data class SelectionMode(
    val allowMultiple: Boolean = true,
    val minSelected: Int = 0,
    val maxSelected: Int = Int.MAX_VALUE,
) {
    init {
        require(minSelected >= 0) { "minSelected must be >= 0" }
        require(maxSelected >= minSelected) { "maxSelected must be >= minSelected" }
        if (!allowMultiple) {
            require(maxSelected <= 1) { "Single made: maxSelected must be <= 1" }
        }
    }

    //    Можно выделить только один чип, клик по другому переключает
    companion object {
        val Single: SelectionMode = SelectionMode(
            allowMultiple = false,
            minSelected = 0,
            maxSelected = 1,
        )

        //    Можно выделить сколько  угодно
        val Multiple: SelectionMode = SelectionMode()

        //    Ограничение сверху или снизу
        fun limited(min: Int = 0, max: Int = Int.MAX_VALUE): SelectionMode =
            SelectionMode(allowMultiple = true, minSelected = min, maxSelected = max)
    }
}

/**
 * Состояние выделения для набора чипов.
 *
 * Создавание через [rememberAdaptiveChipState] или вручную:
 * ```
 * val state = AdaptiveChipState(
 *     initialSelectedIds = setOf("1", "3"),
 *     selectionMode = SelectionMode.Multiple,
 * )
 * ```
 */
@Stable
class AdaptiveChipState internal constructor(
    initialSelectedIds: Set<String>,
    val selectionMode: SelectionMode,
) {
    var selectedIds: Set<String> by mutableStateOf(initialSelectedIds)
        private set

    //    Текущий набор выделенных id
    val selectionCount: Int
        get() = selectedIds.size

    fun isSelected(id: String): Boolean = id in selectedIds

    //    Переключает выделение
    fun toggle(id: String) = if (id in selectedIds) deselect(id) else select(id)

    //    Выделяет чип с учетом [selectionMode]. Ничего не делает, если нельзя
    fun select(id: String) {
        if (id in selectedIds) return
        selectedIds = when {
            !selectionMode.allowMultiple -> setOf(id)
            selectedIds.size >= selectionMode.maxSelected -> return
            else -> selectedIds + id
        }
    }

    //   Снимает выделение, если это не нарушит [SelectionMode.minSelected]
    fun deselect(id: String) {
        if (id !in selectedIds) return
        if (selectedIds.size <= selectionMode.minSelected) return
        selectedIds = selectedIds - id
    }

    //    Чнимает все выделение, елси это допустимо режимом
    fun clear() {
        if (selectionMode.minSelected > 0) return
        selectedIds = emptySet()
    }

    /**
     * Полностью заменяет выделение.
     * В режиме Single оставит максимум один id, в Limited — не больше max.
     * minSelected здесь не проверяется: считается, что вызывающий знает, что делает.
     */
    fun replace(newSelectedIds: Set<String>) {
        selectedIds = when {
            !selectionMode.allowMultiple -> newSelectedIds.take(1).toSet()
            newSelectedIds.size > selectionMode.maxSelected -> newSelectedIds.take(selectionMode.maxSelected)
                .toSet()

            else -> newSelectedIds
        }
    }

    companion object {
        val Saver: Saver<AdaptiveChipState, Any> = listSaver(
            save = { state ->
                listOf(
                    state.selectedIds.toList(),
                    state.selectionMode.allowMultiple,
                    state.selectionMode.minSelected,
                    state.selectionMode.maxSelected,
                )
            },
            restore = { saved ->
                val ids = (saved[0] as List<*>).filterIsInstance<String>().toSet()
                val allowMultiple = saved[1] as Boolean
                val min = saved[2] as Int
                val max = saved[3] as Int
                AdaptiveChipState(
                    initialSelectedIds = ids,
                    selectionMode = SelectionMode(allowMultiple, min, max),
                )
            },
        )
    }
}

/**
 * Создаёт и запоминает [AdaptiveChipState], переживающий пересоздание Activity.
 *
 * При изменении [selectionMode] state пересоздаётся с чистого листа —
 * режим меняется редко и это лучше, чем тащить несовместимое состояние.
 */
@Composable
fun rememberAdaptiveChipsState(
    initialSelectedIds: Set<String> = emptySet(),
    selectionMode: SelectionMode = SelectionMode.Multiple,
): AdaptiveChipState = rememberSaveable(
    inputs = arrayOf(selectionMode),
    saver = AdaptiveChipState.Saver,
) {
    AdaptiveChipState(
        initialSelectedIds = initialSelectedIds,
        selectionMode = selectionMode,
    )
}
