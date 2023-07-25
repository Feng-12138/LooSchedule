package services

class SequenceGenerator {
    private val regular = mutableMapOf(
        "1A" to "F",
        "1B" to "W",
        "2A" to "F",
        "2B" to "W",
        "3A" to "F",
        "3B" to "W",
        "4A" to "F",
        "4B" to "W"
    )

    private val sequence1 = mutableMapOf(
        "1A" to "F",
        "1B" to "W",
        "WT1" to "S",
        "2A" to "F",
        "WT2" to "W",
        "2B" to "S",
        "WT3" to "F",
        "3A" to "W",
        "WT4" to "S",
        "3B" to "F",
        "WT5" to "W",
        "4A" to "S",
        "WT6" to "F",
        "4B" to "W"
    )

    private val sequence2 = mutableMapOf(
        "1A" to "F",
        "1B" to "W",
        "WT1" to "S",
        "2A" to "F",
        "2B" to "W",
        "WT2" to "S",
        "3A" to "F",
        "WT3" to "W",
        "3B" to "S",
        "WT4" to "F",
        "4A" to "W",
        "WT5" to "S",
        "WT6" to "F",
        "4B" to "W"
    )

    private val sequence3 = mutableMapOf(
        "1A" to "F",
        "1B" to "W",
        "2A" to "F",
        "WT1" to "W",
        "2B" to "S",
        "WT2" to "F",
        "3A" to "W",
        "WT3" to "S",
        "3B" to "F",
        "WT4" to "W",
        "4A" to "S",
        "WT5" to "F",
        "WT6" to "W",
        "4B" to "S"
    )

    val sequence4 = mutableMapOf(
        "1A" to "F",
        "1B" to "W",
        "2A" to "S",
        "WT1" to "F",
        "2B" to "W",
        "WT2" to "S",
        "3A" to "F",
        "WT3" to "W",
        "3B" to "S",
        "WT4" to "F",
        "4A" to "W",
        "WT5" to "S",
        "WT6" to "F",
        "4B" to "W"
    )

    fun generateSequence(sequence: String, currentTerm: String? = null): MutableMap<String, String> {
        val generatedSequence = mutableMapOf<String, String>()
        var sequenceMap = mutableMapOf<String, String>()
        when (sequence) {
            "Regular" -> {
                sequenceMap = regular
            }
            "Sequence 1" -> {
                sequenceMap = sequence1
            }
            "Sequence 2" -> {
                sequenceMap = sequence2
            }
            "Sequence 3" -> {
                sequenceMap = sequence3
            }
            "Sequence 4" -> {
                sequenceMap = sequence4
            }
            else -> {
                generatedSequence.clear()
            }
        }
        return mapAfterTerm(sequenceMap, currentTerm)
    }

    private fun mapAfterTerm(map: Map<String, String>, key: String?): MutableMap<String, String> {
        val result = mutableMapOf<String, String>()
        var addElements = key == null

        for ((mapKey, mapValue) in map) {
            if (key != null && mapKey == key) {
                addElements = true
            }
            if (addElements) {
                result[mapKey] = mapValue
            }
        }
        return result
    }
}