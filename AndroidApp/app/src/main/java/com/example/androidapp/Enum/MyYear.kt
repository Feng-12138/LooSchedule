package com.example.androidapp.enum

enum class MyYear(val year : String) {
    SelectYear("Select your academic year"),
    Current("2023"),
    One("2022"),
    Two("2021"),
    Three("2020"),
    Four("2019");
    companion object {
        fun fromString(value: String) = MyYear.values().first { it.year == value }
    }
}