fun main() {
    fun part1(inputFilename: String): Int {
        var horizontal = 0
        var vertical = 0

        getInputFile(inputFilename).forEachLine {
            val command = it.substringBefore(' ')
            val value = it.substringAfter(' ').toInt()
            when (command) {
                "forward" -> horizontal += value
                "down" -> vertical += value
                "up" -> vertical -= value
            }
        }
        return horizontal * vertical
    }

    fun part2(inputFilename: String): Long {
        var horizontal = 0
        var depth = 0
        var aim = 0

        getInputFile(inputFilename).forEachLine {
            val command = it.substringBefore(' ')
            val value = it.substringAfter(' ').toInt()
            when(command) {
                "forward" -> {
                    horizontal += value
                    depth += aim * value
                }
                "down" -> aim += value
                "up" -> aim -= value
            }
        }

        return horizontal.toLong() * depth
    }

    check(part1("Day02_test") == 150)
    check(part2("Day02_test") == 900L)

    println(part1("Day02"))
    println(part2("Day02"))
}