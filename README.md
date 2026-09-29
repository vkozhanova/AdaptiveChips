# adaptive-chip-select

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Адаптивный селектор чипов для Compose с синхронным скроллом рядов,
магнитным снапом, индикатором и переключением свёрнутого / развёрнутого режима.

## Зачем

Стандартный `FlowRow` раскладывает чипы по строкам, но как только строк
становится больше одной, вы упираетесь в ограничения:

- строки не скроллятся вместе и каждая едет сама по себе;
- нет магнитного снапа, и остановка скролла всегда происходит «где-то между»;
- нет индикатора, который честно говорит «есть ли ещё контент впереди»;
- переход «свернутое → развернутое» приходится писать руками;
- состояние выделения (single / multiple / с ограничениями) — тоже ваша задача.

**adaptive-chip-select** закрывает всё это. Один компонент решает все шесть проблем.

### Сравнение

| Возможность | `FlowRow` | `LazyRow` | **adaptive-chip-select** |
|---|---|---|---|
| Много рядов | ✅ | ❌ | ✅ |
| Синхронный скролл рядов | ❌ | — | ✅ |
| Магнитный снап | ❌ | ⚠️ | ✅ |
| Индикатор с подсказкой | ❌ | ❌ | ✅ |
| Свёрнуто / развёрнуто | ❌ | ❌ | ✅ |
| Состояние выделения | ❌ | ❌ | ✅ |

## Возможности

- **Свёрнутый режим** — N рядов чипов с одним общим `ScrollState`.
- **Произвольное количество строк** — пользователь сам задает их число.
- **Развёрнутый режим** — `FlowRow` в ограниченном контейнере.
- **Магнитный снап** к группам чипов после остановки скролла.
- **Индикатор** со скользящим окном: при большом числе групп показывает окно
  из 7 точек, крайняя правая уменьшена, пока есть куда скроллить.
- **`AdaptiveChipState`** — Single / Multiple / Limited(min, max).
  AdaptiveChipState — Single / Multiple / Limited(min, max), сохраняется через `rememberSaveable`.
- **Слоты** — можно использовать свой чип (`FilterChip`, `AssistChip`, что угодно),
  свой заголовок, своё пустое состояние.
- **Двухуровневая тема** — цвета и размеры отдельно для чипа и для контейнера.

## Требования

- minSdk 26
- Compose BOM 2024.09.00+
- Kotlin 2.0+

## Установка

```kotlin
// build.gradle.kts (module)
dependencies {
    implementation("io.github.vkozhanova:adaptive-chip-select:0.1.0")
}
```

> Версия `0.1.0` — первая публичная.

## Быстрый старт

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
        onEditCategory = { /* открыть редактирование */ },
    )
}
```

Компонент сам решит:

- показывать ли кнопку «Развернуть» — если чипы не влезают;
- рисовать ли индикатор — если ряды длиннее экрана;
- синхронизировать скролл рядов;
- магнитить скролл к ближайшей группе.

## Использование

### Состояние выделения

`AdaptiveChipState` управляет тем, что выделено. Режимы:

```kotlin
// Один чип за раз
val state = rememberAdaptiveChipsState(selectionMode = SelectionMode.Single)

// Любое количество
val state = rememberAdaptiveChipsState(selectionMode = SelectionMode.Multiple)

// От 1 до 3
val state = rememberAdaptiveChipsState(
    selectionMode = SelectionMode.limited(min = 1, max = 3),
)
```

API:

```kotlin
state.selectedIds            // Set<String> — текущий набор
state.selectionCount         // Int — сколько выделено
state.isSelected("id")       // Boolean
state.toggle("id")           // переключить
state.select("id")           // выделить (с учётом режима)
state.deselect("id")         // снять выделение (с учётом режима)
state.clear()                // снять всё
state.replace(setOf("1"))    // заменить набор
```

- rememberAdaptiveChipsState использует rememberSaveable — состояние
переживает поворот экрана и пересоздание Activity.

### Темизация

Есть два уровня темы:

- `AdaptiveChipTheme` — цвета и размеры самого чипа.
- `AdaptiveChipsTheme` — цвета и размеры контейнера (карточки, индикатора, divider'ов).

`AdaptiveChipsTheme(...)` умеет прокидывать оба уровня сразу — так удобнее:

```kotlin
AdaptiveChipsTheme(
    colors = AdaptiveChipsDefaults.colors(),
    chipColors = AdaptiveChipDefaults.colors(),
) {
    // ваши чипы
}
```

#### Цвета из Material 3

Если у вас Material-тема, есть готовый хелпер:

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

Цвета чипов и контейнера будут выведены из `MaterialTheme.colorScheme`.

#### Свои цвета

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

### Свой чип

Библиотека не требует использовать именно `AdaptiveChip`. Компонент
`AdaptiveChipScroller` принимает слот, где вы рисуете что угодно:

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

Снап, скролл и индикатор работают так же — они не знают, как выглядит ваш чип.

### Слоты `AdaptiveChipSelectBlock`

```kotlin
AdaptiveChipSelectBlock(
    title = "Category Title",
    items = items,
    state = state,
    isExpanded = isExpanded,
    onToggleExpanded = { isExpanded = !isExpanded },
    onEditCategory = null,   // скрыть иконку редактирования
    titleContent = {
        // свой заголовок с бейджем
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

### Количество рядов

По умолчанию свёрнутый режим показывает два ряда чипов. Число рядов задаётся
параметром `rowCount`:

```kotlin
// Один ряд — горизонтальная лента
AdaptiveChipSelectBlock(
    title = "Simple Chip Category",
    items = items,
    state = state,
    isExpanded = false,
    onToggleExpanded = {},
    rowCount = 1,
)

// Три ряда
AdaptiveChipSelectBlock(
    // ...
    rowCount = 3,
)
```

## Структура

```
adaptive-chip-select/
├── components/
│   ├── AdaptiveChip.kt             — базовый чип
│   ├── AdaptiveChipItem.kt         — модель
│   ├── AdaptiveChipScroller.kt     — синхронный скролл + снап 
│   ├── AdaptiveChipSelectBlock.kt  — карточка с раскрытием 
│   ├── AdaptiveChipState.kt        — состояние выделения
│   ├── ChipPagerIndicator.kt       — индикатор
│   └── ScrollInfo.kt               — позиция скролла
└── theme/
    ├── AdaptiveChipTheme.kt        — тема чипа
    └── AdaptiveChipsTheme.kt       — тема контейнера
```

## Сборка

```bash
./gradlew :adaptive-chip-select:build
./gradlew :adaptive-chip-select:test
```

## Лицензия

Apache License 2.0. См. [LICENSE](LICENSE).