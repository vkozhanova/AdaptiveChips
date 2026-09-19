package com.example.adaptivechips.components

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class AdaptiveChipStateTest {

    @Test
    fun multiple_selects_many() {
        val state = AdaptiveChipState(emptySet(), SelectionMode.Multiple)
        state.toggle("a"); state.toggle("b"); state.toggle("c")
        assertEquals(setOf("a", "b", "c"), state.selectedIds)
    }

    @Test
    fun multiple_toggle_removes() {
        val state = AdaptiveChipState(setOf("a", "b"), SelectionMode.Multiple)
        state.toggle("a")
        assertEquals(setOf("b"), state.selectedIds)
    }

    @Test
    fun single_click_other_replaces() {
        val state = AdaptiveChipState(setOf("a"), SelectionMode.Single)
        state.toggle("b")
        assertEquals(setOf("b"), state.selectedIds)
    }

    @Test fun single_click_self_deselects() {
        val state = AdaptiveChipState(setOf("a"), SelectionMode.Single)
        state.toggle("a")
        assertTrue(state.selectedIds.isEmpty())
    }

    @Test
    fun limited_max_blocks_new_election() {
        val state = AdaptiveChipState(setOf("a", "b"), SelectionMode.limited(max = 2))
        state.toggle("c")
        assertEquals(setOf("a", "b"), state.selectedIds)
    }

    @Test
    fun limited_allows_change_after_max_reached() {
        val state = AdaptiveChipState(setOf("a","b"), SelectionMode.limited(max = 2))
        state.toggle("a")
        state.toggle("c")
        assertEquals(setOf("b", "c"),  state.selectedIds)
    }

    @Test
    fun clear_works_in_multiple() {
        val state = AdaptiveChipState(setOf("a", "b"), SelectionMode.Multiple)
        state.clear()
        assertTrue(state.selectedIds.isEmpty())
    }

    @Test
    fun clear_blocked_by_min() {
        val state = AdaptiveChipState(setOf("a"), SelectionMode.limited(min = 1))
        state.clear()
        assertEquals(setOf("a"), state.selectedIds)
    }

    @Test
    fun replace_in_single_keeps_one() {
        val state = AdaptiveChipState(emptySet(), SelectionMode.Single)
        state.replace(setOf("a", "b", "c"))
        assertEquals(1, state.selectedIds.size)
    }

    @Test
    fun replace_in_limited_crops_in_max() {
        val state = AdaptiveChipState(emptySet(), SelectionMode.limited(max = 2))
        state.replace(setOf("a", "b", "c"))
        assertEquals(2, state.selectedIds.size)
    }

    @Test
    fun is_selected_reports_correctly() {
        val state = AdaptiveChipState(setOf("a"), SelectionMode.Multiple)
        assertTrue(state.isSelected("a"))
        assertFalse(state.isSelected("b"))
    }
}