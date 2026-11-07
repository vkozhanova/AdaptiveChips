package com.example.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.example.demo.ui.theme.DemoAppTheme
import io.github.vkozhanova.adaptivechipselect.theme.AdaptiveChipsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoAppTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    DemoContent()
                }
            }
        }
    }
}

@Composable
private fun DemoContent() {
    val physicalItems = remember {
        listOf(
            AdaptiveChipItem("1", "Dizziness", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("2", "Headache", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("3", "Fatigue", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("4", "Fever", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("5", "Cough", R.drawable.ic_chip_placeholder),
        )
    }

    val emotionItems = remember {
        listOf(
            AdaptiveChipItem("1", "Happy", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("2", "Sad", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("3", "Anxious", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("4", "Calm", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("5", "Angry", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("6", "Excited", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("7", "Nostalgic", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("8", "Hopeful", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("9", "Overwhelmed", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("10", "Grateful", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("11", "Lonely", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("12", "Confident", R.drawable.ic_chip_placeholder),
        )
    }

    val tagsItems = remember {
        listOf(
            AdaptiveChipItem("1", "Work", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("2", "Rest", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("3", "Sport", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("4", "Family", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("5", "Close Friends", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("6", "Hobby", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("7", "Study", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("8", "Distance Travel", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("9", "Health", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("10", "Finance", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("11", "Home", R.drawable.ic_chip_placeholder),
            AdaptiveChipItem("12", "Social", R.drawable.ic_chip_placeholder),
        )
    }

    var isExpanded1 by remember { mutableStateOf(false) }
    var isExpanded2 by remember { mutableStateOf(false) }
    var isExpanded3 by remember { mutableStateOf(false) }

    val state1 = rememberAdaptiveChipsState(
        initialSelectedIds = setOf("1", "3", "4"),
        selectionMode = SelectionMode.Multiple
    )
    val state2 = rememberAdaptiveChipsState(
        initialSelectedIds = setOf("2", "5"),
        selectionMode = SelectionMode.Multiple
    )
    val state3 = rememberAdaptiveChipsState(
        initialSelectedIds = setOf("3", "2", "6", "8"),
        selectionMode = SelectionMode.Multiple
    )
    AdaptiveChipsTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            AdaptiveChipSelectBlock(
                title = "Physical Symptoms",
                items = physicalItems,
                state = state1,
                isExpanded = isExpanded1,
                onToggleExpanded = { isExpanded1 = !isExpanded1 },
                onEditCategory = {},
                rowCount = 1,
                colors = AdaptiveChipsDefaults.colors().copy(
                    cardBackground = MaterialTheme.colorScheme.onBackground,
                    divider = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.4f),
                    indicatorActive = MaterialTheme.colorScheme.onSecondaryContainer,
                    indicatorInactive = MaterialTheme.colorScheme.onSecondary,
                    editIconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                    expandCollapseTint = MaterialTheme.colorScheme.onSecondaryContainer,
                    titleText = MaterialTheme.colorScheme.onSecondaryContainer,
                    expandCollapseText = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                chipColors = AdaptiveChipDefaults.colors().copy(
                    unselectedContainer = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.4f),
                    selectedContainer = MaterialTheme.colorScheme.onSecondary,
                    selectedBorder = MaterialTheme.colorScheme.onPrimary,
                    unselectedLabel = MaterialTheme.colorScheme.onBackground,
                    selectedLabel = MaterialTheme.colorScheme.onBackground,
                    checkBackground = MaterialTheme.colorScheme.surface,
                    checkBorder = MaterialTheme.colorScheme.onPrimary,
                    checkIconTint = MaterialTheme.colorScheme.onPrimary,
                )
            )

            AdaptiveChipSelectBlock(
                title = "Emotional State",
                items = emotionItems,
                state = state2,
                isExpanded = isExpanded2,
                onToggleExpanded = { isExpanded2 = !isExpanded2 },
                onEditCategory = {},
                rowCount = 2,
                colors = AdaptiveChipsDefaults.colors().copy(
                    cardBackground = MaterialTheme.colorScheme.surface,
                    divider = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    indicatorActive = MaterialTheme.colorScheme.onTertiary,
                    indicatorInactive = MaterialTheme.colorScheme.background,
                    editIconTint = MaterialTheme.colorScheme.onSurface,
                    expandCollapseTint = MaterialTheme.colorScheme.onSurface,
                    titleText = MaterialTheme.colorScheme.onSurface,
                    expandCollapseText = MaterialTheme.colorScheme.onSurface,
                ),
                chipColors = AdaptiveChipDefaults.colors().copy(
                    unselectedContainer = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.4f),
                    selectedContainer = MaterialTheme.colorScheme.onTertiary,
                    selectedBorder = MaterialTheme.colorScheme.onPrimary,
                    unselectedLabel = MaterialTheme.colorScheme.onPrimary,
                    selectedLabel = MaterialTheme.colorScheme.surface,
                    checkBackground = MaterialTheme.colorScheme.surface,
                    checkBorder = MaterialTheme.colorScheme.onPrimary,
                    checkIconTint = MaterialTheme.colorScheme.onPrimary,
                ),
            )

            AdaptiveChipSelectBlock(
                title = "Tags",
                items = tagsItems,
                state = state3,
                isExpanded = isExpanded3,
                onToggleExpanded = { isExpanded3 = !isExpanded3 },
                onEditCategory = {},
                rowCount = 3,
                colors = AdaptiveChipsDefaults.colors().copy(
                    cardBackground = MaterialTheme.colorScheme.tertiaryContainer,
                    divider = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    indicatorActive = MaterialTheme.colorScheme.onTertiaryContainer,
                    indicatorInactive = MaterialTheme.colorScheme.onSurfaceVariant,
                    editIconTint = MaterialTheme.colorScheme.secondaryContainer,
                    expandCollapseTint = MaterialTheme.colorScheme.secondaryContainer,
                    titleText = MaterialTheme.colorScheme.secondaryContainer,
                    expandCollapseText = MaterialTheme.colorScheme.secondaryContainer,
                ),
                chipColors = AdaptiveChipDefaults.colors().copy(
                    unselectedContainer = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.5f),
                    selectedContainer = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedBorder = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedLabel = MaterialTheme.colorScheme.tertiaryContainer,
                    selectedLabel = MaterialTheme.colorScheme.tertiaryContainer,
                    checkBackground = MaterialTheme.colorScheme.onTertiaryContainer,
                    checkBorder = MaterialTheme.colorScheme.secondaryContainer,
                    checkIconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}