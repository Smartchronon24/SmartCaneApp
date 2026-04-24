package com.example.smartcaneapp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class DataViewModel : ViewModel() {
    val db = FirebaseFirestore.getInstance()

    private  val _espstatus = mutableStateOf<Boolean?>(false)
    val espstatus : MutableState<Boolean?> = _espstatus

    private  val _gyrostatus = mutableStateOf<Boolean?>(false)
    val gyrostatus : MutableState<Boolean?> = _gyrostatus

    private  val _ultrasonicstatus = mutableStateOf<Boolean?>(false)
    val ultrasonicstatus : MutableState<Boolean?> = _ultrasonicstatus

    private  val _gpsstatus = mutableStateOf<Boolean?>(false)
    val gpsstatus : MutableState<Boolean?> = _gpsstatus

    fun getstatus(status:Boolean?): String {
        if (status == true) {
            return "ONLINE"
        }
        else {
            return "OFFLINE"
        }
    }

    fun updateESPStatus(status: Boolean) {
        _espstatus.value = status
    }

}
