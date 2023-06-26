package com.example.androidapp.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.androidapp.dataClass.MyDegree
import com.example.androidapp.enum.CoopSequence
import com.example.androidapp.enum.MyMajor
import com.example.androidapp.enum.MyMinor
import com.example.androidapp.enum.MySpecialization
import com.example.androidapp.enum.MyYear
import com.example.androidapp.models.Communication
import com.example.androidapp.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectDegreeVM : ViewModel() {
    private val _uiState = MutableStateFlow(MyDegree())
    val uiState: StateFlow<MyDegree> = _uiState.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog : Boolean get() = _showDialog.value

    private fun getCommunications(context: Context) {
        val api = RetrofitClient.create()
        val call = api.getCommunications()
        call?.enqueue(object : Callback<List<Communication>> {
            override fun onResponse(call: Call<List<Communication>>, response: Response<List<Communication>>) {
                if (response.isSuccessful) {
                    val comList = response.body()
                    Toast.makeText(context, comList?.get(0)?.subject ?: "fail to read", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "response unSuccessful", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Communication>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })
    }

    fun toggleDialog() {
        _showDialog.value = !_showDialog.value
    }

    fun generateSchedule(
        major: MyMajor,
        year: MyYear,
        sequence: CoopSequence,
        minor: MyMinor,
        specialization: MySpecialization,
        context: Context
    ) {
        if (major.major == "Select your degree" ||
            year.year == "Select your academic year" ||
            sequence.sequence == "Select your Coop sequence") {
            toggleDialog()
            return
        }

        val api = RetrofitClient.create()
        val call = api.getCommunications()
        call?.enqueue(object : Callback<List<Communication>>{
            override fun onResponse(call: Call<List<Communication>>, response: Response<List<Communication>>) {
                if (response.isSuccessful) {
                    val comList = response.body()
                    Toast.makeText(context, comList?.get(0)?.subject ?: "fail to read", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "response unSuccessful", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Communication>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })

    }
}