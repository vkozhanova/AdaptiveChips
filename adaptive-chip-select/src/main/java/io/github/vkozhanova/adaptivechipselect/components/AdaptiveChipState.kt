package io.github.vkozhanova.adaptivechipselect.components

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
 * Chip selection policy.
 *
 * @param allowMultiple whether more than one chip can be selected
 * @param minSelected minimum number of selected chips (0 — all can be deselected)
 * @param maxSelected maximum number of selected chips
 */

@Immutable
public data class SelectionMode(
    val allowMultiple: Boolean = true,
    val minSelected: Int = 0,
    val maxSelected: Int = Int.MAX_VALUE,
) {
    init {
        require(minSelected >= 0) { "minSelected must be >= 0" }
        require(maxSelected >= minSelected) { "maxSelected must be >= minSelected" }
        if (!allowMultiple) {
            require(maxSelected <= 1) { "Single mode: maxSelected must be <= 1" }
        }
    }

    /** Only one chip can be selected at a time; clicking another one switches. */
    public companion object {
        public val Single: SelectionMode = SelectionMode(
            allowMultiple = false,
            minSelected = 0,
            maxSelected = 1,
        )

        /** Any number of chips can be selected. */
        public val Multiple: SelectionMode = SelectionMode()

        /** Lower and/or upper bound on the number of selected chips. */
        public fun limited(min: Int = 0, max: Int = Int.MAX_VALUE): SelectionMode =
            SelectionMode(allowMultiple = true, minSelected = min, maxSelected = max)
    }
}

/**
 * Selection state for a set of chips.
 *
 * Create it via [rememberAdaptiveChipsState], or manually:
 * ```
 * val state = AdaptiveChipState(
 *     initialSelectedIds = setOf("1", "3"),
 *     selectionMode = SelectionMode.Multiple,
 * )
 * ```
 */
@Stable
public class AdaptiveChipState internal constructor(
    initialSelectedIds: Set<String>,
    public val selectionMode: SelectionMode,
) {
    public var selectedIds: Set<String> by mutableStateOf(initialSelectedIds)
        private set

    /** Current set of selected ids. */
    public val selectionCount: Int
        get() = selectedIds.size

    public fun isSelected(id: String): Boolean = id in selectedIds

    /** Toggles selection: selects if not selected, deselects otherwise. */
    public fun toggle(id: String) {
        if (id in selectedIds) deselect(id) else select(id)
    }

    /** Selects a chip according to [selectionMode]. No-op when not allowed. */
    public fun select(id: String) {
        if (id in selectedIds) return
        selectedIds = when {
            !selectionMode.allowMultiple -> setOf(id)
            selectedIds.size >= selectionMode.maxSelected -> return
            else -> selectedIds + id
        }
    }

    /** Deselects a chip unless doing so would violate [SelectionMode.minSelected]. */
    public fun deselect(id: String) {
        if (id !in selectedIds) return
        if (selectedIds.size <= selectionMode.minSelected) return
        selectedIds = selectedIds - id
    }

    /** Clears the entire selection if allowed by the current mode. */
    public fun clear() {
        if (selectionMode.minSelected > 0) return
        selectedIds = emptySet()
    }

    /**
     * Replaces the entire selection.
     * In Single mode keeps at most one id; in Limited mode keeps no more than max.
     * `minSelected` is not enforced here — the caller is assumed to know what
     * they are doing.
     */
    public fun replace(newSelectedIds: Set<String>) {
        selectedIds = when {
            !selectionMode.allowMultiple -> newSelectedIds.take(1).toSet()
            newSelectedIds.size > selectionMode.maxSelected -> newSelectedIds.take(selectionMode.maxSelected)
                .toSet()

            else -> newSelectedIds
        }
    }

    public companion object {
        public val Saver: Saver<AdaptiveChipState, Any> = listSaver(
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
 * Creates and remembers an [AdaptiveChipState] that survives Activity
 * recreation.
 *
 * When [selectionMode] changes, the state is recreated from scratch —
 * the mode rarely changes, and this is cleaner than carrying over an
 * incompatible state.
 */

@Composable
public fun rememberAdaptiveChipsState(
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