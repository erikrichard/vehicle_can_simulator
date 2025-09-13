package com.senai.myapplication.ui.diagnostic

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        dashboardViewModel.speed.observe(viewLifecycleOwner) {
            Log.i(TAG, "Velocidade Atual: $it")
            velocity.text = it
        }
        val volume: TextView = binding.canVolumeLabel
        dashboardViewModel.volume.observe(viewLifecycleOwner) {
            Log.i(TAG, "Volume CAN: $it")
            volume.text = it
        }

        dashboardViewModel.treble.observe(viewLifecycleOwner) {
            Log.i(TAG, "Treble: $it")
        }

        dashboardViewModel.mid.observe(viewLifecycleOwner) {
            Log.i(TAG, "Mid: $it")
        }

        dashboardViewModel.bass.observe(viewLifecycleOwner) {
            Log.i(TAG, "Bass: $it")
        }

        binding.readSpeedButton.setOnClickListener {
            Log.i(TAG, "Solicitando atualização de velocidade")
            dashboardViewModel.updateSpeedData()
        }
        binding.sendCanVolumeButton.setOnClickListener {
            Log.i(TAG, "Solicitando atualização de volume")
            dashboardViewModel.updateVolumeData()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}