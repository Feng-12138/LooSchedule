package com.example.androidapp.Enum

enum class MyMinor(val minor: String) {
    SelectMinor("Select your minor"),
    Statistic("Statistics Minor"),
    CO("Combinatorics and Optimization Minor");
    companion object {
        fun fromString(value: String) = MyMinor.values().first { it.minor == value }
    }
}