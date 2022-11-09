package com.maxssoft.wifimaptest.ui.map

/**
 * Типы алгоритмов кластеризации
 *
 * @author Сидоров Максим on 10.11.2022
 */
sealed class ClusterAlgorithm {
    /**
     * Алгоритм на основе голого SQL, очень быстрый, практически не зависит от числа точек в базе, может переваривать объемы в сотни раз больше
     *
     * Разбивает область карты на 10 x 10 квадратов (задается константой) и каждый квадрат обсчитывает sql запрсом вида
     * select count(*), avg(latitude), avg(longitude)
     * Все запросы объединяются в один запрос через UNION и выполняются одним sql запросом
     *
     * @param countSquare (countSquare = 10, будет 10 х 10 частей)
     */
    class Sql(val countSquare: Int = SQL_COUNT_SQUARE) : ClusterAlgorithm()

    /**
     * Комбинированный алгорит на основе SQL [Sql]
     * На больших масштабах использует для загрузки алгорит [Sql], на маленьких масштабах использует алгоритм [Google]
     *
     * @param countSquare (countSquare = 10, будет 10 х 10 частей)
     * @param googleActivateZoom zoom больше которого включается Google алгоритм кластеризации (не поддерживает слишком большое число точек)
     */
    class SqlCombo(val countSquare: Int = SQL_COUNT_SQUARE, val googleActivateZoom: Float = GOOGLE_ACTIVATE_ZOOM) : ClusterAlgorithm()

    /**
     * Стандартный Google алгоритм кластеризации
     */
    object Google : ClusterAlgorithm()
}

private const val SQL_COUNT_SQUARE = 10
private const val GOOGLE_ACTIVATE_ZOOM = 6.0F