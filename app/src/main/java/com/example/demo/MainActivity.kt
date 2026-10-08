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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.vkozhanova.adaptivechipselect.components.AdaptiveChipItem
import io.github.vkozhanova.adaptivechipselect.components.AdaptiveChipSelectBlock
import io.github.vkozhanova.adaptivechipselect.components.SelectionMode
import io.github.vkozhanova.adaptivechipselect.components.rememberAdaptiveChipsState
import io.github.vkozhanova.adaptivechipselect.theme.AdaptiveChipDefaults
import io.github.vkozhanova.adaptivechipselect.theme.AdaptiveChipsDefaults
import io.github.vkozhanova.adaptivechipselect.theme.AdaptiveChipsTheme
import com.example.demo.ui.theme.DemoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AdaptiveChipsTheme(
                        colors = AdaptiveChipsDefaults.colors().copy(
                            cardBackground = MaterialTheme.colorScheme.tertiaryContainer,
                            indicatorActive = MaterialTheme.colorScheme.tertiary,
                            editIconTint = MaterialTheme.colorScheme.tertiary,
                        ),
                        chipColors = AdaptiveChipDefaults.colors().copy(
                            selectedContainer = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                            selectedBorder = MaterialTheme.colorScheme.tertiary,
                            checkBackground = MaterialTheme.colorScheme.tertiary,
                            label = MaterialTheme.colorScheme.onTertiaryContainer,
                        ),
                    ) {
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
    val state = rememberAdaptiveChipsState(
        initialSelectedIds = setOf("1", "3", "6"),
        selectionMode = SelectionMode.Multiple,
    )
    val items = remember {
        listOf(
            AdaptiveChipItem("1", "Dizziness", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("2", "Headache", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("3", "Nausea", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("4", "Fatigue", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("5", "Fever", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("6", "Cough", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("7", "Chest pain", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("8", "Shortness of breath", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("9", "Rapid heartbeat", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("10", "Sweating", R.drawable.ic_chip_placeholder),
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
        AdaptiveChipSelectBlock(
            title = "Symptoms",
            items = items,
            state = state,
            isExpanded = isExpanded,
            onToggleExpanded = { isExpanded = !isExpanded },
            onEditCategory = {},
        )
    }
}