fun main() {
    check(part1("Day13_test") == 17)
    println(part1("Day13"))
    part2("Day13")
}

private fun part1(inputFilename: String): Int {
    val pointList: MutableList<Point> = mutableListOf()
    val instructions: MutableList<Instruction> = mutableListOf()
    fillData(inputFilename, pointList, instructions)

    return foldList(pointList.toSet(), instructions[0]).size
}

private fun part2(inputFilename: String) {
    val pointList: MutableList<Point> = mutableListOf()
    val instructions: MutableList<Instruction> = mutableListOf()
    fillData(inputFilename, pointList, instructions)

    val resultSet: Set<Point> = instructions
        .fold(pointList.toSet()) {set, instruction ->
            foldList(set, instruction)
        }

    val maxX: Int = resultSet.maxOf { it.x }
    val maxY: Int = resultSet.maxOf { it.y }

    for (y in 0 .. maxY) {
        for (x in 0 .. maxX) {
            print(if (resultSet.contains(Point(x, y))) "#" else ".")
        }
        println()
    }
}

private fun fillData(inputFilename: String, pointList: MutableList<Point>, instructions: MutableList<Instruction>) {
    getInputFile(inputFilename).forEachLine {
        when {
            it.isNotBlank() && it[0].isDigit() -> {
                pointList.add(Point(
                    it.substringBefore(",").toInt(),
                    it.substringAfter(",").toInt()
                ))
            }

            it.startsWith("fold along ") -> {
                val s = it.substringAfter("fold along ")
                instructions.add(Instruction(s[0], s.substringAfter("=").toInt()))
            }
        }
    }
}

private fun foldList(current: Set<Point>, instruction: Instruction): Set<Point> {
    val result = mutableSetOf<Point>()
    current.forEach{ point ->
        if (instruction.direction == 'x') {
            if (point.x <= instruction.line)
                result.add(point)
            else
                result.add(Point(2 * instruction.line - point.x, point.y))
        }
        else {
            if (point.y <= instruction.line)
                result.add(point)
            else
                result.add(Point(point.x, 2 * instruction.line - point.y))
        }
    }
    return result
}

private data class Instruction(val direction: Char, val line: Int)