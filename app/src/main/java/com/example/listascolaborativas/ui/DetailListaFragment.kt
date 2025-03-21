package com.example.listascolaborativas.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listascolaborativas.R
import com.example.listascolaborativas.adapter.ItemAdapter
import com.example.listascolaborativas.databinding.FragmentDetailListaBinding
import com.example.listascolaborativas.helper.FirebaseHelper
import com.example.listascolaborativas.model.Item
import com.example.listascolaborativas.model.Lista
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DetailListaFragment : Fragment() {

    private var _binding: FragmentDetailListaBinding? = null
    private val binding get() = _binding!!

    private lateinit var lista: Lista
    private lateinit var itemAdapter: ItemAdapter
    private val itemList = mutableListOf<Item>()

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
        setupRecyclerView()
        loadItems()

        binding.btnFlutuanteAdd.setOnClickListener {
            if (::lista.isInitialized) {
                val bundle = Bundle().apply { putString("listaId", lista.id) }
                findNavController().navigate(R.id.action_detailListaFragment_to_itemFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Erro ao carregar lista.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editTituloLista.setText(lista.titulo)

        binding.btnExcluir.setOnClickListener {
            confirmarExclusaoLista()
        }

        binding.btnSalvar.setOnClickListener {
            salvarAlteracao()
        }
    }

    private fun recuperarLista() {
        val id = arguments?.getString("id")
        val titulo = arguments?.getString("titulo")

        if (!id.isNullOrEmpty() && !titulo.isNullOrEmpty()) {
            lista = Lista(id = id, titulo = titulo)
        } else {
            Toast.makeText(requireContext(), "Erro ao carregar lista.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun salvarAlteracao(){
        val novoTitulo = binding.editTituloLista.text.toString().trim()

        if (novoTitulo.isEmpty()){
            Toast.makeText(requireContext(), "O título não pode ser vazio!", Toast.LENGTH_SHORT).show()
            return
        }

        lista.titulo = novoTitulo

        FirebaseHelper
            .getDatabase()
            .child("lista")
            .child(FirebaseHelper.getUserId() ?: "")
            .child(lista.id)
            .setValue(lista)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Lista atualizada com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao atualizar a lista.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupRecyclerView() {
        itemAdapter = ItemAdapter(requireContext(), itemList, lista.id) // Passa o ID da lista
        binding.recyclerViewItensLista.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = itemAdapter
        }
    }

    private fun loadItems() {
        val userId = FirebaseHelper.getUserId()
        if (userId.isNullOrEmpty() || !::lista.isInitialized) return

        FirebaseHelper.getDatabase()
            .child("itens")
            .child(userId)
            .child(lista.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    itemList.clear()
                    snapshot.children.mapNotNullTo(itemList) { it.getValue(Item::class.java) }
                    itemAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DetailListaFragment", "Erro ao carregar itens: ${error.message}")
                }
            })
    }


    private fun confirmarExclusaoLista() {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir Lista")
            .setMessage("Tem certeza que deseja excluir esta lista?")
            .setPositiveButton("Sim") { _, _ -> excluirLista() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun excluirLista() {
        if (!::lista.isInitialized) return
        val userId = FirebaseHelper.getUserId()
        if (userId.isNullOrEmpty()) return

        FirebaseHelper.getDatabase()
            .child("lista")
            .child(userId)
            .child(lista.id)
            .removeValue()
            .addOnSuccessListener {
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