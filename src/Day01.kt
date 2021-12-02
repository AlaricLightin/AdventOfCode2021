import java.util.*

fun main() {
    fun part1(inputFilename: String): Int {
        var previous: Int? = null
        var result = 0
        getInputFile(inputFilename).forEachLine {
            val current: Int = it.toInt()
            if (previous != null && current > previous!!) result++
            previous = current
        }

        return result
    }

    fun part2(inputFilename: String): Int {
        var previousSum = 0
        val previousData: LinkedList<Int> = LinkedList()

        var result = 0
        getInputFile(inputFilename).forEachLine {
            val current: Int = it.toInt()
            if (previousData.size < 3) {
                previousSum += current
            }
            else {
                val newSum = previousSum - previousData[0] + current
                if (newSum > previousSum)
                    result++
                previousData.removeFirst()
                previousSum = newSum
            }
            previousData.add(current)
        }

        return result
    }

    // test if implementation meets criteria from the description, like:
    check(part1("Day01_test") == 7)
    check(part2("Day01_test") == 5)

    println(part1("Day01"))
    println(part2("Day01"))
}
