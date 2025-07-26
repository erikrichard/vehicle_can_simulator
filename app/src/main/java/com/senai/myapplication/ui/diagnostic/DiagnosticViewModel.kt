package com.senai.myapplication.ui.diagnostic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senai.myapplication.simulator.CanMessage
import com.senai.myapplication.simulator.VehicleCanBusSimulator
import com.senai.myapplication.simulator.VehicleSensorSimulator
import kotlinx.coroutines.launch

class DiagnosticViewModel : ViewModel() {
    val VOLUME_ID = 0x124
    val SPEED_ID = 0x123
    private val _volume = MutableLiveData<String>().apply {
        value = "0"
    }
    private val _speed = MutableLiveData<String>().apply {
        value = "0"
    }
    val volume: LiveData<String> = _volume
    val speed: LiveData<String> = _speed
    private val vehicleSensorSimulator = VehicleSensorSimulator("Velocidade")
    private val vehicleSensorVolume = VehicleSensorSimulator("Volume")
    private val canBusSimulator = VehicleCanBusSimulator()

    init {
        viewModelScope.launch {
            canBusSimulator.canMessageFlow.collect { message ->
                when (message.id) {
                    SPEED_ID -> {
                        if (message.data.isNotEmpty()) {
                            val speedValue = message.data[0].toInt() and 0xFF // Converte byte para int (0-255)
                            _speed.postValue("Velocidade Atual: $speedValue km/h")
                        }
                    }
                    VOLUME_ID -> {
                        if (message.data.isNotEmpty()) {
                            val volumeValue = message.data[0].toInt() and 0xFF // Converte byte para int (0-255)
                            _volume.postValue("Volume CAN: $volumeValue")
                        }
                    }
                }
            }
        }
        updateSpeedData()
    }

    fun updateSpeedData() {
        val randomValue = vehicleSensorSimulator.readSensorData()
        val message = CanMessage(0x123, byteArrayOf(randomValue.toByte()))
        canBusSimulator.sendMessage(message)
    }
    fun updateVolumeData() {
        val randomValue = vehicleSensorVolume.readSensorData()
        val message = CanMessage(0x124, byteArrayOf(randomValue.toByte()))
        canBusSimulator.sendMessage(message)
    }
}