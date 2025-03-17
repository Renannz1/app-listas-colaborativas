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
        FirebaseHelper
            .getDatabase().child("lista")
            .child(FirebaseHelper.getUserId() ?: "")
            .child(lista.id)
            .setValue(lista)
            .addOnCompleteListener{ lista ->
                if (lista.isSuccessful){
                    if (newLista){ // Criando nova tarefa
                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), "Lista criada.", Toast.LENGTH_SHORT).show()
                    } else { // Editando tarefa
                        Toast.makeText(requireContext(), "Lista atualizada.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro ao tentar criar a lista.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao tentar criar a lista.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
