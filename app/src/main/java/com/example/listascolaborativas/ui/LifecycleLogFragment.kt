package com.example.listascolaborativas.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.listascolaborativas.R
import com.example.listascolaborativas.databinding.FragmentLifecycleLogBinding
import com.example.listascolaborativas.model.LifecycleLogger

class LifecycleLogFragment : Fragment() {

    private var _binding: FragmentLifecycleLogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLifecycleLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logs = LifecycleLogger.getLogs().reversed() // Mostra os logs mais recentes primeiro
        binding.textLogs.text = logs.joinToString("\n")

        atualizarLogs()

        binding.btnLimparLogs.setOnClickListener {
            LifecycleLogger.clearLogs()
            atualizarLogs()
        }
    }

    private fun atualizarLogs() {
        val logs = LifecycleLogger.getLogs().reversed()
        binding.textLogs.text = if (logs.isNotEmpty()) {
            logs.joinToString("\n")
        } else {
            "Nenhum evento registrado."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
