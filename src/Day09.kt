fun main() {
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val matrix: Array<ByteArray> = createMatrix(input)

    var result = 0
    for (i in 1 .. matrix.size - 2) {
        for (j in 1 .. matrix[0].size - 2) {
            val data = matrix[i][j]
            if (data < matrix[i - 1][j] && data < matrix[i + 1][j]
                && data < matrix[i][j - 1] && data < matrix[i][j + 1])
                result += (data + 1)
        }
    }
    return result
}

private const val MAX_HEIGHT: Byte = 9

private fun part2(input: List<String>): Int {
    val matrix: Array<ByteArray> = createMatrix(input)
    val lowPoints: MutableSet<Point> = mutableSetOf()

    fun findAdjacentLowPoints(x: Int, y: Int): Set<Point> {
        if (lowPoints.contains(Point(x, y)))
            return setOf(Point(x, y))

        val result = mutableSetOf<Point>()
        val data = matrix[x][y]
        if (matrix[x - 1][y] < data)
            result.addAll(findAdjacentLowPoints(x - 1, y))

        if (matrix[x + 1][y] < data)
            result.addAll(findAdjacentLowPoints(x + 1, y))

        if (matrix[x][y - 1] < data)
            result.addAll(findAdjacentLowPoints(x, y - 1))

        if (matrix[x][y + 1] < data)
            result.addAll(findAdjacentLowPoints(x, y + 1))

        return result
    }

    for (i in 1 .. matrix.size - 2) {
        for (j in 1 .. matrix[0].size - 2) {
            val data = matrix[i][j]
            if (data < matrix[i - 1][j] && data < matrix[i + 1][j]
                && data < matrix[i][j - 1] && data < matrix[i][j + 1])
                lowPoints.add(Point(i, j))
        }
    }

    val basinMap: MutableMap<Point, Int> = mutableMapOf()
    for (i in 1 .. matrix.size - 2) {
        for (j in 1 .. matrix[0].size - 2) {
            if (matrix[i][j] == MAX_HEIGHT)
                continue

            val adjacentLowPoints: Set<Point> = findAdjacentLowPoints(i, j)
            if (adjacentLowPoints.size == 1) {
                adjacentLowPoints.forEach{point ->
                    val currentValue = basinMap[point]
                    basinMap[point] = if (currentValue != null) currentValue + 1 else 1
                }
            }
        }
    }

    return basinMap.asSequence()
        .map { it.value }
        .sortedDescending()
        .take(3)
        .fold(1) { acc, i -> acc * i }
}

private fun createMatrix(input: List<String>): Array<ByteArray> {
    val rowCount = input.size
    val columnCount = input[0].length
    return Array(rowCount + 2) { i ->
        if (i != 0 && i != rowCount + 1) {
            ByteArray(columnCount + 2) { j ->
                if (j != 0 && j != columnCount + 1) input[i - 1][j - 1].digitToInt().toByte() else 9
            }
        }
        else ByteArray(columnCount + 2) { 9 }
    }
}
