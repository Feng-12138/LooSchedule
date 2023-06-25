package com.example.androidapp.Enum

enum class MySpecialization(val specialization : String) {
    SelectSpecialization("Select your specialization"),
    ArtificialIntelligence("Artificial Intelligence Specialization"),
    Business("Business Specialization");
    companion object {
        fun fromString(value: String) = MySpecialization.values().first { it.specialization == value }
    }
}