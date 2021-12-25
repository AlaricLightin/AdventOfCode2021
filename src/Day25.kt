fun main() {
    val testInput = readInput("Day25_test")
    check(part1(testInput) == 58)

    val input = readInput("Day25")
    println(part1(input))
}

private fun part1(input: List<String>): Int {
    val rowCount = input.size
    val columnCount = input[0].length
    val matrix: Array<ByteArray> = Array(rowCount) { row ->
        ByteArray(columnCount) { column ->
            when(input[row][column]) {
                '>' -> 1
                'v' -> 2
                else -> 0
            }
        }
    }

    val moveEastFunction: (Point) -> Point = { p -> Point((p.x + 1) % columnCount, p.y) }
    val moveSouthFunction: (Point) -> Point = { p -> Point(p.x, (p.y  + 1) % rowCount) }

    fun getMoving(type: Byte, moveFunction: (Point) -> Point): List<Point> {
        val result: MutableList<Point> = mutableListOf()
        matrix.forEachIndexed { row, array ->
            array.forEachIndexed { column, value ->
                if (value == type) {
                    val point = Point(column, row)
                    val goal = moveFunction(point)
                    if (matrix[goal.y][goal.x] == 0.toByte())
                        result.add(point)
                }
            }
        }
        return result
    }

    fun move(type: Byte, moving: List<Point>, moveFunction: (Point) -> Point) {
        moving.forEach {
            matrix[it.y][it.x] = 0
            val goal = moveFunction(it)
            matrix[goal.y][goal.x] = type
        }
    }

    var result = 0
    do {
        var moving = getMoving(1, moveEastFunction)
        val movingEastCount = moving.size
        move(1, moving, moveEastFunction)

        moving = getMoving(2, moveSouthFunction)
        move(2, moving, moveSouthFunction)
        result++
    } while (movingEastCount > 0 || moving.isNotEmpty())

    return result
}

