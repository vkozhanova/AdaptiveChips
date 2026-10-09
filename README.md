# adaptive-chip-select

[![Release](https://img.shields.io/github/v/release/vkozhanova/AdaptiveChips)](https://github.com/vkozhanova/AdaptiveChips/releases)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Docs](https://img.shields.io/badge/Docs-vkozhanova.github.io%2FAdaptiveChips-blue)](https://vkozhanova.github.io/AdaptiveChips/)

**English** · [Русский](README.ru.md)

Adaptive chip selector for Compose with synchronized multi-row scrolling,
magnetic snap, pager indicator, and collapsible / expandable modes.

## Why

Standard `FlowRow` lays chips out across rows. Once you have more than one row,
limitations start to show:

| Problem | What `adaptive-chip-select` gives you |
|---|---|
| Rows scroll independently | A single shared `ScrollState` — all rows move together |
| Scroll stops "somewhere in between" | Magnetic snap to the nearest chip group |
| No hint that more content is off-screen | Indicator with a sliding window and edge hint |
| Collapse / expand is your job | Ready transition: 2–3 scrolling rows ↔ full `FlowRow` |
| No selection state | `AdaptiveChipState` with Single / Multiple / Limited modes |
| Row count not configurable | `rowCount` parameter |

One component solves all of these.

## Features

- **Collapsed mode** — N rows of chips sharing a single `ScrollState`.
- **Configurable row count** — via the `rowCount` parameter.
- **Expanded mode** — `FlowRow` inside a bounded container.
- **Magnetic snap** to chip groups after the scroll stops.
- **Indicator** with a sliding window: shows all dots when there are ≤ 7 groups;
  otherwise a 7-dot window centered on the current position. The rightmost dot
  is smaller while there is more content to scroll to.
- **`AdaptiveChipState`** — Single / Multiple / Limited(min, max).
  Backed by `rememberSaveable`, survives configuration changes.
- **Slots** — bring your own chip (`FilterChip`, `AssistChip`, anything),
  your own title, your own empty state.
- **Two-level theming** — colors and dimensions for the chip and for the
  container are configured separately.

## Requirements

- minSdk 26
- Compose BOM 2024.09.00+
- Kotlin 2.0+

## Installation

The package is published to [GitHub Packages](https://github.com/vkozhanova/AdaptiveChips/packages).

### 1. Add the Maven repository

In `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/vkozhanova/AdaptiveChips")
            credentials {
                username = providers.gradleProperty("gcp_username").orNull
                    ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gcp_token").orNull
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

### 2. Add the dependency

In the module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.vkozhanova:adaptive-chip-select:0.1.0")
}
```

### 3. Configure access

GitHub Packages requires a token **even for public packages**. Create a
Personal Access Token with the `read:packages` scope:

1. https://github.com/settings/tokens → `Generate new token (classic)`.
2. Scopes: `read:packages`.
3. Copy the token (`ghp_...`).

Put it in `~/.gradle/gradle.properties` — outside the project, so it never
reaches git:

```properties
gcp_username=your-github-username
gcp_token=ghp_your_token
```

## Quick start

```kotlin
@Composable
fun SimpleSelector() {
    var isExpanded by remember { mutableStateOf(false) }

    val state = rememberAdaptiveChipsState(
        initialSelectedIds = setOf("1", "3"),
        selectionMode = SelectionMode.Multiple,
    )

    val items = remember {
        listOf(
            AdaptiveChipItem("1", "Chip one", R.drawable.ic_chip_1),
            AdaptiveChipItem("2", "Chip two", R.drawable.ic_chip_2),
            AdaptiveChipItem("3", "Chip three", R.drawable.ic_chip_3),
        )
    }

    AdaptiveChipSelectBlock(
        title = "Simple Chip Category",
        items = items,
        state = state,
        isExpanded = isExpanded,
        onToggleExpanded = { isExpanded = !isExpanded },
        onEditCategory = { /* open edit */ },
    )
}
```

The component decides on its own:

- whether to show the "Expand" button — if chips don't fit;
- whether to draw the indicator — if rows exceed screen width;
- how to synchronize row scrolling;
- how to snap scroll to the nearest group.

## Usage

### Selection state

`AdaptiveChipState` tracks what is selected. Modes:

```kotlin
// One chip at a time
val state = rememberAdaptiveChipsState(selectionMode = SelectionMode.Single)

// Any number
val state = rememberAdaptiveChipsState(selectionMode = SelectionMode.Multiple)

// Between 1 and 3
val state = rememberAdaptiveChipsState(
    selectionMode = SelectionMode.limited(min = 1, max = 3),
)
```

API:

```kotlin
state.selectedIds            // Set<String> — current selection
state.selectionCount         // Int — number of selected items
state.isSelected("id")       // Boolean
state.toggle("id")           // toggle
state.select("id")           // select (respecting the mode)
state.deselect("id")         // deselect (respecting the mode)
state.clear()                // clear all
state.replace(setOf("1"))    // replace the selection
```

`rememberAdaptiveChipsState` is backed by `rememberSaveable` — the state
survives rotation and Activity recreation.

### Theming

There are two theme levels:

- `AdaptiveChipTheme` — colors and dimensions of the chip itself;
- `AdaptiveChipsTheme` — colors and dimensions of the container (card,
  indicator, dividers).

`AdaptiveChipsTheme(...)` provides both levels at once — the more convenient
entry point:

```kotlin
AdaptiveChipsTheme(
    colors = AdaptiveChipsDefaults.colors(),
    chipColors = AdaptiveChipDefaults.colors(),
) {
    // your chips
}
```

#### Colors from Material 3

If you already use a Material theme, there's a helper:

```kotlin
MaterialTheme(colorScheme = myScheme) {
    AdaptiveChipsTheme(
        colors = AdaptiveChipsDefaults.materialColors(),
        chipColors = AdaptiveChipDefaults.materialColors(),
    ) {
        // ...
    }
}
```

Chip and container colors are derived from `MaterialTheme.colorScheme`.

#### Custom colors

```kotlin
AdaptiveChipsTheme(
    colors = AdaptiveChipsDefaults.colors().copy(
        cardBackground = Color(0xFFFFF0F5),
        indicatorActive = Color(0xFFE91E63),
    ),
    chipColors = AdaptiveChipDefaults.colors().copy(
        selectedContainer = Color(0xFFE91E63).copy(alpha = 0.15f),
        selectedBorder = Color(0xFFE91E63),
        checkBackground = Color(0xFFE91E63),
    ),
) {
    // ...
}
```

### Bring your own chip

The library does not require `AdaptiveChip`. `AdaptiveChipScroller` accepts
a slot where you draw whatever you like:

```kotlin
AdaptiveChipScroller(
    items = myItems,
    state = state,
    key = { it.id },
    chip = { item, isSelected, onToggle ->
        FilterChip(
            selected = isSelected,
            onClick = onToggle,
            label = { Text(item.title) },
        )
    },
)
```

Snap, scroll, and the indicator work the same — they don't know what your
chip looks like.

### `AdaptiveChipSelectBlock` slots

```kotlin
AdaptiveChipSelectBlock(
    title = "Category Title",
    items = items,
    state = state,
    isExpanded = isExpanded,
    onToggleExpanded = { isExpanded = !isExpanded },
    onEditCategory = null,   // hide the edit icon
    titleContent = {
        // custom title with a badge
        Row {
            Text("Category Title", style = MaterialTheme.typography.titleMedium)
            Badge { Text("${state.selectionCount}") }
        }
    },
    emptyContent = {
        Text("No chips to select")
    },
)
```

### Row count

By default, collapsed mode shows two rows of chips. The number of rows is
controlled by `rowCount`:

```kotlin
// Single row — a horizontal ribbon
AdaptiveChipSelectBlock(
    title = "Simple Chip Category",
    items = items,
    state = state,
    isExpanded = false,
    onToggleExpanded = {},
    rowCount = 1,
)

// Three rows
AdaptiveChipSelectBlock(
    // ...
    rowCount = 3,
)
```

The parameter affects collapsed mode only. In expanded mode, `FlowRow` is
used and the row count is determined by container width.

## Structure

```
adaptive-chip-select/
├── components/
│   ├── AdaptiveChip.kt             — base chip
│   ├── AdaptiveChipItem.kt         — model
│   ├── AdaptiveChipScroller.kt     — synchronized scroll + snap
│   ├── AdaptiveChipSelectBlock.kt  — card with collapse/expand
│   ├── AdaptiveChipState.kt        — selection state
│   ├── ChipPagerIndicator.kt       — indicator
│   └── ScrollInfo.kt               — scroll position
└── theme/
    ├── AdaptiveChipTheme.kt        — chip theme
    └── AdaptiveChipsTheme.kt       — container theme
```

## Build

```bash
./gradlew :adaptive-chip-select:build
./gradlew :adaptive-chip-select:test
```

## Links

- [API documentation](https://vkozhanova.github.io/AdaptiveChips/api/)
- [Releases](https://github.com/vkozhanova/AdaptiveChips/releases)
- [Packages](https://github.com/vkozhanova/AdaptiveChips/packages)
- [Issues](https://github.com/vkozhanova/AdaptiveChips/issues)

## Author

Vera Kozhanova: [@vkozhanova](https://github.com/vkozhanova)

## License

Apache License 2.0. See [LICENSE](LICENSE).