import kotlin.math.abs

fun main() {
    check(part1(readInput("Day23_test")) == 12521)
    check(part2(readInput("Day23_test2")) == 44169)

    println(part1(readInput("Day23")))
    println(part2(readInput("Day23_part2")))
}

private fun part1(input: List<String>): Int {
    return getAnswer(input, 2)
}

private fun part2(input: List<String>): Int {
    return getAnswer(input, 4)
}

private fun getAnswer(input: List<String>, roomSize: Int): Int {
    val hallPoints: List<Point> = listOf(0, 1, 3, 5, 7, 9, 10)
        .map { Point(it, 0) }

    val goalXCoords = listOf(2, 4, 6, 8)

    val roomRange = IntRange(1, roomSize)

    val goalPoints: List<List<Point>> = goalXCoords
        .map { x -> roomRange.map { Point(x, it) } }

    fun atGoal(positions: Map<Point, Byte>, point: Point, type: Byte): Boolean {
        if (goalXCoords[type.toInt()] != point.x)
            return false

        if (point.y == roomSize)
            return true

        return IntRange(point.y + 1, roomSize).all {
            positions[Point(point.x, it)] == type
        }
    }

    fun getGoalPoint(positions: Map<Point, Byte>, type: Byte): Point? {
        val x = goalXCoords[type.toInt()]
        if (!roomRange.all {
                val typeAtGoal = positions[Point(x, it)]
                typeAtGoal == null || typeAtGoal == type
            })
            return null

        return roomRange
            .reversed()
            .map { Point(x, it) }
            .firstOrNull { positions[it] == null }
    }

    fun getMinEnergy(positions: Map<Point, Byte>): Int? {
        var result = Int.MAX_VALUE

        var tempResult = 0
        val notAtGoalList = positions
            .filter { (point, type) -> !atGoal(positions, point, type) }
            .entries
            .toMutableList()
        val changedPositions: MutableMap<Point, Byte> = positions.toMutableMap()

        do {
            var moveCount = 0
            for (i in notAtGoalList.indices.reversed()) {
                val point = notAtGoalList[i].key
                if (!isBlocked(changedPositions, point)) {
                    val type = notAtGoalList[i].value
                    val goalPoint = getGoalPoint(changedPositions, type)
                    if (goalPoint != null && canMoveToPoint(changedPositions, point, goalPoint)) {
                        changedPositions.remove(point)
                        changedPositions[goalPoint] = type
                        tempResult += toPointEnergy(point, goalPoint, type)
                        moveCount++
                        notAtGoalList.removeAt(i)
                    }
                }
            }
        } while (moveCount > 0)

        if (changedPositions.all { (point, type) -> atGoal(changedPositions, point, type) }) {
            return tempResult
        }

        notAtGoalList
            .filter { (point, _) -> point.y != 0 && !isBlocked(changedPositions, point) }
            .forEach { (point, type) ->
                hallPoints
                    .filter { canMoveToPoint(changedPositions, point, it) }
                    .forEach {
                        val energy: Int? = getMinEnergy(
                            getNewPositions(changedPositions, point, it, type)
                        )
                        if (energy != null) {
                            val newResult = energy + toPointEnergy(point, it, type) + tempResult
                            if (newResult < result) {
                                result = newResult
                            }
                        }
                    }
            }

        return if (result != Int.MAX_VALUE) result else null
    }

    val positions: Map<Point, Byte> = getStartPositions(input, goalPoints)
    return getMinEnergy(positions)!!
}

private val typeCostMap: Map<Byte, Int> = mapOf(
    0.toByte() to 1, 1.toByte() to 10, 2.toByte() to 100, 3.toByte() to 1000)

private fun getStartPositions(
    input: List<String>,
    goalPoints: List<List<Point>>
): Map<Point, Byte> {
    return goalPoints
        .flatten()
        .associateWith { p ->
            when(input[p.y + 1][p.x + 1]) {
                'A' -> 0
                'B' -> 1
                'C' -> 2
                'D' -> 3
                else -> throw IllegalStateException()
            }
        }
}

//private fun visualisation(positions: Map<Point, Byte>) {
//    println("===================")
//    for (i in 0 .. 4) {
//        for (j in 0..11) {
//            print(positions[Point(j, i)] ?: " ")
//        }
//        println()
//    }
//}

private fun toPointLength(start: Point, end: Point) = (start.y + end.y + abs(start.x - end.x))

private fun toPointEnergy(start: Point, end: Point, type: Byte): Int {
    return toPointLength(start, end) * typeCostMap[type]!!
}

private fun getNewPositions(
    positions: Map<Point, Byte>,
    startPoint: Point,
    endPoint: Point,
    type: Byte
): Map<Point, Byte> {
    val newPositions: MutableMap<Point, Byte> = HashMap(positions)
    newPositions.remove(startPoint)
    newPositions[endPoint] = type
    return newPositions
}

private fun canMoveToPoint(positions: Map<Point, Byte>, start: Point, end: Point): Boolean {
    if (start == end)
        return false

    val range: IntRange = if (start.x < end.x)
        IntRange(start.x + 1, end.x)
    else
        IntRange(end.x, start.x - 1)

    return range.all { positions[Point(it, 0)] == null }
}

private fun isBlocked(positions: Map<Point, Byte>, point: Point): Boolean {
    if (point.y <= 1)
        return false
    return IntRange(1, point.y - 1)
        .any { positions[Point(point.x, it)] != null }
}
