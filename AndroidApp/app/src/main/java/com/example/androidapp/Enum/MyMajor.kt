package com.example.androidapp.Enum

enum class MyMajor(val major: String) {
    Selectmajor("Select your degree"),
    CS("Bachelor of Computer Science"),
    MathCS("Bachelor of Mathematics (Computer Science)");
    companion object {
        fun fromString(value: String) = MyMajor.values().first { it.major == value }
    }
}