package com.example.listascolaborativas.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.listascolaborativas.R
import com.example.listascolaborativas.databinding.FragmentDetailListaBinding // Import the generated ViewBinding class
import com.example.listascolaborativas.helper.FirebaseHelper
import com.example.listascolaborativas.model.Lista

class DetailListaFragment : Fragment() {
    private var _binding: FragmentDetailListaBinding? = null
    private val binding get() = _binding!!

    private lateinit var lista: Lista

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailListaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recuperarLista()

        binding.btnExcluir.setOnClickListener {
            confirmarExclusao()
        }
    }

    private fun confirmarExclusao() {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir Lista")
            .setMessage("Tem certeza que deseja excluir esta lista?")
            .setPositiveButton("Sim") { _, _ -> excluirLista() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun recuperarLista() {
        val id = arguments?.getString("id") ?: ""
        val titulo = arguments?.getString("titulo") ?: ""

        Log.d("DetailListaFragment", "ID recebido: $id, Título recebido: $titulo") // Debug

        if (id.isNotEmpty()) {
            lista = Lista(id = id, titulo = titulo)
        } else {
            Toast.makeText(requireContext(), "Erro ao carregar lista.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun excluirLista() {
        if (lista.id.isEmpty()) {
            Toast.makeText(requireContext(), "Erro: ID da lista não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseHelper
            .getDatabase()
            .child("lista") // Nó principal onde as listas estão armazenadas
            .child(FirebaseHelper.getUserId() ?: "") // ID do usuário logado
            .child(lista.id) // ID da lista que será excluída
            .removeValue()
            .addOnSuccessListener {
                Log.d("DetailListaFragment", "Tentando excluir a lista com ID: ${lista.id}")
                Toast.makeText(requireContext(), "Lista excluída com sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_detailListaFragment_to_homeFragment)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao excluir a lista.", Toast.LENGTH_SHORT).show()
            }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}