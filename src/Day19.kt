import kotlin.math.abs

fun main() {
    val (beaconCountTest, scannersDistanceTest) = getAnswer(readInput("Day19_test"))
    check(beaconCountTest == 79)
    check(scannersDistanceTest == 3621)

    val (beaconCount, scannersDistance) = getAnswer(readInput("Day19"))
    println(beaconCount)
    println(scannersDistance)
}

private fun getAnswer(input: List<String>): Pair<Int, Int> {
    val scanners: MutableList<Scanner> = readData(input)
    val distances: List<Array<List<Distance>>> = scanners
        .map { createDistanceArray(it) }

    var zeroBasedScanners: MutableList<Scanner> = mutableListOf(scanners[0])
    scanners.removeAt(0)
    val zeroBasedCheckedScanners: MutableList<Scanner> = mutableListOf()

    val scannerCoordsList: MutableList<List<Int>> = mutableListOf()

    do {
        val newZeroBasedScanners: MutableList<Scanner> = mutableListOf()
        zeroBasedScanners.forEach { scanner1 ->
            val distances1: Array<List<Distance>> = distances[scanner1.index]
            for (j in scanners.indices.reversed()) {
                val scanner2 = scanners[j]
                val commonPoints: List<Pair<Int, Int>> = compareDistancesForScanner(
                    distances1, distances[scanner2.index])
                if (commonPoints.size < 12)
                    continue
                val nonTrivialDistancePair: Pair<Distance, Distance> =
                    getNonTrivialDistancePair(commonPoints, scanner1, scanner2)
                val transformationMatrix: Array<IntArray> = getTransformationMatrix(nonTrivialDistancePair)
                val transformationDelta: Array<Int> = getTransformationDelta(transformationMatrix,
                    scanner1.beacons[commonPoints[0].first].coords,
                    scanner2.beacons[commonPoints[0].second].coords
                )
                val scannerCoords = getNewCoords(listOf(0, 0, 0), transformationMatrix, transformationDelta)
                scannerCoordsList.add(scannerCoords)
                val scannerWithNewCoords: Scanner = transformScanner(
                    scanner2, transformationMatrix,
                    transformationDelta)
                newZeroBasedScanners.add(scannerWithNewCoords)
                scanners.removeAt(j)
            }
        }
        zeroBasedCheckedScanners.addAll(zeroBasedScanners)
        zeroBasedScanners = newZeroBasedScanners
    } while (newZeroBasedScanners.isNotEmpty())

    val resultSet: Set<List<Int>> = zeroBasedCheckedScanners
        .flatMap { it.beacons }
        .map { it.coords }
        .toSet()

    return Pair(resultSet.size, getMaxDistanceBetweenScanners(scannerCoordsList))
}

private fun getMaxDistanceBetweenScanners(scannerCoordsList: List<List<Int>>): Int {
    var result = 0
    scannerCoordsList.forEachIndexed { index, scanner1Coords ->
        for (i in index + 1 until scannerCoordsList.size) {
            val diffCoordsList = getAbsDiffCoordsList(scanner1Coords, scannerCoordsList[i])
            val manhattanDistance = diffCoordsList[0] + diffCoordsList[1] + diffCoordsList[2]
            if (manhattanDistance > result)
                result = manhattanDistance
        }
    }
    return result
}

private fun transformScanner(
    scanner: Scanner,
    transformationMatrix: Array<IntArray>,
    transformationDelta: Array<Int>
): Scanner {
    val transformedBeacons: List<Beacon> = scanner.beacons
        .map { it.coords }
        .map {
            getNewCoords(it, transformationMatrix, transformationDelta)
        }
        .map { Beacon(it) }
    return Scanner(scanner.index, transformedBeacons)
}

private fun getNewCoords(
    coords: List<Int>,
    transformationMatrix: Array<IntArray>,
    transformationDelta: Array<Int>
): List<Int> {
    val transformedCoords = multiplyToMatrix(coords, transformationMatrix)
    return List(coords.size) { index -> transformedCoords[index] + transformationDelta[index] }
}

private fun getTransformationDelta(
    transformationMatrix: Array<IntArray>,
    coords1: List<Int>,
    coords2: List<Int>
): Array<Int> {
    val transformedCoords = multiplyToMatrix(coords2, transformationMatrix)
    return Array(3) { i ->
        coords1[i] - transformedCoords[i]
    }
}

