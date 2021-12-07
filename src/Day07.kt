import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        return calculate(input[0]) { abs(it) }
    }

    fun part2(input: List<String>): Int {
        return calculate(input[0]) { diff ->
            val length = abs(diff)
            length * (length + 1) / 2
        }
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}

fun calculate(inputString: String, fuelFunction: (Int) -> Int): Int {
    val list = inputString
        .split(',')
        .map { it.toInt() }
    val positionMap: Map<Int, Int> = list
        .groupingBy { it }
        .eachCount()
    val min: Int = list.minOf { it }
    val max: Int = list.maxOf { it }

    var result = Int.MAX_VALUE
    for (i in min .. max) {
        var currentResult = 0
        positionMap.forEach { (pos, count) ->
            currentResult += fuelFunction(pos - i) * count
        }
        if (currentResult < result)
            result = currentResult
    }
    return result
}