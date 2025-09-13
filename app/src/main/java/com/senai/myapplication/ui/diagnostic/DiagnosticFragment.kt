package com.senai.myapplication.ui.diagnostic

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.senai.myapplication.databinding.FragmentDiagnosticBinding

class DiagnosticFragment : Fragment() {

    private var _binding: FragmentDiagnosticBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val TAG = "DiagnosticFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DiagnosticViewModel::class.java)

        _binding = FragmentDiagnosticBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val velocity: TextView = binding.speedLabel
        val volume: TextView = binding.canVolumeLabel
        val bassSeekBar: SeekBar = binding.bassSeekBar
        val midSeekBar: SeekBar = binding.midSeekBar
        val trebleSeekBar: SeekBar = binding.trebleSeekBar
        val equalizerSwitch: SwitchCompat = binding.equalizerSwitch
        equalizerSwitch.isChecked = false
        toggleEqualizer(equalizerSwitch.isChecked)

        dashboardViewModel.speed.observe(viewLifecycleOwner) {
            Log.i(TAG, "Velocidade Atual: $it")
            velocity.text = it
        }
        dashboardViewModel.volume.observe(viewLifecycleOwner) {
            Log.i(TAG, "Volume CAN: $it")
            volume.text = it
        }

        dashboardViewModel.treble.observe(viewLifecycleOwner) {
            Log.i(TAG, "Treble: $it")
            trebleSeekBar.progress = it.toInt()
        }

        dashboardViewModel.mid.observe(viewLifecycleOwner) {
            Log.i(TAG, "Mid: $it")
            midSeekBar.progress = it.toInt()
        }

        dashboardViewModel.bass.observe(viewLifecycleOwner) {
            Log.i(TAG, "Bass: $it")
            bassSeekBar.progress = it.toInt()
        }

        binding.readSpeedButton.setOnClickListener {
            Log.i(TAG, "Solicitando atualização de velocidade")
            dashboardViewModel.updateSpeedData()
        }
        binding.sendCanVolumeButton.setOnClickListener {
            Log.i(TAG, "Solicitando atualização de volume")
            dashboardViewModel.updateVolumeData()
        }

        binding.equalizerSwitch.setOnCheckedChangeListener { _, isChecked ->
            Log.i(TAG, "Estado do Equalizer: $isChecked")
            toggleEqualizer(isChecked)
        }

        binding.midSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Log.i(TAG, "Mid SeekBar Progress: $p0")
                if (p0 != null) {
                    dashboardViewModel.updateMidData(p0.progress)
                }
            }

        })

        binding.trebleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Log.i(TAG, "Treble SeekBar Progress: $p0")
                if (p0 != null) {
                    dashboardViewModel.updateTrebleData(p0.progress)
                }
            }

        })

        binding.bassSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Log.i(TAG, "Bass SeekBar Touch Stopped")
                if (p0 != null) {
                    dashboardViewModel.updateBassData(p0.progress)
                }
            }

        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toggleEqualizer(state: Boolean) {
        Log.i(TAG, "Estado do Equalizer: $state")
        if (state) {
            binding.midSeekBar.visibility = View.VISIBLE
            binding.trebleSeekBar.visibility = View.VISIBLE
            binding.bassSeekBar.visibility = View.VISIBLE
        } else {
            binding.midSeekBar.visibility = View.GONE
            binding.trebleSeekBar.visibility = View.GONE
            binding.bassSeekBar.visibility = View.GONE
        }
    }
}