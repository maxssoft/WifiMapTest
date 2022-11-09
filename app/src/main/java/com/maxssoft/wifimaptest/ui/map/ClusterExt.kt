package com.maxssoft.wifimaptest.ui.map

import com.maxssoft.wifimaptest.R

/**
 * Функции расширения для упрощения работы с кластерами
 *
 * @author Сидоров Максим on 09.11.2022
 */

/**
 * возвращщает процент [value] от общего количества [this]
 */
fun Int.percent(value: Int): Int = value * 100 / this

/**
 * возвращает иконку в зависимости от процента
 */
fun Int.percentToDrawable(): Int =
    when {
        this > 40 -> R.drawable.ic_wifi_x5
        this > 15 -> R.drawable.ic_wifi_x4
        this > 5 -> R.drawable.ic_wifi_x3
        else -> R.drawable.ic_wifi_x2
    }

/**
 * возвращает иконку в зависимости от количества точек в кластере
 */
fun Int.countToDrawable(): Int =
    when {
        this > 1000 -> R.drawable.ic_wifi_x5
        this > 100 -> R.drawable.ic_wifi_x4
        this > 50 -> R.drawable.ic_wifi_x3
        this > 10 -> R.drawable.ic_wifi_x2
        else -> R.drawable.ic_wifi_x1
    }
