import kotlin.math.max

fun main() {
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 739785)
    check(part2(testInput) == 444356092776315)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val positions: Array<Int> = getStartPositions(input)
    val playersCount = positions.size
    val dice = Dice()
    val points: Array<Int> = Array(playersCount) { 0 }
    var currentPlayer = -1

    do {
        currentPlayer = (currentPlayer + 1) % playersCount
        val move = dice.getMove()
        positions[currentPlayer] = getNewPosition(positions[currentPlayer], move)
        points[currentPlayer] += positions[currentPlayer]
    } while (points[currentPlayer] < 1000)

    val losingPlayer = 1 - currentPlayer
    return points[losingPlayer] * dice.count
}

private fun getNewPosition(currentPosition: Int, move: Int): Int {
    return (currentPosition - 1 + move) % 10 + 1
}

private fun getStartPositions(input: List<String>): Array<Int> {
    return Array(input.size) { i -> input[i].substringAfter(": ").toInt()}
}

private class Dice {
    private var value: Int = 1
    var count: Int = 0

    fun getMove(): Int {
        var result = 0
        for (i in 1 .. 3) {
            result += value
            value++
            if (value > 100) value = 1
        }
        count += 3
        return result
    }
}

private val moveStateMap = mapOf(
    3 to 1,
    4 to 3,
    5 to 6,
    6 to 7,
    7 to 6,
    8 to 3,
    9 to 1
)

private fun part2(input: List<String>): Long {
    val positions: Array<Int> = getStartPositions(input)
    val playersCount = positions.size
    var currentPlayer = -1
    val universesCount: Array<Long> = Array(playersCount) { 0 }
    var stateMap: MutableMap<GameState, Long> = mutableMapOf(
        GameState(positions, arrayOf(0, 0)) to 1
    )

    do {
        currentPlayer = (currentPlayer + 1) % playersCount
        val newStateMap: MutableMap<GameState, Long> = mutableMapOf()
        stateMap.forEach{ (gameState, universes) ->
            moveStateMap.forEach { (move, count) ->
                val newGameState = gameState.getNewState(currentPlayer, move)
                val newUniverseCount = universes * count
                if (newGameState.getPoints(currentPlayer) >= 21)
                    universesCount[currentPlayer] += newUniverseCount
                else
                    newStateMap.compute(newGameState) { _, value -> (value ?: 0L) + newUniverseCount}
            }
        }
        stateMap = newStateMap
    } while (stateMap.isNotEmpty())

    return max(universesCount[0], universesCount[1])
}

private data class GameState(
    val firstPosition: Int,
    val secondPosition: Int,
    val firstPoints: Int,
    val secondPoints: Int
) {
    constructor(position: Array<Int>, points: Array<Int>): this(position[0], position[1], points[0], points[1])

    fun getNewState(currentPlayer: Int, move: Int): GameState {
        return if (currentPlayer == 0) {
            val newPosition = getNewPosition(firstPosition, move)
            GameState(newPosition, secondPosition, firstPoints + newPosition, secondPoints)
        } else {
            val newPosition = getNewPosition(secondPosition, move)
            GameState(firstPosition, newPosition, firstPoints, secondPoints  + newPosition)
        }
    }

    fun getPoints(player: Int): Int {
        return if (player == 0) firstPoints else secondPoints
    }
}