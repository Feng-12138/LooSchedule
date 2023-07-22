package com.example.androidapp

import com.example.androidapp.dataClass.Everything

class EverythingManager private constructor() {
    // Data fields that you want to share
    private var everything: Everything? = null

    companion object {
        // The single instance of DataManager
        @Volatile
        private var instance: EverythingManager? = null

        // The global access point for the DataManager instance
        fun getInstance(): EverythingManager =
            instance ?: synchronized(this) {
                instance ?: EverythingManager().also { instance = it }
            }
    }

    fun setEverything(data: Everything) {
        everything = data
    }

    fun getCourses(): List<String>? {
        return everything?.courses
    }

    fun getMajors(): List<String> {
        return mutableListOf("Select your degree").apply {
            everything?.majors.let {
                if (it != null) {
                    this.addAll(it)
                }
            }
        }
    }

    fun getMinors(): List<String> {
        return mutableListOf("Select your minor").apply {
            everything?.minors.let {
                if (it != null) {
                    this.addAll(it)
                }
            }
        }
    }

    fun getSpecializations(): List<String> {
        return mutableListOf("Select your specialization").apply {
            everything?.specializations.let {
                if (it != null) {
                    this.addAll(it)
                }
            }
        }
    }
}