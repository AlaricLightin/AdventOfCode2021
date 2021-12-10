fun main() {
    check(part1("Day10_test") == 26397)
    check(part2("Day10_test") == 288957L)

    println(part1("Day10"))
    println(part2("Day10"))
}

val OPENING_BRACES = setOf('(', '[', '{', '<')
val BRACES_MAP = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
val BRACES_PRICE_WRONG = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
val BRACES_PRICE_COMPLETE = mapOf('(' to 1, '[' to 2, '{' to 3, '<' to 4)

fun part1(inputFilename: String): Int {
    var result = 0
    getInputFile(inputFilename).forEachLine {
        val stack = ArrayDeque<Char>()
        it.forEach { char ->
            if (OPENING_BRACES.contains(char))
                stack.addLast(char)
            else {
                val fromStackChar = stack.removeLast()
                if (BRACES_MAP[fromStackChar] != char) {
                    result += BRACES_PRICE_WRONG[char]!!
                    return@forEach
                }
            }
        }
    }
    return result
}

fun part2(inputFilename: String): Long {
    val resultList = mutableListOf<Long>()
    getInputFile(inputFilename).forEachLine {
        val stack = ArrayDeque<Char>()
        var isWrong = false
        it.forEach { char ->
            if (OPENING_BRACES.contains(char))
                stack.addLast(char)
            else {
                val fromStackChar = stack.removeLast()
                if (BRACES_MAP[fromStackChar] != char) {
                    isWrong = true
                    return@forEach
                }
            }
        }

        if (!isWrong) {
            resultList.add(
                stack.reversed()
                    .fold(0) { acc, c -> acc * 5 + BRACES_PRICE_COMPLETE[c]!! }
            )
        }
    }

    resultList.sort()
    return resultList[resultList.size / 2]
}