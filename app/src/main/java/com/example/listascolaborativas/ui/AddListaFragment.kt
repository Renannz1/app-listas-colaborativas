package com.example.listascolaborativas.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.listascolaborativas.R
import com.example.listascolaborativas.databinding.FragmentAddListaBinding
import com.example.listascolaborativas.helper.FirebaseHelper
import com.example.listascolaborativas.model.LifecycleLogger
import com.example.listascolaborativas.model.Lista

class AddListaFragment : Fragment() {

    private var _binding: FragmentAddListaBinding? = null
    private val binding get() = _binding!!

    private lateinit var lista: Lista
    private  var newLista: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  FragmentAddListaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            val titulo = binding.edtTituloLista.text.toString()

            if(titulo.isNotEmpty()) {
                if (newLista) lista = Lista()
                lista.titulo = titulo

                saveLista()
            } else {
                Toast.makeText(requireContext(), "Informe o título da sua lista.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveLista() {
        val userId = FirebaseHelper.getUserId() ?: run {
            Toast.makeText(requireContext(), "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        val databaseRef = FirebaseHelper.getDatabase().child("lista").child(userId)

        // Gerar novo ID apenas se for uma nova lista
        val listRef = if (newLista) {
            val newRef = databaseRef.push()
            lista.id = newRef.key ?: "" // Define o ID gerado
            newRef
        } else {
            databaseRef.child(lista.id)
        }

        listRef.setValue(lista)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (newLista) {
                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), "Lista criada.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Lista atualizada.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar lista.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LifecycleLogger.addLog("AddListaFragment", "onStart")
    }

    override fun onStart() {
        super.onStart()
        LifecycleLogger.addLog("AddListaFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
        LifecycleLogger.addLog("AddListaFragment", "onResume")
    }

    override fun onPause() {
        super.onPause()
        LifecycleLogger.addLog("AddListaFragment", "onPause")
    }

    override fun onStop() {
        super.onStop()
        LifecycleLogger.addLog("AddListaFragment", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        LifecycleLogger.addLog("AddListaFragment", "onDestroy")
    }
}
