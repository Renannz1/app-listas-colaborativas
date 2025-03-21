package com.example.listascolaborativas.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.listascolaborativas.R

class CiclosFragment : Fragment() {

    private val TAG = "CiclosFragment"
    private lateinit var tvLifecycleEvents: TextView
    private val eventLog = mutableListOf<String>() // Lista para armazenar eventos

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logEvent("onAttach() chamado")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logEvent("onCreate() chamado")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logEvent("onCreateView() chamado")
        val view = inflater.inflate(R.layout.fragment_ciclos, container, false)
        tvLifecycleEvents = view.findViewById(R.id.tvLifecycleEvents)

        // Atualizar a tela com eventos anteriores ao recriar a View
        updateTextView()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logEvent("onViewCreated() chamado")
    }

    override fun onStart() {
        super.onStart()
        logEvent("onStart() chamado")
    }

    override fun onResume() {
        super.onResume()
        logEvent("onResume() chamado")
    }

    override fun onPause() {
        super.onPause()
        logEvent("onPause() chamado")
    }

    override fun onStop() {
        super.onStop()
        logEvent("onStop() chamado")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logEvent("onDestroyView() chamado")
    }

    override fun onDestroy() {
        super.onDestroy()
        logEvent("onDestroy() chamado")
    }

    override fun onDetach() {
        super.onDetach()
        logEvent("onDetach() chamado")
    }

    // Método para registrar eventos e atualizar a interface
    private fun logEvent(event: String) {
        val fullEvent = "$TAG: $event"
        Log.d(TAG, fullEvent)
        eventLog.add(fullEvent)

        // Atualizar a interface de forma segura na thread principal
        activity?.runOnUiThread {
            updateTextView()
        }
    }

    // Atualizar a TextView com todos os eventos armazenados
    private fun updateTextView() {
        if (::tvLifecycleEvents.isInitialized) {
            tvLifecycleEvents.post {
                tvLifecycleEvents.text = eventLog.joinToString("\n")
            }
        }
    }
}
