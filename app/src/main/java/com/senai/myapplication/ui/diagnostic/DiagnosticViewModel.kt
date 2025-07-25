package com.senai.myapplication.ui.diagnostic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DiagnosticViewModel : ViewModel() {

    private val _vel = MutableLiveData<String>().apply {
        value = "Velocidade Atual: -- km/h"
    }
    val vel: LiveData<String> = _vel
    private val _vol = MutableLiveData<String>().apply {
        value = "Volume CAN: --"
    }
    val vol: LiveData<String> = _vol
}