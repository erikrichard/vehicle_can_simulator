package com.senai.myapplication.ui.diagnostic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senai.myapplication.simulator.CanMessage
import com.senai.myapplication.simulator.VehicleCanBusSimulator
import com.senai.myapplication.utils.AppConstants.CAN_MSG_BASS
import com.senai.myapplication.utils.AppConstants.CAN_MSG_GET_BASS
import com.senai.myapplication.utils.AppConstants.CAN_MSG_GET_MID
import com.senai.myapplication.utils.AppConstants.CAN_MSG_GET_SPEED
import com.senai.myapplication.utils.AppConstants.CAN_MSG_GET_TREBLE
import com.senai.myapplication.utils.AppConstants.CAN_MSG_GET_VOLUME
import com.senai.myapplication.utils.AppConstants.CAN_MSG_MID
import com.senai.myapplication.utils.AppConstants.CAN_MSG_SET_BASS
import com.senai.myapplication.utils.AppConstants.CAN_MSG_SET_MID
import com.senai.myapplication.utils.AppConstants.CAN_MSG_SET_SPEED
import com.senai.myapplication.utils.AppConstants.CAN_MSG_SET_TREBLE
import com.senai.myapplication.utils.AppConstants.CAN_MSG_SET_VOLUME
import com.senai.myapplication.utils.AppConstants.CAN_MSG_TREBLE
import com.senai.myapplication.utils.AppConstants.CAN_MSG_VOLUME
import kotlinx.coroutines.launch

class DiagnosticViewModel : ViewModel() {

    private val TAG = "DiagnosticViewModel"

    private val _volume = MutableLiveData<String>().apply {
        value = "0"
    }
    private val _speed = MutableLiveData<String>().apply {
        value = "0"
    }

    private val _treble = MutableLiveData<Int>().apply {
        value = 0
    }
    private val _mid = MutableLiveData<Int>().apply {
        value = 0
    }
    private val _bass = MutableLiveData<Int>().apply {
        value = 0
    }

    val volume: LiveData<String> = _volume
    val speed: LiveData<String> = _speed
    val treble: LiveData<Int> = _treble
    val mid: LiveData<Int> = _mid
    val bass: LiveData<Int> = _bass

    private val canBusSimulator = VehicleCanBusSimulator()

    init {
        viewModelScope.launch {
            canBusSimulator.canMessageFlow.collect { message ->
                Log.i(TAG, "Mensagem recebida: ${message.id}")
                when (message.id) {
                    CAN_MSG_GET_SPEED -> {
                        if (message.data.isNotEmpty()) {
                            val speedValue = message.data[0].toInt() and 0xFF // Converte byte para int (0-255)
                            _speed.postValue("Velocidade Atual: $speedValue km/h")
                        }
                    }

                    CAN_MSG_VOLUME -> {
                        if (message.data.isNotEmpty()) {
                            val volumeValue = message.data[0].toInt() and 0xFF // Converte byte para int (0-255)
                            _volume.postValue("Volume CAN: $volumeValue")
                        }
                    }

                    CAN_MSG_TREBLE -> {
                        if (message.data.isNotEmpty()) {
                            val trebleValue =
                                message.data[0].toInt() and 0xFF // Converte byte para int (0-255)
                            _treble.postValue(trebleValue)
                        }
                    }

                    CAN_MSG_MID -> {
                        if (message.data.isNotEmpty()) {
                            val midValue =
                                message.data[0].toInt() and 0xFF // Converte byte para int (0-255)
                            _mid.postValue(midValue)
                        }
                    }

                    CAN_MSG_BASS -> {
                        if (message.data.isNotEmpty()) {
                            val bassValue =
                                message.data[0].toInt() and 0xFF // Converte byte para int (0-255)
                            _bass.postValue(bassValue)
                        }
                    }

                }
            }
        }
        initValues()
    }

    fun updateVolumeData() {
        Log.i(TAG, "Solicitando atualização de volume")
        val randomValue = (0..100).random()
        val message = CanMessage(CAN_MSG_SET_VOLUME, byteArrayOf(randomValue.toByte()))
        canBusSimulator.sendMessage(message)
    }

    fun updateSpeedData() {
        Log.i(TAG, "Solicitando atualização de velocidade")
        val randomValue = (0..100).random()
        val message = CanMessage(CAN_MSG_SET_SPEED, byteArrayOf(randomValue.toByte()))
        canBusSimulator.sendMessage(message)
    }

    fun updateTrebleData(value: Int) {
        val message = CanMessage(CAN_MSG_SET_TREBLE, byteArrayOf(value.toByte()))
        canBusSimulator.sendMessage(message)
    }

    fun updateMidData(value: Int) {
        val message = CanMessage(CAN_MSG_SET_MID, byteArrayOf(value.toByte()))
        canBusSimulator.sendMessage(message)
    }

    fun updateBassData(value: Int) {
        val message = CanMessage(CAN_MSG_SET_BASS, byteArrayOf(value.toByte()))
        canBusSimulator.sendMessage(message)
    }

    private fun initValues() {
        Log.i(TAG, "Iniciando valores")
        canBusSimulator.sendMessage(CanMessage(CAN_MSG_GET_SPEED, byteArrayOf()))
        canBusSimulator.sendMessage(CanMessage(CAN_MSG_GET_VOLUME, byteArrayOf()))
        canBusSimulator.sendMessage(CanMessage(CAN_MSG_GET_TREBLE, byteArrayOf()))
        canBusSimulator.sendMessage(CanMessage(CAN_MSG_GET_MID, byteArrayOf()))
        canBusSimulator.sendMessage(CanMessage(CAN_MSG_GET_BASS, byteArrayOf()))
    }
}