package com.example.listascolaborativas.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.listascolaborativas.databinding.FragmentEditItemBinding
import com.example.listascolaborativas.helper.FirebaseHelper
import com.example.listascolaborativas.model.Item
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class EditItemFragment : Fragment() {

    private var _binding: FragmentEditItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var listaId: String
    private lateinit var itemId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera os dados do item
        recuperarDadosItem()

        // Configura o botão de salvar
        binding.btnSalvarItem.setOnClickListener {
            salvarItem()
        }
    }

    private fun recuperarDadosItem() {
        listaId = arguments?.getString("listaId") ?: ""
        itemId = arguments?.getString("itemId") ?: ""
        val itemNome = arguments?.getString("itemNome")

        if (!itemNome.isNullOrEmpty()) {
            binding.edtItemNome.setText(itemNome)
        } else {
            Toast.makeText(requireContext(), "Erro ao carregar item.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun salvarItem() {
        val nome = binding.edtItemNome.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(requireContext(), "O nome do item não pode estar vazio.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseHelper.getUserId()
        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Erro ao salvar item.", Toast.LENGTH_SHORT).show()
            return
        }

        val itemRef = FirebaseHelper.getDatabase()
            .child("itens")
            .child(userId)
            .child(listaId)
            .child(itemId)

        itemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existingItem = snapshot.getValue(Item::class.java)
                val updatedItem = existingItem?.copy(nome = nome) ?: Item(itemId, nome, listaId)

                itemRef.setValue(updatedItem)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Item atualizado!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Erro ao salvar.", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Erro ao carregar item.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}