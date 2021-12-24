fun main() {
    println(getResult("Day24") { a, b -> a > b})
    println(getResult("Day24") { a, b -> a < b})
}

private fun getResult(inputFilename: String, compareFunction: (Long, Long) -> Boolean): Long {
    val commandArrays: List<CommandsChunk> = readData(inputFilename)
    val reduceFunction: (Long, Long) -> Long = { acc, next -> if (compareFunction(next, acc)) next else acc }
    var stateMap: MutableMap<State, Long> = mutableMapOf(State(0, 0, 0, 0) to 0L)
    commandArrays.forEach { commandsChunk ->
        val truncatedStateMap: Map<State, Long> = getTruncatedStateMap(stateMap,
            commandsChunk.inputVar, reduceFunction)
        val newStateMap: MutableMap<State, Long> = mutableMapOf()
        truncatedStateMap.forEach { (state, modelNum) ->
            for (i in 1 .. 9) {
                val newModelNum: Long = modelNum * 10 + i
                val newState = execute(state, commandsChunk, i)
                val savedModelNum = newStateMap[newState]
                if (savedModelNum == null || compareFunction(newModelNum, savedModelNum))
                    newStateMap[newState] = newModelNum
            }
        }
        stateMap = newStateMap
    }
    return stateMap
        .filter { it.key.z == 0 }
        .values
        .reduce(reduceFunction)
}

private enum class Operation {ADD, MUL, DIV, MOD, EQL}

private data class Command(val operation: Operation, val varIndex: Int, val second: String)

private data class CommandsChunk(val inputVar: String, val commands: List<Command>)

private data class State(val w: Int, val x: Int, val y: Int, val z: Int) {
    fun truncate(variable: String): State {
        return when(variable) {
            "w" -> State(0, x, y, z)
            "x" -> State(w, 0, y, z)
            "y" -> State(w, x, 0, z)
            "z" -> State(w, x, y, 0)

            else -> this
        }
    }
}

private val indexes = mapOf("w" to 0, "x" to 1, "y" to 2, "z" to 3)

private fun getIndex(s: String): Int {
    return indexes[s]!!
}

private fun execute(state: State, commandsChunk: CommandsChunk, input: Int): State {
    val variables: Array<Int> = arrayOf(
        state.w,
        state.x,
        state.y,
        state.z
    )

    fun getValue(s: String): Int {
        return if (s == "w" || s == "x" || s == "y" || s == "z")
            variables[getIndex(s)]
        else
            s.toInt()
    }

    variables[getIndex(commandsChunk.inputVar)] = input

    commandsChunk.commands.forEach{
        when(it.operation) {
            Operation.ADD -> variables[it.varIndex] += getValue(it.second)

            Operation.MUL -> variables[it.varIndex] *= getValue(it.second)

            Operation.DIV -> variables[it.varIndex] /= getValue(it.second)

            Operation.MOD -> variables[it.varIndex] %= getValue(it.second)

            Operation.EQL -> variables[it.varIndex] =
                if (variables[it.varIndex] == getValue(it.second)) 1 else 0
        }
    }

    return State(variables[0], variables[1], variables[2], variables[3])
}


private fun readData(inputFilename: String): List<CommandsChunk> {
    val result = mutableListOf<CommandsChunk>()
    var inputVar: String? = null
    var commandList: MutableList<Command> = mutableListOf()
    getInputFile(inputFilename).forEachLine {
        val parts: List<String> = it.split(" ")
        if (parts[0] == "inp") {
            if (inputVar != null) {
                result.add(CommandsChunk(inputVar!!, commandList))
                commandList = mutableListOf()
            }
            inputVar = parts[1]
        }
        else
            commandList.add(Command(Operation.valueOf(parts[0].uppercase()), getIndex(parts[1]), parts[2]))
    }

    if (inputVar != null) {
        result.add(CommandsChunk(inputVar!!, commandList))
    }
    return result
}

private fun getTruncatedStateMap(stateMap: Map<State, Long>,
                                 inputVar: String,
                                 reduceFunction: (Long, Long) -> Long): Map<State, Long> {
    return stateMap
        .entries
        .groupingBy { it.key.truncate(inputVar) }
        .aggregate { _, accumulator, element, first ->
            if (first)
                element.value
            else
                reduceFunction(element.value, accumulator!!)
        }
}
