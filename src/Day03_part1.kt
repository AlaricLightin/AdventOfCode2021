fun main() {
    fun part1(inputFilename: String): Long {
        var differenceArray: Array<Int>? = null
        getInputFile(inputFilename).forEachLine {
            if (differenceArray == null)
                differenceArray = Array(it.length){ 0 }

            it.forEachIndexed { index, c ->
                when(c) {
                    '0' -> differenceArray!![index]--
                    '1' -> differenceArray!![index]++
                }
            }
        }

        var gammaRate = 0
        var epsilonRate = 0
        if (differenceArray != null) {
            val arraySize = differenceArray!!.size
            differenceArray!!.forEachIndexed { index, difference ->
                val bitValue = 1 shl (arraySize - 1 - index)
                if (difference > 0) {
                    gammaRate += bitValue
                }
                else
                    epsilonRate += bitValue
            }
        }

        return gammaRate.toLong() * epsilonRate
    }

    check(part1("Day03_test") == 198L)

    println(part1("Day03"))
}