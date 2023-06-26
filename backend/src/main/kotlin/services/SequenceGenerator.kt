package services

class SequenceGenerator {
    fun generateSequence(sequence: String): MutableMap<String, String> {
        val generatedSequence = mutableMapOf<String, String>()
        generatedSequence["1A"] = "F"
        generatedSequence["1B"] = "W"
        when (sequence) {
            "Regular" -> {
                generatedSequence["2A"] = "F"
                generatedSequence["2B"] = "W"
                generatedSequence["3A"] = "F"
                generatedSequence["3B"] = "W"
                generatedSequence["4A"] = "F"
                generatedSequence["4B"] = "W"
            }
            "Sequence 1" -> {
                generatedSequence["WT1"] = "S"
                generatedSequence["2A"] = "F"
                generatedSequence["WT2"] = "W"
                generatedSequence["2B"] = "S"
                generatedSequence["WT3"] = "F"
                generatedSequence["3A"] = "W"
                generatedSequence["WT4"] = "S"
                generatedSequence["3B"] = "F"
                generatedSequence["WT5"] = "W"
                generatedSequence["4A"] = "S"
                generatedSequence["WT6"] = "F"
                generatedSequence["4B"] = "W"
            }
            "Sequence 2" -> {
                generatedSequence["WT1"] = "S"
                generatedSequence["2A"] = "F"
                generatedSequence["2B"] = "W"
                generatedSequence["WT2"] = "S"
                generatedSequence["3A"] = "F"
                generatedSequence["WT3"] = "W"
                generatedSequence["3B"] = "S"
                generatedSequence["WT4"] = "F"
                generatedSequence["4A"] = "W"
                generatedSequence["WT5"] = "S"
                generatedSequence["WT6"] = "F"
                generatedSequence["4B"] = "W"
            }
            "Sequence 3" -> {
                generatedSequence["2A"] = "F"
                generatedSequence["WT1"] = "W"
                generatedSequence["2B"] = "S"
                generatedSequence["WT2"] = "F"
                generatedSequence["3A"] = "W"
                generatedSequence["WT3"] = "S"
                generatedSequence["3B"] = "F"
                generatedSequence["WT4"] = "W"
                generatedSequence["4A"] = "S"
                generatedSequence["WT5"] = "F"
                generatedSequence["WT6"] = "W"
                generatedSequence["4B"] = "S"
            }
            "Sequence 4" -> {
                generatedSequence["2A"] = "S"
                generatedSequence["WT1"] = "F"
                generatedSequence["2B"] = "W"
                generatedSequence["WT2"] = "S"
                generatedSequence["3A"] = "F"
                generatedSequence["WT3"] = "W"
                generatedSequence["3B"] = "S"
                generatedSequence["WT4"] = "F"
                generatedSequence["4A"] = "W"
                generatedSequence["WT5"] = "S"
                generatedSequence["WT6"] = "F"
                generatedSequence["4B"] = "W"
            }
            else -> {
                generatedSequence.clear()
            }
        }
        println("/////////////")
        println(generatedSequence)
        return generatedSequence
    }
}