fun main() {
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)
    check(part2(testInput) == 315)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val rowCount = input.size
    val columnCount = input[0].length
    val matrix: Array<ByteArray> = Array(rowCount) { i ->
        ByteArray(columnCount) { j -> input[i][j].digitToInt().toByte() }
    }

    return findPathValue(matrix)
}

private fun part2(input: List<String>): Int {
    val inputRowCount = input.size
    val inputColumnCount = input[0].length
    val matrix: Array<ByteArray> = Array(inputRowCount * 5) { i ->
        ByteArray(inputColumnCount * 5) { j ->
            val inputValue = input[i % inputRowCount][j % inputColumnCount].digitToInt().toByte()
            val addValue = i / inputRowCount + j / inputColumnCount
            return@ByteArray ((inputValue - 1 + addValue) % 9 + 1).toByte()
        }
    }

    return findPathValue(matrix)
}

private fun findPathValue(matrix: Array<ByteArray>): Int {
    val rowCount = matrix.size
    val columnCount = matrix[0].size

    fun getAdjacent(x: Int, y: Int): List<Point> {
        val result = mutableListOf<Point>()
        if (y < rowCount - 1)
            result.add(Point(x, y + 1))
        if (x < columnCount - 1)
            result.add(Point(x + 1, y))
        if (y > 0)
            result.add(Point(x, y - 1))
        if (x > 0)
            result.add(Point(x - 1, y))

        return result
    }

    val pointsValueMap: MutableMap<Point, Int> = mutableMapOf(Point(0, 0) to 0)
    val visitedPoints: MutableSet<Point> = mutableSetOf()
    val allPointsCount = rowCount * columnCount
    val endPoint = Point(columnCount - 1, rowCount - 1)

    while (visitedPoints.size < allPointsCount) {
        val minPointEntry = pointsValueMap
            .minByOrNull { it.value } ?: break
        val p: Point = minPointEntry.key
        val pointValue = minPointEntry.value

        if (p == endPoint)
            return pointValue

        getAdjacent(p.x, p.y)
            .filter { !visitedPoints.contains(it) }
            .forEach{
                val v = pointsValueMap[it]
                val newLength = pointValue + matrix[it.y][it.x]
                if (v == null || v > newLength)
                    pointsValueMap[it] = newLength
            }

        visitedPoints.add(p)
        pointsValueMap.remove(p)
    }

    return Int.MAX_VALUE // по идее, это всё равно никогда не вызовется
}