// Неплохо бы подумать, как можно сделать лучше
fun main() {
    check(SnailfishNumber("[[1,2],[[3,4],5]]").magnitude() == 143)
    check(SnailfishNumber("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]").magnitude() == 1384)
    check(SnailfishNumber("[[[[1,1],[2,2]],[3,3]],[4,4]]").magnitude() == 445)
    check(SnailfishNumber("[[[[3,0],[5,3]],[4,4]],[5,5]]").magnitude() == 791)
    check(SnailfishNumber("[[[[5,0],[7,4]],[5,5]],[6,6]]").magnitude() == 1137)
    check(SnailfishNumber("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]").magnitude() == 3488)

    explosionCheck("[[[[[9,8],1],2],3],4]", "[[[[0,9],2],3],4]")
    explosionCheck("[7,[6,[5,[4,[3,2]]]]]", "[7,[6,[5,[7,0]]]]")
    explosionCheck("[[6,[5,[4,[3,2]]]],1]", "[[6,[5,[7,0]]],3]")
    explosionCheck("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]", "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")

    splitCheck("[[[[0,7],4],[15,[0,13]]],[1,1]]", "[[[[0,7],4],[[7,8],[0,13]]],[1,1]]")
    splitCheck("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]", "[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]")

    plusCheck("[[[[4,3],4],4],[7,[[8,4],9]]]", "[1,1]", "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")
    plusCheck("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]", "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]", "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]")
    plusCheck("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]", "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]", "[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]")
    plusCheck("[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]", "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]", "[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]")

    val testInput = readInput("Day18_test")
    check(part1(testInput) == 4140)
    check(part2(testInput) == 3993)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.drop(1)
        .fold(SnailfishNumber(input[0])) {acc, s -> acc.plus(SnailfishNumber(s))
        }
        .magnitude()
}

private fun part2(input: List<String>): Int {
    var result = 0
    for(i in input.indices) {
        for (j in input.indices) {
            if (i != j) {
                val magnitude = SnailfishNumber(input[i]).plus(SnailfishNumber(input[j])).magnitude()
                if (magnitude > result)
                    result = magnitude
            }
        }
    }
    return result
}

private fun explosionCheck(num1: String, num2: String) {
    val snailfishNumber = SnailfishNumber(num1)
    snailfishNumber.explode()
    check(snailfishNumber == SnailfishNumber(num2))
}

private fun plusCheck(a1: String, a2: String, expectedResult: String) {
    check(SnailfishNumber(a1).plus(SnailfishNumber(a2)) == SnailfishNumber(expectedResult))
}

private fun splitCheck(num1: String, num2: String) {
    val snailfishNumber = SnailfishNumber(num1)
    snailfishNumber.split()
    check(snailfishNumber == SnailfishNumber(num2))
}



private interface SnailfishPart {
    fun magnitude(): Int
}

private data class SnailfishInt(val value: Int): SnailfishPart {
    override fun magnitude(): Int {
        return value
    }

    override fun toString(): String {
        return "$value"
    }
}

private class SnailfishNumber(input: String, var parent: SnailfishNumber? = null): SnailfishPart {
    var first: SnailfishPart
    var second: SnailfishPart

    init {
        var commaPos = -1
        var bracesCount = 0
        for (i in 1 until input.length) {
            when(input[i]) {
                '[' -> bracesCount++
                ']' -> bracesCount--
                ',' -> if (bracesCount == 0) {
                    commaPos = i
                    break
                }
            }
        }

        if (commaPos < 0)
            throw IllegalArgumentException("Argument $input")

        val firstPartString = input.substring(1 until commaPos)
        first = createPart(firstPartString)
        val secondPartString = input.substring(commaPos + 1 .. input.length - 2)
        second = createPart(secondPartString)
    }

    private fun createPart(input: String): SnailfishPart {
        return if (input[0] == '[') SnailfishNumber(input, this) else SnailfishInt(input.toInt())
    }

    override fun magnitude(): Int {
        return 3 * first.magnitude() + 2 * second.magnitude()
    }

    private fun findExploding(depth: Int): SnailfishNumber? {
        var result: SnailfishNumber? = null
        if (depth < 4) {
            if (first is SnailfishNumber)
                result = (first as SnailfishNumber).findExploding(depth + 1)

            if (result == null && second is SnailfishNumber)
                result = (second as SnailfishNumber).findExploding(depth + 1)
        }
        else
            result = this

        return result
    }

    fun explode(): Boolean {
        val exploding: SnailfishNumber? = findExploding(0)
        if (exploding != null) {
            var parent: SnailfishNumber? = exploding.parent
            var element: SnailfishNumber = exploding
            while (parent != null && parent.first === element) {
                element = parent
                parent = element.parent
            }

            if (parent != null) {
                if (parent.first is SnailfishInt)
                    parent.first = SnailfishInt(parent.first.magnitude() + exploding.first.magnitude())
                else {
                    element = parent.first as SnailfishNumber
                    while (element.second is SnailfishNumber)
                        element = element.second as SnailfishNumber

                    element.second = SnailfishInt(element.second.magnitude() + exploding.first.magnitude())
                }
            }

            parent = exploding.parent
            element = exploding
            while (parent != null && parent.second === element) {
                element = parent
                parent = element.parent
            }

            if (parent != null) {
                if (parent.second is SnailfishInt)
                    parent.second = SnailfishInt(parent.second.magnitude() + exploding.second.magnitude())
                else {
                    element = parent.second as SnailfishNumber
                    while (element.first is SnailfishNumber)
                        element = element.first as SnailfishNumber

                    element.first = SnailfishInt(element.first.magnitude() + exploding.second.magnitude())
                }
            }

            if (exploding.parent!!.first === exploding)
                exploding.parent!!.first = SnailfishInt(0)
            else
                exploding.parent!!.second = SnailfishInt(0)

            return true
        }
        else
            return false
    }

    fun split(): Boolean {
        var result = false
        if (first is SnailfishInt) {
            if (first.magnitude() >= 10) {
                first = getSplitResult(first as SnailfishInt)
                result = true
            }
        }
        else
            result = (first as SnailfishNumber).split()

        if (!result) {
            if (second is SnailfishInt) {
                if (second.magnitude() >= 10) {
                    second = getSplitResult(second as SnailfishInt)
                    result = true
                }
            }
            else
                result = (second as SnailfishNumber).split()
        }
        return result
    }

    private fun getSplitResult(regular: SnailfishInt): SnailfishNumber {
        val value = regular.value
        val first = value / 2
        val second = value - first
        return SnailfishNumber("[$first,$second]", this)
    }

    private fun reduce() {
        while(explode() || split()) {
        }
    }

    fun plus(number: SnailfishNumber): SnailfishNumber {
        val result = SnailfishNumber("[0,0]")
        this.parent = result
        number.parent = result
        result.first = this
        result.second = number
        result.reduce()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SnailfishNumber

        if (first != other.first) return false
        if (second != other.second) return false

        return true
    }

    override fun hashCode(): Int {
        var result = first.hashCode()
        result = 31 * result + second.hashCode()
        return result
    }

    override fun toString(): String {
        return "($first,$second)"
    }
}