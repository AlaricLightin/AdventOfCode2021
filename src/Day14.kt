fun main() {
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588)
    check(part2(testInput, 10) == 1588L)
    check(part2(testInput, 40) == 2188189693529)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input, 40))
}

private fun part1(input: List<String>): Int {
    var string = input[0]
    val rules: Map<String, Char> = readRules(input)

    for (i in 1 .. 10) {
        val stringBuilder: StringBuilder = StringBuilder(string[0].toString())
        for (index in 1 until string.length) {
            val c = string[index]
            val pair: String = string[index - 1].plus(c.toString())
            val newChar = rules[pair]
            if (newChar != null)
                stringBuilder.append(newChar)
            stringBuilder.append(c)
        }

        string = stringBuilder.toString()
    }

    val charMap: Map<Char, Int> = string
        .groupingBy { it }
        .eachCount()

    return charMap.maxOf { it.value } - charMap.minOf { it.value }
}

private fun part2(input: List<String>, iterationCount: Int): Long {
    val string = input[0]
    val rules: Map<String, Char> = readRules(input)
    val tempResultMap: MutableMap<Pair<String, Int>, Map<Char, Long>> = mutableMapOf()

    fun countCharForPair(pair: String, depth: Int): Map<Char, Long> {
        var result = tempResultMap[pair to depth]
        if (result != null)
            return result

        if (depth == iterationCount)
            return mapOf(pair[0] to 1)

        val char = rules[pair]
        return if (char != null) {
            result = mapPlus(
                countCharForPair(pair[0].plus(char.toString()), depth + 1),
                countCharForPair(char.plus(pair[1].toString()), depth + 1)
            )
            tempResultMap[pair to depth] = result
            result
        } else
        // Последний символ не учитывается, потому что он учитывается потом
            mapOf(pair[0] to 1)
    }

    var resultMap: Map<Char, Long> = mapOf()
    for (i in 0 .. string.length - 2) {
        resultMap = mapPlus(resultMap, countCharForPair(string.substring(i .. i + 1), 0))
    }

    resultMap = mapPlus(resultMap, mapOf(string.last() to 1))
    return resultMap.maxOf { it.value } - resultMap.minOf { it.value }
}

private fun mapPlus(mapA: Map<Char, Long>, mapB: Map<Char, Long>): Map<Char, Long> {
    return mapA.keys.plus(mapB.keys)
        .associateWith { mapA.getOrDefault(it, 0) + mapB.getOrDefault(it, 0) }
}

private fun readRules(input: List<String>): Map<String, Char> {
    return input
        .drop(2)
        .associate { Pair(it.substringBefore(" -> "), it.substringAfter(" -> ")[0]) }
}