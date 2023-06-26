package services

class SequenceGenerator(year: String, sequence: String) {

    val entryYear = year
    val sequence = sequence

    fun generateSequence(): MutableMap<String, String> {
        val generatedSequence = mutableMapOf<String, String>()
        generatedSequence["1A"] = "Fall"
        generatedSequence["1B"] = "Winter"
        when (sequence) {
            "Regular" -> {
                generatedSequence["2A"] = "Fall"
                generatedSequence["2B"] = "Winter"
                generatedSequence["3A"] = "Fall"
                generatedSequence["3B"] = "Winter"
                generatedSequence["4A"] = "Fall"
                generatedSequence["4B"] = "Winter"
            }
            "Sequence 1" -> {
                generatedSequence["WT1"] = "Spring"
                generatedSequence["2A"] = "Fall"
                generatedSequence["WT2"] = "Winter"
                generatedSequence["2B"] = "Spring"
                generatedSequence["WT3"] = "Fall"
                generatedSequence["3A"] = "Winter"
                generatedSequence["WT4"] = "Sprint"
                generatedSequence["3B"] = "Fall"
                generatedSequence["WT5"] = "Winter"
                generatedSequence["4A"] = "Spring"
                generatedSequence["WT6"] = "Fall"
                generatedSequence["4B"] = "Winter"
            }
            "Sequence 2" -> {
                generatedSequence["WT1"] = "Spring"
                generatedSequence["2A"] = "Fall"
                generatedSequence["2B"] = "Winter"
                generatedSequence["WT2"] = "Spring"
                generatedSequence["3A"] = "Fall"
                generatedSequence["WT3"] = "Winter"
                generatedSequence["3B"] = "Spring"
                generatedSequence["WT4"] = "Fall"
                generatedSequence["4A"] = "Winter"
                generatedSequence["WT5"] = "Spring"
                generatedSequence["WT6"] = "Fall"
                generatedSequence["4B"] = "Winter"
            }
            "Sequence 3" -> {
                generatedSequence["2A"] = "Fall"
                generatedSequence["WT1"] = "Winter"
                generatedSequence["2B"] = "Spring"
                generatedSequence["WT2"] = "Fall"
                generatedSequence["3A"] = "Winter"
                generatedSequence["WT3"] = "Spring"
                generatedSequence["3B"] = "Fall"
                generatedSequence["WT4"] = "Winter"
                generatedSequence["4A"] = "Spring"
                generatedSequence["WT5"] = "Fall"
                generatedSequence["WT6"] = "Winter"
                generatedSequence["4B"] = "Spring"
            }
            "Sequence 4" -> {
                generatedSequence["2A"] = "Spring"
                generatedSequence["WT1"] = "Fall"
                generatedSequence["2B"] = "Winter"
                generatedSequence["WT2"] = "Spring"
                generatedSequence["3A"] = "Fall"
                generatedSequence["WT3"] = "Winter"
                generatedSequence["3B"] = "Spring"
                generatedSequence["WT4"] = "Fall"
                generatedSequence["4A"] = "Winter"
                generatedSequence["WT5"] = "Spring"
                generatedSequence["WT6"] = "Fall"
                generatedSequence["4B"] = "Winter"
            }
            else -> {
                generatedSequence.clear()
            }
        }
        return generatedSequence
    }
}