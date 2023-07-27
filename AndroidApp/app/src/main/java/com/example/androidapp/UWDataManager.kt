package com.example.androidapp

import com.example.androidapp.dataClass.UWData
import com.example.androidapp.models.Course

class UWDataManager private constructor() {
    // Data fields that you want to share
    private var UWData: UWData? = null

    companion object {
        // The single instance of DataManager
        @Volatile
        private var instance: UWDataManager? = null

        // The global access point for the DataManager instance
        fun getInstance(): UWDataManager =
            instance ?: synchronized(this) {
                instance ?: UWDataManager().also { instance = it }
            }
    }

    fun setEverything(data: UWData) {
        UWData = data
    }

    fun getCourses(): List<Course>? {
        return UWData?.courses
    }

    fun getMajors(): List<String> {
        return mutableListOf("Select your degree").apply {
            UWData?.majors.let {
                if (it != null) {
                    this.addAll(it)
                }
            }
        }
    }

    fun getMinors(): List<String> {
        return mutableListOf("Select your minor").apply {
            UWData?.minors.let {
                if (it != null) {
                    this.addAll(it)
                }
            }
        }
    }

    fun getSpecializations(): List<String> {
        return mutableListOf("Select your specialization").apply {
            UWData?.specializations.let {
                if (it != null) {
                    this.addAll(it)
                }
            }
        }
    }
}