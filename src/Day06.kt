fun main() {
    fun part1(input: List<String>): Long {
        return calculate(input, 80)
    }

    fun part2(input: List<String>): Long {
        return calculate(input, 256)
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 5934L)
    check(part2(testInput) == 26984457539)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}

fun calculate(input: List<String>, dayCount: Int): Long {
    val timers: List<Int> = input[0].split(',').map { it.toInt() }
    val array: Array<Long> = Array(9){ i -> timers.count { it == i }.toLong()}
    for(i in 1..dayCount) {
        val newFishCount = array[0]
        for (j in 1..8) array[j - 1] = array[j]
        array[6] += newFishCount
        array[8] = newFishCount
    }
    return array.sum()
}