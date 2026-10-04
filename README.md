# adaptive-chip-select

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Docs](https://img.shields.io/badge/Docs-vkozhanova.github.io%2FAdaptiveChips-blue)](https://vkozhanova.github.io/AdaptiveChips/)

Адаптивный селектор чипов для Compose с синхронным скроллом рядов,
магнитным снапом, индикатором и переключением свёрнутого / развёрнутого режима.

## Зачем

Стандартный `FlowRow` раскладывает чипы по строкам. Как только строк становится
больше одной, появляются ограничения:

| Чего нет у `FlowRow` | Что даёт `adaptive-chip-select`                            |
|---|------------------------------------------------------------|
| Ряды скроллятся независимо друг от друга | Один общий `ScrollState` и все ряды двигаются синхронно    |
| Скролл останавливается «где-то между» | Магнитный снап к ближайшей группе чипов                    |
| Не видно, есть ли ещё контент впереди | Индикатор с подсказкой о продолжении                       |
| Развернуть / свернуть — ваша задача | Готовый переход: 2–3 ряда со скроллом ↔ полный `FlowRow`   |
| Состояния выделения нет | `AdaptiveChipState` с режимами Single / Multiple / Limited |
| Число рядов не настраивается | Для этой задачи есть параметр `rowCount`                   |

Один компонент решает все эти задачи.

## Возможности

- **Свёрнутый режим** — N рядов чипов с одним общим `ScrollState`.
- **Произвольное количество рядов** — задаётся параметром `rowCount`.
- **Развёрнутый режим** — `FlowRow` в ограниченном контейнере.
- **Магнитный снап** к группам чипов после остановки скролла.
- **Индикатор** со скользящим окном: при большом числе групп показывает окно
  из 7 точек, крайняя правая уменьшена, пока есть куда скроллить.
  Количество видимых точек регулируется параметром maxVisibleDots (по умолчанию 7).
- **`AdaptiveChipState`** — Single / Multiple / Limited(min, max).
  Сохраняется через `rememberSaveable` и переживает пересоздание Activity.
- **Слоты** — можно использовать свой чип (`FilterChip`, `AssistChip`, что угодно),
  свой заголовок, своё пустое состояние.
- **Двухуровневая тема** — цвета и размеры отдельно для чипа и для контейнера.

## Требования

- minSdk 26
- Compose BOM 2024.09.00+
- Kotlin 2.0+

## Установка

Пакет публикуется в [GitHub Packages](https://github.com/vkozhanova/AdaptiveChips/packages).

### 1. Добавьте Maven-репозиторий

В `settings.gradle.kts`:

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

### 2. Добавьте зависимость

В `build.gradle.kts` модуля:

```kotlin
dependencies {
    implementation("io.github.vkozhanova:adaptive-chip-select:0.1.0")
}
```

### 3. Настройте доступ

GitHub Packages требует токен **даже для публичных пакетов**. Создайте
Personal Access Token со scope `read:packages`:

1. https://github.com/settings/tokens → `Generate new token (classic)`.
2. Scopes: `read:packages`.
3. Скопируйте токен (`ghp_...`).

Пропишите его в `~/.gradle/gradle.properties` — файл вне проекта, в git не попадёт:

```properties
gcp_username=your-github-username
gcp_token=ghp_ваш_токен
```

> Библиотека пока не опубликована в Maven Central.

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
        onEditCategory = { /* open edit */ },
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

`rememberAdaptiveChipsState` использует `rememberSaveable` — состояние
переживает поворот экрана и пересоздание Activity.

### Темизация

Есть два уровня темы:

- `AdaptiveChipTheme` — цвета и размеры самого чипа;
- `AdaptiveChipsTheme` — цвета и размеры контейнера (карточки, индикатора, divider'ов).

`AdaptiveChipsTheme(...)` для удобства умеет прокидывать оба уровня сразу:

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

Снап, скролл и индикатор работают так же, они не знают, как выглядит ваш чип.

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

Параметр влияет только на свёрнутый режим. В развёрнутом используется
`FlowRow`, число рядов определяется шириной контейнера.

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

## Ссылки

- [Исходный код](https://github.com/vkozhanova/AdaptiveChips)
- [Issues](https://github.com/vkozhanova/AdaptiveChips/issues)
- [Packages](https://github.com/vkozhanova/AdaptiveChips/packages)
- [Документация API](https://vkozhanova.github.io/AdaptiveChips/api/)

## Автор
Vera Kozhanova: [@vkozhanova](https://github.com/vkozhanova)

## Лицензия

Apache License 2.0. См. [LICENSE](LICENSE).