private fun multiplyToMatrix(coords: List<Int>, matrix: Array<IntArray>): List<Int> {
    return List(coords.size) { index ->
        matrix[index].foldIndexed(0) { index2, acc, j -> acc + j * coords[index2] }
    }
}

private fun getTransformationMatrix(distancePair: Pair<Distance, Distance>): Array<IntArray> {
    val result = Array(3) {IntArray(3)}
    val firstCoords = distancePair.first.diffCoords
    distancePair.second.diffCoords.forEachIndexed { index, coord ->
        for (i in 0 .. 2) {
            if (firstCoords[i] == coord)
                result[i][index] = 1
            else if (firstCoords[i] == -coord)
                result[i][index] = -1
        }
    }
    return result
}

private fun getNonTrivialDistancePair(
    commonPoints: List<Pair<Int, Int>>,
    scanner1: Scanner,
    scanner2: Scanner
): Pair<Distance, Distance> {
    commonPoints.forEachIndexed { index, pair ->
        for (j in index + 1 until commonPoints.size) {
            val commonPoint = commonPoints[j]
            val distance = Distance(getDiffCoordsList(
                scanner1.beacons[pair.first].coords,
                scanner1.beacons[commonPoint.first].coords
            ))
            val diffCoords = distance.diffCoords
            if (diffCoords.all { it != 0 }
                && abs(diffCoords[0]) != abs(diffCoords[1])
                && abs(diffCoords[0]) != abs(diffCoords[2])
                && abs(diffCoords[1]) != abs(diffCoords[2])
            )
                return Pair(distance, Distance(getDiffCoordsList(
                    scanner2.beacons[pair.second].coords,
                    scanner2.beacons[commonPoint.second].coords
                )))
        }
    }

    throw IllegalStateException()
}

private data class Beacon(val coords: List<Int>)

private data class Distance(val diffCoords: List<Int>)

private data class Scanner(val index: Int, val beacons: List<Beacon>)

private fun readData(input: List<String>): MutableList<Scanner> {
    var scannerIndex: Int = -1
    val result: MutableList<Scanner> = mutableListOf()
    var beaconList: MutableList<Beacon> = mutableListOf()
    input.forEach{ s ->
        when {
            s.isBlank() -> {}
            s.startsWith("--- scanner") -> {
                if (scannerIndex >= 0) {
                    result.add(Scanner(scannerIndex, beaconList))
                    beaconList = mutableListOf()
                }

                scannerIndex++
            }

            else -> beaconList.add(Beacon(s.split(",").map { it.toInt() }))
        }
    }

    result.add(Scanner(scannerIndex, beaconList))

    return result
}

private fun createDistanceArray(scanner: Scanner): Array<List<Distance>> {
    val size = scanner.beacons.size
    return Array(size) { i ->
        val b1 = scanner.beacons[i]
        scanner.beacons
            .filter { b2 -> b1 !== b2 }
            .map { b2 -> Distance(getAbsDiffCoordsList(b1.coords, b2.coords)) }
    }
}

private fun compareDistancesForScanner(d1: Array<List<Distance>>, d2: Array<List<Distance>>): List<Pair<Int, Int>> {
    val result = mutableListOf<Pair<Int, Int>>()
    d1.forEachIndexed { index1, it1 ->
        d2.forEachIndexed { index2, it2 ->
            if (compareDistancesForPoints(it1, it2) >= 11)
                result.add(Pair(index1, index2))
        }
    }
    return result
}

private fun compareDistancesForPoints(list1: List<Distance>, list2: List<Distance>): Int {
    val d2copy: MutableList<Distance> = list2.toMutableList()
    var count = 0
    list1.forEach { d1 ->
        for (i in 0 until d2copy.size) {
            val d2 = d2copy[i]
            if (d1.diffCoords.containsAll(d2.diffCoords)) {
                count++
                d2copy.removeAt(i)
                break
            }
        }
    }
    return count
}

private fun getAbsDiffCoordsList(coords1: List<Int>, coords2: List<Int>): List<Int> {
    return coords1.mapIndexed { index, i -> abs(i - coords2[index]) }
}

private fun getDiffCoordsList(coords1: List<Int>, coords2: List<Int>): List<Int> {
    return coords1.mapIndexed { index, i -> i - coords2[index] }
}