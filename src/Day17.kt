// Очень корявое решение с сильными привязками к входным данным
import java.lang.IllegalArgumentException
import kotlin.math.sqrt

fun main() {
    check(part1("target area: x=20..30, y=-10..-5") == 45)
    check(part2("target area: x=20..30, y=-10..-5") == 112)

    val input = readInput("Day17")
    println(part1(input[0]))
    println(part2(input[0]))
}

private fun part1(input: String): Int {
    val rectangle: Pair<Point, Point> = getDestRectangleCoords(input)

    val minVx: Int = ((-1 + sqrt(1.0 + 8 * rectangle.first.x)) / 2).toInt()
    val maxVx: Int = rectangle.second.x

    var maxHeight = 0
    for(startVx in minVx .. maxVx) {
        var startVy = 1
        var isUnder = false
        do {
            var x = 0
            var y = 0
            var vx = startVx
            var vy = startVy
            var newMaxHeight = 0
            do {
                if (vx == 0 && x < rectangle.first.x) {
                    isUnder = true
                    break
                }
                x += vx
                y += vy
                if (vx > 0) vx--
                vy--

                if (y > newMaxHeight) newMaxHeight = y

                if (x in rectangle.first.x .. rectangle.second.x
                    && y in rectangle.first.y .. rectangle.second.y) {
                    if (newMaxHeight > maxHeight)
                        maxHeight = newMaxHeight

                    break
                }
            } while (y >= rectangle.first.y && x <= rectangle.second.x)
            startVy++
        } while (!isUnder && x <= rectangle.second.x && startVy < 10000) // пока не знаю, как доказать, что ограничение в 10000 оправдано и каким оно должно быть
    }

    return maxHeight
}

private fun part2(input: String): Int {
    val rectangle: Pair<Point, Point> = getDestRectangleCoords(input)

    val minVx: Int = ((-1 + sqrt(1.0 + 8 * rectangle.first.x)) / 2).toInt()
    val maxVx: Int = rectangle.second.x

    var result = 0
    for(startVx in minVx .. maxVx) {
        var startVy = rectangle.first.y
        var isUnder = false
        do {
            var x = 0
            var y = 0
            var vx = startVx
            var vy = startVy
            var newMaxHeight = 0
            do {
                if (vx == 0 && x < rectangle.first.x) {
                    isUnder = true
                    break
                }
                x += vx
                y += vy
                if (vx > 0) vx--
                vy--

                if (y > newMaxHeight) newMaxHeight = y

                if (x in rectangle.first.x .. rectangle.second.x
                    && y in rectangle.first.y .. rectangle.second.y) {
                    result++
                    break
                }
            } while (y >= rectangle.first.y && x <= rectangle.second.x)
            startVy++
        } while (!isUnder && x <= rectangle.second.x && startVy < 10000) // пока не знаю, как доказать, что ограничение в 10000 оправдано и каким оно должно быть
    }
    return result
}

private val inputRegex = Regex("target area: x=(-?\\d+)\\.\\.(-?\\d+), y=(-?\\d+)\\.\\.(-?\\d+)")

private fun getDestRectangleCoords(input: String): Pair<Point, Point> {
    val m: MatchResult = inputRegex.matchEntire(input) ?: throw IllegalArgumentException()

    return Pair(
        Point(m.groupValues[1].toInt(), m.groupValues[3].toInt()),
        Point(m.groupValues[2].toInt(), m.groupValues[4].toInt())
    )
}
