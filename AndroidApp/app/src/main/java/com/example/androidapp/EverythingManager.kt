package com.example.androidapp

import com.example.androidapp.dataClass.Everything

class EverythingManager private constructor() {
    // Data fields that you want to share
    private var someData: Everything? = null

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
        someData = data
    }

    fun getEverything(): Everything? {
        return someData
    }
}