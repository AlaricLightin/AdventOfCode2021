fun main() {
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val rowCount = input.size
    val colCount = input[0].length
    val matrix: Array<ByteArray> = Array(rowCount) { i ->
        ByteArray(colCount) { j -> input[i][j].digitToInt().toByte() }
    }

    var result = 0
    for(i in 1 .. 100) {
        result += doStepAndGetFlashCount(matrix)
    }

    return result
}

private fun part2(input: List<String>): Int {
    val rowCount = input.size
    val colCount = input[0].length
    val matrix: Array<ByteArray> = Array(rowCount) { i ->
        ByteArray(colCount) { j -> input[i][j].digitToInt().toByte() }
    }

    val allCount = rowCount * colCount
    var result = 0
    do {
        val flashCount = doStepAndGetFlashCount(matrix)
        result++
    } while (flashCount != allCount)

    return result
}

private fun doStepAndGetFlashCount(matrix: Array<ByteArray>): Int {
    val rowCount = matrix.size
    val colCount = matrix[0].size
    var result = 0
    matrix.forEach { row -> row.forEachIndexed { index, _ -> row[index]++ } }

    val fullFlashSet = mutableSetOf<Point>()
    do {
        val flashList = mutableListOf<Point>()
        matrix.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, value -> if (value > 9) flashList.add(Point(colIndex, rowIndex)) }
        }

        fullFlashSet.addAll(flashList)

        flashList.forEach { flashPoint ->
            getAdjacent(flashPoint.x, flashPoint.y, rowCount, colCount)
                .filter { !fullFlashSet.contains(it) }
                .forEach { matrix[it.y][it.x]++ }

            matrix[flashPoint.y][flashPoint.x] = 0
        }

        result += flashList.size
    } while (flashList.isNotEmpty())

    return result
}

private fun getAdjacent(x: Int, y: Int, rowCount: Int, colCount: Int): List<Point> {
    return listOf(
        Point(x - 1, y - 1),
        Point(x - 1, y),
        Point(x - 1, y + 1),
        Point(x, y - 1),
        Point(x, y + 1),
        Point(x + 1, y - 1),
        Point(x + 1, y),
        Point(x + 1, y + 1),
    )
        .filter { p -> p.x >= 0 && p.y >= 0 && p.x < colCount && p.y < rowCount }
}