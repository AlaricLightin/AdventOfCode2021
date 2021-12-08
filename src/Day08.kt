fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { it.substringAfter(" | ") }
            .flatMap { it.split(" ") }
            .map { it.length }
            .count { it == 2 || it == 3 || it == 4 || it == 7 }
    }

    fun part2(input: List<String>) : Int {
        return input.sumOf { decodeString(it) }
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

private fun decodeString(input: String): Int {
    val allDigitsList: List<Set<Char>> = input.substringBefore(" | ")
        .split(" ")
        .map { stringToSet(it) }

    val patterns: Array<Set<Char>> = Array(10) { emptySet() }
    patterns[1] = allDigitsList.first { it.size == 2 }
    patterns[4] = allDigitsList.first { it.size == 4 }
    patterns[7] = allDigitsList.first { it.size == 3 }
    patterns[8] = allDigitsList.first { it.size == 7 }

    patterns[3] = allDigitsList.first { it.size == 5 && it.containsAll(patterns[1]) }
    patterns[9] = allDigitsList.first { it.size == 6 && it.containsAll(patterns[4]) }
    patterns[6] = allDigitsList.first { it.size == 6 && (it - patterns[1]).size == 5 }
    patterns[0] = allDigitsList.first { it.size == 6 && it != patterns[9] && it != patterns[6] }
    patterns[2] = allDigitsList.first { it.size == 5 && it != patterns[3] && it.intersect(patterns[9]) != it }
    patterns[5] = allDigitsList.first { it.size == 5 && it != patterns[2] && it != patterns[3] }

    return input.substringAfter(" | ")
        .split(" ")
        .map { stringToSet(it) }
        .map { charSet -> patterns.indexOfFirst { it == charSet } }
        .fold(0) { acc, i -> acc * 10 + i }
}

private fun stringToSet(input: String): Set<Char> {
    return input.fold(HashSet()){ set, char ->
        set.add(char)
        set
    }
}