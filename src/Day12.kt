fun main() {
    check(part1("Day12_test1") == 10)
    check(part1("Day12_test2") == 19)
    check(part1("Day12_test3") == 226)
    check(part2("Day12_test1") == 36)
    check(part2("Day12_test2") == 103)
    check(part2("Day12_test3") == 3509)

    println(part1("Day12"))
    println(part2("Day12"))
}

private fun part1(inputFilename: String): Int {
    fun findPathCountPart(currentNode: String,
                          currentPathSet: Set<String>,
                          graphMap: Map<String, MutableSet<String>>): Int {
        var result = 0
        graphMap[currentNode]?.forEach{
            if (it == "end")
                result++
            else
                if (it[0].isUpperCase() || !currentPathSet.contains(it))
                    result += findPathCountPart(it, currentPathSet.plus(currentNode), graphMap)
        }
        return result
    }

    val graphMap: Map<String, MutableSet<String>> = createGraphMap(inputFilename)
    return findPathCountPart("start", setOf(), graphMap)
}

private fun part2(inputFilename: String): Int {
    fun findPathCountPart(currentNode: String,
                          currentPathSet: Set<String>,
                          smallNodeVisitedTwice: Boolean,
                          graphMap: Map<String, MutableSet<String>>): Int {
        var result = 0
        graphMap[currentNode]?.forEach{
            when {
                it == "start" -> {}

                it == "end" ->
                    result++

                it[0].isUpperCase() ->
                    result += findPathCountPart(it, currentPathSet.plus(currentNode), smallNodeVisitedTwice, graphMap)

                !currentPathSet.contains(it) ->
                    result += findPathCountPart(it, currentPathSet.plus(currentNode), smallNodeVisitedTwice, graphMap)

                !smallNodeVisitedTwice ->
                    result += findPathCountPart(it, currentPathSet.plus(currentNode), true, graphMap)
            }
        }
        return result
    }

    val graphMap: Map<String, MutableSet<String>> = createGraphMap(inputFilename)
    return findPathCountPart("start", setOf(), false, graphMap)
}

private fun createGraphMap(inputFilename: String): Map<String, MutableSet<String>> {
    val graphMap: MutableMap<String, MutableSet<String>> = mutableMapOf()
    getInputFile(inputFilename).forEachLine {
        val start = it.substringBefore("-")
        val end = it.substringAfter("-")
        addPathToMap(start, end, graphMap)
        addPathToMap(end, start, graphMap)
    }
    return graphMap
}

private fun addPathToMap(start: String, end: String, map: MutableMap<String, MutableSet<String>>) {
    val list = map[start]
    if (list != null)
        list.add(end)
    else
        map[start] = mutableSetOf(end)
}