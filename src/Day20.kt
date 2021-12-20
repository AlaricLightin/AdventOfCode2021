fun main() {
    val testInput = readInput("Day20_test")
    check(part1(testInput, 2) == 35)
    check(part1(testInput, 50) == 3351)

    val input = readInput("Day20")
    println(part1(input, 2))
    println(part1(input, 50))
}

private fun part1(input: List<String>, iterationCount: Int): Int {
    val algorithm = getAlgorithm(input[0])
    var matrix = getMatrix(input.drop(2))
    var borderValue = false
    for (i in 1..iterationCount) {
        matrix = createIncreasedMatrix(matrix, borderValue)
        matrix = iterate(matrix, algorithm, borderValue)
        borderValue = getBorderValue(borderValue, algorithm)
    }
    return matrix.sumOf { it.count { b -> b } }
}

private fun getAlgorithm(input: String): BooleanArray {
    return BooleanArray(512) { i -> input[i] == '#' }
}

private fun getMatrix(input: List<String>): Array<BooleanArray> {
    return Array(input.size) { i ->
        BooleanArray(input[i].length) { j -> input[i][j] == '#' }
    }
}

private fun createIncreasedMatrix(matrix: Array<BooleanArray>, borderValue: Boolean): Array<BooleanArray> {
    val inputRowCount = matrix.size
    val inputColumnCount = matrix.size
    return Array(inputRowCount + 4) { i ->
        if (i in 2 .. inputRowCount + 1)
            BooleanArray(inputColumnCount + 4 ) { j ->
                if (j in 2 .. inputColumnCount + 1)
                    matrix[i - 2][j - 2]
                else
                    borderValue
            }
        else
            BooleanArray(inputColumnCount + 4) { borderValue }
    }
}

private fun iterate(matrix: Array<BooleanArray>, algorithm: BooleanArray, borderValue: Boolean): Array<BooleanArray> {
    val rowCount = matrix.size
    val columnCount = matrix[0].size
    val newBorderValue = getBorderValue(borderValue, algorithm)

    return Array(columnCount) { i ->
        if (i in 1 .. rowCount - 2) {
            BooleanArray(columnCount) { j ->
                if (j in 1 .. columnCount - 2) {
                    val data: Array<Boolean> = arrayOf(
                        matrix[i - 1][j - 1], matrix[i - 1][j], matrix[i - 1][j + 1],
                        matrix[  i  ][j - 1], matrix[  i  ][j], matrix[  i  ][j + 1],
                        matrix[i + 1][j - 1], matrix[i + 1][j], matrix[i + 1][j + 1]
                    )
                    algorithm[calculateBitValue(data)]
                }
                else
                    newBorderValue
            }
        }
        else
            BooleanArray(columnCount) { newBorderValue }
    }
}

private fun calculateBitValue(data: Array<Boolean>): Int {
    return data.fold(0) { acc, b -> (acc shl 1) + (if (b) 1 else 0) }
}

private fun getBorderValue(currentBorderValue: Boolean, algorithm: BooleanArray): Boolean {
    return if(currentBorderValue) algorithm[511] else algorithm[0]
}