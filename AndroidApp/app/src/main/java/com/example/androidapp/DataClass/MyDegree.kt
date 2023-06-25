package com.example.androidapp.DataClass

import com.example.androidapp.Enum.CoopSequence
import com.example.androidapp.Enum.MyMajor
import com.example.androidapp.Enum.MyMinor
import com.example.androidapp.Enum.MySpecialization
import com.example.androidapp.Enum.MyYear

data class MyDegree(
    var major: MyMajor = MyMajor.Selectmajor,
    var year: MyYear = MyYear.SelectYear,
    var sequence: CoopSequence = CoopSequence.SelectSequence,
    var minor: MyMinor = MyMinor.SelectMinor,
    var specialization: MySpecialization = MySpecialization.SelectSpecialization,
)
