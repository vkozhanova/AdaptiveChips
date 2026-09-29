package io.github.vkozhanova.adaptivechipselect.components

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

/**
 * Модель чипа для [AdaptiveChipSelectBlock] и [AdaptiveChipScroller].
 *
 * @param id стабильный идентификатор. Используется как ключ в [AdaptiveChipState],
 *           а также как ключ кэша X-позиций в скроллере.
 *           Должен быть уникальным в пределах одного списка и не меняться
 *           между рекомпозициями.
 * @param title текст на чипе.
 * @param iconResId идентификатор drawable-ресурса для иконки слева.
 */
@Immutable
public data class AdaptiveChipItem(
    public val id: String,
    public val title: String,
    @DrawableRes public val iconResId: Int,
)