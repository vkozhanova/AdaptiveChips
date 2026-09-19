package com.example.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.adaptivechips.components.NoteItemData
import com.example.adaptivechips.components.NoteSelectBlock
import com.example.adaptivechips.theme.AdaptiveChipsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AdaptiveChipsTheme {
                        DemoContent()
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoContent() {
    var isExpanded by remember { mutableStateOf(false) }

    // Список как состояние — клики меняют isSelected и UI перерисовывается.
    val items = remember {
        mutableStateListOf(
            NoteItemData("1", "Головокружение", R.drawable.ic_chip_placeholder, true),
            NoteItemData("2", "Головная боль", R.drawable.ic_chip_placeholder, false),
            NoteItemData("3", "Тошнота", R.drawable.ic_chip_placeholder, true),
            NoteItemData("4", "Слабость", R.drawable.ic_chip_placeholder, false),
            NoteItemData("5", "Температура", R.drawable.ic_chip_placeholder, false),
            NoteItemData("6", "Кашель", R.drawable.ic_chip_placeholder, true),
            NoteItemData("7", "Боль в груди", R.drawable.ic_chip_placeholder, false),
            NoteItemData("8", "Одышка", R.drawable.ic_chip_placeholder, false),
            NoteItemData("9", "Учащённый пульс", R.drawable.ic_chip_placeholder, false),
            NoteItemData("10", "Потливость", R.drawable.ic_chip_placeholder, false),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        NoteSelectBlock(
            title = "Симптомы",
            noteItems = items,
            isExpanded = isExpanded,
            onToggleExpanded = { isExpanded = !isExpanded },
            onEditCategory = {},
            onItemSelectionChanged = { id, selected ->
                val index = items.indexOfFirst { it.id == id }
                if (index >= 0) {
                    items[index] = items[index].copy(isSelected = selected)
                }
            },
        )
    }
}