package com.example.listascolaborativas.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.listascolaborativas.databinding.FragmentItemBinding
import com.example.listascolaborativas.helper.FirebaseHelper
import com.example.listascolaborativas.model.Item

class ItemFragment : Fragment() {

    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var item: Item
    private var newItem: Boolean = true
    private var listaId: String = "" // ID da lista à qual o item pertence

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera o ID da lista passado como argumento
        listaId = arguments?.getString("listaId") ?: ""

        binding.btnSaveItem.setOnClickListener {
            val nome = binding.edtItemNome.text.toString()

            if (nome.isNotEmpty()) {
                if (newItem) item = Item()
                item.nome = nome
                item.listaId = listaId

                saveItem()
            } else {
                Toast.makeText(requireContext(), "Informe o nome do item.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveItem() {
        val userId = FirebaseHelper.Companion.getUserId() ?: run {
            Toast.makeText(requireContext(), "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        val databaseRef = FirebaseHelper.Companion.getDatabase()
            .child("itens")
            .child(userId)
            .child(listaId)

        // Gerar novo ID apenas se for um novo item
        val itemRef = if (newItem) {
            val newRef = databaseRef.push()
            item.id = newRef.key ?: "" // Define o ID gerado
            newRef
        } else {
            databaseRef.child(item.id)
        }

        itemRef.setValue(item)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (newItem) {
                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), "Item salvo.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Item atualizado.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar item.", Toast.LENGTH_SHORT).show()
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
}