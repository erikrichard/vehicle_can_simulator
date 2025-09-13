package com.senai.myapplication.simulator

import android.util.Log
import com.senai.myapplication.utils.AppConstants.CAN_MSG_BASS
import com.senai.myapplication.utils.AppConstants.CAN_MSG_GET_BASS
import com.senai.myapplication.utils.AppConstants.CAN_MSG_GET_MID
import com.senai.myapplication.utils.AppConstants.CAN_MSG_GET_TREBLE
import com.senai.myapplication.utils.AppConstants.CAN_MSG_GET_VOLUME
import com.senai.myapplication.utils.AppConstants.CAN_MSG_MID
import com.senai.myapplication.utils.AppConstants.CAN_MSG_SET_BASS
import com.senai.myapplication.utils.AppConstants.CAN_MSG_SET_MID
import com.senai.myapplication.utils.AppConstants.CAN_MSG_SET_TREBLE
import com.senai.myapplication.utils.AppConstants.CAN_MSG_SET_VOLUME
import com.senai.myapplication.utils.AppConstants.CAN_MSG_TREBLE
import com.senai.myapplication.utils.AppConstants.CAN_MSG_VOLUME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioHeadUnit(private val canBus: VehicleCanBusSimulator) {

    private val TAG = "AudioHeadUnitECU"

    private var mVolume: Int = 0
    private var mBass: Int = 0
    private var mTreble: Int = 0
    private var mMid: Int = 0
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        Log.i(TAG, "Iniciando AHU")
        scope.launch {
            Log.i(TAG, "Iniciando simulador CAN")
            canBus.canMessageFlow.collect { message ->
                Log.i(TAG, "Menssagem recebida")
                canBusReceive(message)
            }
        }
    }

    private fun setVolume(newValue: Int) {
        Log.i(TAG, "Current volume: $mVolume")
        if (newValue != mVolume && newValue in 0..100) {
            mVolume = newValue
            Log.i(TAG, "new volume set to $mVolume")
        }
        canBusSend(CAN_MSG_VOLUME, byteArrayOf(mVolume.toByte()))
    }

    private fun setBass(newValue: Int) {
        Log.i(TAG, "Current Bass: $mBass")
        if (newValue != mBass) {
            mBass = newValue
            Log.i(TAG, "new Bass set to $mBass")
        }
        canBusSend(CAN_MSG_BASS, byteArrayOf(mBass.toByte()))
    }

    private fun setTreble(newValue: Int) {
        Log.i(TAG, "Current Treble: $mTreble")
        if (newValue != mTreble) {
            mTreble = newValue
            Log.i(TAG, "new Treble set to $mTreble")
        }
        canBusSend(CAN_MSG_TREBLE, byteArrayOf(mTreble.toByte()))
    }

    private fun setMid(newValue: Int) {
        Log.i(TAG, "Current Mid: $mMid")
        if (newValue != mMid) {
            mMid = newValue
            Log.i(TAG, "new Mid set to $mMid")
        }
        canBusSend(CAN_MSG_MID, byteArrayOf(mMid.toByte()))
    }

    private fun getVolume(): Int {
        return mVolume
    }

    private fun getBass(): Int {
        return mBass
    }

    private fun getTreble(): Int {
        return mTreble
    }

    private fun getMid(): Int {
        return mMid
    }

    private fun canBusSend(msgId: Int, data: ByteArray) {
        val message = CanMessage(msgId, data)
        canBus.sendMessage(message)
        Log.d(
            TAG,
            "Mensagem CAN enviada (ID: 0x%X, Dados: %s)".format(
                message.id,
                message.data.joinToString { "%02X".format(it) })
        )
    }

    private fun canBusReceive(message: CanMessage) {
        Log.d(
            TAG,
            "Mensagem CAN recebida (ID: 0x%X, Dados: %s)".format(
                message.id,
                message.data.joinToString { "%02X".format(it) })
        )
        when (message.id) {
            // Volume Signals
            CAN_MSG_SET_VOLUME -> {
                if (message.data.isNotEmpty()) {
                    Log.i(TAG, "Volume: ${message.data[0].toInt()}")
                    setVolume(message.data[0].toInt())
                }
            }

            CAN_MSG_VOLUME -> {
                canBusSend(CAN_MSG_GET_VOLUME, byteArrayOf(getVolume().toByte()))
            }

            // Treble Signals
            CAN_MSG_SET_TREBLE -> {
                if (message.data.isNotEmpty()) {
                    Log.i(TAG, "Treble: ${message.data[0].toInt()}")
                    setTreble(message.data[0].toInt())
                }
                canBusSend(CAN_MSG_TREBLE, byteArrayOf(getTreble().toByte()))
            }

            CAN_MSG_TREBLE -> {
                canBusSend(CAN_MSG_GET_TREBLE, byteArrayOf(getTreble().toByte()))
            }

            // Bass Signals
            CAN_MSG_SET_BASS -> {
                if (message.data.isNotEmpty()) {
                    Log.i(TAG, "Bass: ${message.data[0].toInt()}")
                    setBass(message.data[0].toInt())
                }
                canBusSend(CAN_MSG_BASS, byteArrayOf(getBass().toByte()))
            }

            CAN_MSG_GET_BASS -> {
                canBusSend(CAN_MSG_BASS, byteArrayOf(getBass().toByte()))
            }

            // Mid Signals
            CAN_MSG_SET_MID -> {
                if (message.data.isNotEmpty()) {
                    Log.i(TAG, "Mid: ${message.data[0].toInt()}")
                    setMid(message.data[0].toInt())
                }
                canBusSend(CAN_MSG_MID, byteArrayOf(getMid().toByte()))
            }

            CAN_MSG_GET_MID -> {
                canBusSend(CAN_MSG_MID, byteArrayOf(getMid().toByte()))
            }
        }
    }
}