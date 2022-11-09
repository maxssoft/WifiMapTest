package com.maxssoft.wifimaptest.domain.model

/**
 * Координаты области карты
 *
 * @author Сидоров Максим on 08.11.2022
 */
data class MapArea(
    val minLatitude: Double,
    val minLongitude:Double,
    val maxLatitude: Double,
    val maxLongitude: Double
)

/**
 * Возвращает размер шага latitude для [MapArea] поделенной на [count] частей
 */
fun MapArea.getLatitudeStep(count: Int) = (maxLatitude - minLatitude) / count

/**
 * Возвращает размер шага longitude для [MapArea] поделенной на [count] частей
 */
fun MapArea.getLongitudeStep(countQuadrant: Int) = (maxLongitude - minLongitude) / countQuadrant
