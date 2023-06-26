package com.example.androidapp.enum

enum class CoopSequence(val sequence: String) {
    SelectSequence("Select your sequence"),
    Sequence1("Sequence 1"),
    Sequence2("Sequence 2"),
    Sequence3("Sequence 3"),
    Sequence4("Sequence 4");

    companion object {
        fun fromString(value: String) = CoopSequence.values().first { it.sequence == value }
    }
}