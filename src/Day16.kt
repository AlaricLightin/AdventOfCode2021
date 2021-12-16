fun main() {
    check(part1("8A004A801A8002F478") == 16)
    check(part1("620080001611562C8802118E34") == 12)
    check(part1("C0015000016115A2E0802F182340") == 23)
    check(part1("A0016C880162017C3686B18A3D4780") == 31)

    check(part2("C200B40A82") == 3L)
    check(part2("04005AC33890") == 54L)
    check(part2("880086C3E88112") == 7L)
    check(part2("CE00C43D881120") == 9L)
    check(part2("D8005AC2A8F0") == 1L)
    check(part2("F600BC2D8F") == 0L)
    check(part2("9C005AC2F8F0") == 0L)
    check(part2("9C0141080250320F1802104A08") == 1L)

    val input = readInput("Day16")
    println(part1(input[0]))
    println(part2(input[0]))
}

private fun part1(hexString: String): Int {
    val binaryString: String = convertHexToBinary(hexString)
    var pos = 0

    fun skipLiteralContent() {
        while (binaryString[pos] == '1')
            pos += 5

        pos +=5
    }

    fun readPackage(): Int {
        var result: Int = binaryString.substring(pos..pos + 2).toInt(2)
        pos += 3
        val type: Int = binaryString.substring(pos..pos + 2).toInt(2)
        pos += 3
        if (type == 4)
            skipLiteralContent()
        else {
            val lengthType: Char = binaryString[pos]
            pos++
            if (lengthType == '0') {
                val length: Int = binaryString.substring(pos .. pos + 14).toInt(2)
                pos += 15
                val posAtStart = pos
                while(pos < posAtStart + length) {
                    result += readPackage()
                }
            }
            else {
                val length: Int = binaryString.substring(pos .. pos + 10).toInt(2)
                pos += 11
                for (i in 1 .. length)
                    result += readPackage()
            }
        }
        return result
    }

    return readPackage()
}

private fun part2(hexString: String): Long {
    val binaryString: String = convertHexToBinary(hexString)
    var pos = 0

    fun getLiteralContent(): Long {
        val stringBuilder = StringBuilder()
        while (binaryString[pos] == '1') {
            stringBuilder.append(binaryString.substring(pos + 1..pos + 4))
            pos += 5
        }

        stringBuilder.append(binaryString.substring(pos + 1..pos + 4))
        pos +=5
        return stringBuilder.toString().toLong(2)
    }

    fun calculate(): Long {
        pos += 3
        val type: Int = binaryString.substring(pos..pos + 2).toInt(2)
        pos += 3
        if (type == 4)
            return getLiteralContent()
        else {
            val lengthType: Char = binaryString[pos]
            pos++
            val operandList: MutableList<Long> = mutableListOf()
            if (lengthType == '0') {
                val length: Int = binaryString.substring(pos .. pos + 14).toInt(2)
                pos += 15
                val posAtStart = pos
                while(pos < posAtStart + length) {
                    operandList.add(calculate())
                }
            }
            else {
                val length: Int = binaryString.substring(pos .. pos + 10).toInt(2)
                pos += 11
                for (i in 1 .. length)
                    operandList.add(calculate())
            }

            return when(type) {
                0 -> operandList.sum()
                1 -> operandList.fold(1) { acc, i -> acc * i }
                2 -> operandList.minOf { it }
                3 -> operandList.maxOf { it }

                5 -> if (operandList[0] > operandList[1]) 1 else 0
                6 -> if (operandList[0] < operandList[1]) 1 else 0
                7 -> if (operandList[0] == operandList[1]) 1 else 0

                else -> 0
            }
        }
    }

    return calculate()
}

private fun convertHexToBinary(hex: String): String {
    val result = StringBuilder()
    hex.forEach {
        result.append(when(it) {
            '0' -> "0000"
            '1' -> "0001"
            '2' -> "0010"
            '3' -> "0011"
            '4' -> "0100"
            '5' -> "0101"
            '6' -> "0110"
            '7' -> "0111"
            '8' -> "1000"
            '9' -> "1001"
            'A' -> "1010"
            'B' -> "1011"
            'C' -> "1100"
            'D' -> "1101"
            'E' -> "1110"
            'F' -> "1111"

            else -> ""
        })
    }
    return result.toString()
}