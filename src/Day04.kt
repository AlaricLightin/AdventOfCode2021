fun main() {
    fun part1(input: List<String>): Int {
        val numArray: Array<Int> = input[0].split(',').map(String::toInt).toTypedArray()
        val numMap: HashMap<Int, MutableList<NumPosition>> = HashMap(numArray.size)
        numArray.forEach { numMap[it] = mutableListOf() }

        val boardSums: ArrayList<Int> = ArrayList()
        loadData(input, numMap, boardSums)
        val bingoCounters: List<BingoCounter> = List(boardSums.size) { BingoCounter() }

        numArray.forEach {
            val list = numMap[it]
            list?.forEach { numPosition ->
                val board = numPosition.board
                boardSums[board] -= it
                bingoCounters[board].apply {
                    rows[numPosition.row]++
                    if (rows[numPosition.row] == 5)
                        return boardSums[board] * it

                    columns[numPosition.column]++
                    if (columns[numPosition.column] == 5)
                        return boardSums[board] * it
                }
            }
        }

        return 0
    }

    fun part2(input: List<String>): Int {
        val numArray: Array<Int> = input[0].split(',').map(String::toInt).toTypedArray()
        val numMap: HashMap<Int, MutableList<NumPosition>> = HashMap(numArray.size)
        numArray.forEach { numMap[it] = mutableListOf() }

        val boardSums: ArrayList<Int> = ArrayList()
        loadData(input, numMap, boardSums)
        val bingoCounters: List<BingoCounter> = List(boardSums.size) { BingoCounter() }
        val boardClosedArray: Array<Boolean> = Array(boardSums.size) { false }
        var boardsLeft = boardClosedArray.size

        numArray.forEach {
            val list = numMap[it]
            list?.forEach { numPosition ->
                val board = numPosition.board
                if (!boardClosedArray[board]) {
                    boardSums[board] -= it
                    bingoCounters[board].apply {
                        rows[numPosition.row]++
                        columns[numPosition.column]++
                        if (rows[numPosition.row] == 5 || columns[numPosition.column] == 5) {
                            boardClosedArray[board] = true
                            boardsLeft--

                            if (boardsLeft == 0)
                                return it * boardSums[board]
                        }
                    }
                }
            }
        }

        return 0
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

private data class NumPosition(val board: Int, val row: Int, val column: Int)

@Suppress("ArrayInDataClass")
private data class BingoCounter(
    val rows: Array<Int> = Array(5) { 0 },
    val columns: Array<Int> = Array(5) { 0 }
)

private fun loadData(
    input: List<String>,
    numMap: HashMap<Int, MutableList<NumPosition>>,
    boardSums: ArrayList<Int>
) {
    var currentBoardIndex = -1
    var currentRow = 0
    var currentBoardSum = 0
    for (i in 1 until input.size) {
        val s = input[i]
        if (s.isBlank()) {
            if (currentBoardIndex >= 0) {
                boardSums.add(currentBoardSum)
                currentBoardSum = 0
            }

            currentBoardIndex++
            currentRow = 0
        }
        else {
            s
                .split(' ')
                .filter { it.isNotBlank() }
                .map(String::toInt)
                .forEachIndexed { index, j ->
                    val list = numMap[j]
                    list?.add(NumPosition(currentBoardIndex, currentRow, index))

                    currentBoardSum += j
                }
            currentRow++
        }
    }

    boardSums.add(currentBoardSum)
}