package com.example.androidapp.dataClass

import com.example.androidapp.enum.CoopSequence
import com.example.androidapp.enum.MyMajor
import com.example.androidapp.enum.MyMinor
import com.example.androidapp.enum.MySpecialization
import com.example.androidapp.enum.MyYear

data class MyDegree(
    var major: MyMajor = MyMajor.Selectmajor,
    var year: MyYear = MyYear.SelectYear,
    var sequence: CoopSequence = CoopSequence.SelectSequence,
    var minor: MyMinor = MyMinor.SelectMinor,
    var specialization: MySpecialization = MySpecialization.SelectSpecialization,
)
