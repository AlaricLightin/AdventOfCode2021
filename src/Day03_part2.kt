import kotlin.math.sign

fun main() {
    fun part2(input: List<String>): Int {
        val oxygen = getResultForChar(input, '1')
        val co2 = getResultForChar(input, '0')
        return oxygen * co2
    }

    val testInput = readInput("Day03_test")
    check(part2(testInput) == 230)

    val input = readInput("Day03")
    println(part2(input))
}

fun filterList(list: List<String>, currentIndex: Int, defaultChar: Char): List<String> {
    if (list.size == 1)
        return list

    var difference = 0
    list.forEach {
        when(it[currentIndex]) {
            '0' -> difference--
            '1' -> difference++
        }
    }

    val keepChar: Char = if (difference != 0) {
        if ((defaultChar == '1').xor(difference.sign > 0)) '0' else '1'
    }
    else defaultChar

    return list.filter { it[currentIndex] == keepChar }
}

fun getResultForChar(list: List<String>, defaultChar: Char): Int {
    val size = list[0].length
    var currentList: List<String> = list
    for (index in 0 until size) {
        if (currentList.size == 1)
            break

        currentList = filterList(currentList, index, defaultChar)
    }

    check(currentList.size == 1)
    var result = 0
    currentList[0].forEachIndexed { index, c ->
        if (c == '1')
            result += (1 shl (size - 1 - index))
    }

    return result
}