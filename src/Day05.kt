import kotlin.math.abs
import kotlin.math.sign

fun main() {
    fun part1(input: List<String>): Int {
        return calculate(input) { arrayOf() }
    }

    fun part2(input: List<String>): Int {
        return calculate(input) {
            val difX = it.end.x - it.start.x
            val difY = it.end.y - it.start.y
            if (abs(difX) == abs(difY)) {
                val sgnX = difX.sign
                val sgnY = difY.sign
                return@calculate Array(abs(difX) + 1){ i ->
                    Point(it.start.x + i * sgnX, it.start.y + i * sgnY)
                }
            }
            else arrayOf()
        }
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

private data class Point(val x: Int, val y: Int)
private data class Line(val start: Point, val end: Point)

private fun calculate(input: List<String>, diagonalFunction: (Line) -> Array<Point>): Int {
    return input.asSequence()
        .map { s ->
            val list: List<Int> = s
                .split(",", " -> ")
                .map { it.toInt() }
            return@map Line(Point(list[0], list[1]), Point(list[2], list[3]))
        }
        .map {
            if (it.start.x == it.end.x) {
                val min = it.start.y.coerceAtMost(it.end.y)
                val max = it.start.y.coerceAtLeast(it.end.y)
                return@map Array(max - min + 1) { i -> Point(it.start.x, min + i) }
            }
            else if (it.start.y == it.end.y) {
                val min = it.start.x.coerceAtMost(it.end.x)
                val max = it.start.x.coerceAtLeast(it.end.x)
                return@map Array(max - min + 1) { i -> Point(min + i, it.start.y) }
            }
            else
                return@map diagonalFunction(it)
        }
        .fold(HashMap<Point, Int>()){ map, array ->
            array.forEach {
                val count = map[it]
                map[it] = if (count != null) count + 1 else 1
            }
            return@fold map
        }
        .count { it.value >= 2 }
}