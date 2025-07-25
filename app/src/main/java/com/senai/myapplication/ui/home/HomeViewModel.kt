package com.senai.myapplication.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senai.myapplication.simulator.CanMessage
import com.senai.myapplication.simulator.VehicleCanBusSimulator
import com.senai.myapplication.simulator.VehicleSensorSimulator
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val vehicleSensorSimulator = VehicleSensorSimulator("Velocidade")
    private val canBusSimulator = VehicleCanBusSimulator()

    init {
        viewModelScope.launch {
            canBusSimulator.canMessageFlow.collect { message ->
                _text.postValue("Velocidade: CAN Message Received: ID=${message.id}, Data=${message.data.joinToString()}")
            }
        }
        sendRandomSensorData()
    }

    fun sendRandomSensorData() {
        val randomValue = vehicleSensorSimulator.readSensorData()
        val message = CanMessage(0x123, byteArrayOf(randomValue.toByte()))
        canBusSimulator.sendMessage(message)
    }

}