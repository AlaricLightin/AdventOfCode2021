import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput1 = readInput("Day22_test1")
    check(part1(testInput1) == 39L)

    val testInput2 = readInput("Day22_test2")
    check(part1(testInput2) == 590784L)

    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Long {
    val cubes: List<Cube> = readData(input)
    return getSwitchedOnCount(cubes, cubes.size, listOf(
        Interval(-50, 50),
        Interval(-50, 50),
        Interval(-50, 50)
    ))
}

private fun part2(input: List<String>): Long {
    val cubes: List<Cube> = readData(input)
    return getSwitchedOnCount(cubes, cubes.size, listOf(
        Interval(Int.MIN_VALUE, Int.MAX_VALUE),
        Interval(Int.MIN_VALUE, Int.MAX_VALUE),
        Interval(Int.MIN_VALUE, Int.MAX_VALUE)
    ))
}

private fun getSwitchedOnCount(cubes: List<Cube>, stepCount: Int, area: List<Interval>): Long {
    if (stepCount == 0)
        return 0

    var result: Long = 0
    val innerCubes: MutableList<Cube> = mutableListOf()
    cubes
        .take(stepCount)
        .forEach {
            val newCoords: List<Interval> = area.mapIndexed { index, areaInterval ->
                areaInterval.intersection(it.coords[index])
            }
                .filterNotNull()

            if (newCoords.size == 3)
                innerCubes.add(Cube(newCoords, it.isOn))
        }

    innerCubes.forEachIndexed { index, cube ->
        result -= getSwitchedOnCount(innerCubes, index, cube.coords)
        if (cube.isOn)
            result += cube.volume()
    }

    return result
}

private data class Interval(val begin: Int, val end: Int) {
    fun intersection(interval: Interval): Interval? {
        val resultBegin = max(this.begin, interval.begin)
        val resultEnd = min(this.end, interval.end)
        return if (resultEnd >= resultBegin) Interval(resultBegin, resultEnd) else null
    }

    fun length(): Int = end - begin + 1
}

private data class Cube(val coords: List<Interval>, val isOn: Boolean) {
    fun volume(): Long = coords.fold(1L) { acc, interval -> acc * interval.length().toLong() }
}

private val inputRegex = Regex("(on|off) x=(-?\\d+)\\.\\.(-?\\d+),y=(-?\\d+)\\.\\.(-?\\d+),z=(-?\\d+)\\.\\.(-?\\d+)")

private fun readData(input: List<String>): List<Cube> {
    return input
        .map {
            val m: MatchResult = inputRegex.matchEntire(it) ?: throw IllegalArgumentException()
            Cube(
                listOf(
                    Interval(m.groupValues[2].toInt(), m.groupValues[3].toInt()),
                    Interval(m.groupValues[4].toInt(), m.groupValues[5].toInt()),
                    Interval(m.groupValues[6].toInt(), m.groupValues[7].toInt())
                ),
                m.groupValues[1] == "on"
            )

        }
}