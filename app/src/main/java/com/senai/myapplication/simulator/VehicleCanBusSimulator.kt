package com.senai.myapplication.simulator

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class VehicleCanBusSimulator {

    private val TAG = "VehicleCanBusSimulator"

    private val messageChannel = Channel<CanMessage>()
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _canMessageFlow = MutableSharedFlow<CanMessage>()
    val canMessageFlow = _canMessageFlow.asSharedFlow()

    private val mAudioHeadUnit = AudioHeadUnit(this)

    init {
        scope.launch {
            for (message in messageChannel) {
                Log.d(TAG, "Mensagem CAN recebida (ID: 0x%X, Dados: %s)".format(message.id, message.data.joinToString { "%02X".format(it) }))
                // Publica a mensagem para qualquer ouvinte (ex: MainActivity)
                _canMessageFlow.emit(message)
            }
        }
    }
    /**
     * Simula o envio de uma mensagem CAN para o barramento.
     * @param message A mensagem CAN a ser enviada.
     */
    fun sendMessage(message: CanMessage) {
        scope.launch {
            Log.i(TAG, "Enviando mensagem CAN (ID: 0x%X, Dados: %s)".format(message.id, message.data.joinToString { "%02X".format(it) }))
            messageChannel.send(message)
        }
    }
}