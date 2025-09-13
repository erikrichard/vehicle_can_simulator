package com.senai.myapplication.ui.diagnostic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senai.myapplication.simulator.CanMessage
import com.senai.myapplication.simulator.VehicleCanBusSimulator
import com.senai.myapplication.utils.AppConstants.CAN_MSG_GET_SPEED
import com.senai.myapplication.utils.AppConstants.CAN_MSG_GET_VOLUME
import com.senai.myapplication.utils.AppConstants.CAN_MSG_SET_SPEED
import com.senai.myapplication.utils.AppConstants.CAN_MSG_SET_VOLUME
import com.senai.myapplication.utils.AppConstants.CAN_MSG_VOLUME
import kotlinx.coroutines.launch

class DiagnosticViewModel : ViewModel() {

    private val _volume = MutableLiveData<String>().apply {
        value = "0"
    }
    private val _speed = MutableLiveData<String>().apply {
        value = "0"
    }
    val volume: LiveData<String> = _volume
    val speed: LiveData<String> = _speed
    private val canBusSimulator = VehicleCanBusSimulator()

    init {
        viewModelScope.launch {
            canBusSimulator.canMessageFlow.collect { message ->
                when (message.id) {
                    CAN_MSG_GET_SPEED -> {
                        if (message.data.isNotEmpty()) {
                            val speedValue = message.data[0].toInt() and 0xFF // Converte byte para int (0-255)
                            _speed.postValue("Velocidade Atual: $speedValue km/h")
                        }
                    }

                    CAN_MSG_GET_VOLUME -> {
                        if (message.data.isNotEmpty()) {
                            val volumeValue = message.data[0].toInt() and 0xFF // Converte byte para int (0-255)
                            _volume.postValue("Volume CAN: $volumeValue")
                        }
                    }
                }
            }
        }
        initValues()
    }

    fun updateVolumeData() {
        val randomValue = (0..100).random()
        val message = CanMessage(CAN_MSG_SET_VOLUME, byteArrayOf(randomValue.toByte()))
        canBusSimulator.sendMessage(message)
    }

    fun updateSpeedData() {
        val randomValue = (0..100).random()
        val message = CanMessage(CAN_MSG_SET_SPEED, byteArrayOf(randomValue.toByte()))
        canBusSimulator.sendMessage(message)
    }

    private fun initValues() {
        canBusSimulator.sendMessage(CanMessage(CAN_MSG_GET_SPEED, byteArrayOf()))
        canBusSimulator.sendMessage(CanMessage(CAN_MSG_VOLUME, byteArrayOf()))
    }
}