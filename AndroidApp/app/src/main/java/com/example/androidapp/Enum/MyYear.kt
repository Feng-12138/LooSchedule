package com.example.androidapp.enum

enum class MyYear(val year : String) {
    SelectYear("Select your academic year"),
    Current("2023 - 2024"),
    One("2022 - 2023"),
    Two("2021 - 2022"),
    Three("2020 - 2021"),
    Four("2019 -2020");
    companion object {
        fun fromString(value: String) = MyYear.values().first { it.year == value }
    }
}