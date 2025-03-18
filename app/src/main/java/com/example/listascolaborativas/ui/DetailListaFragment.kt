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
    private val itemList = mutableListOf<Item>() // Lista de itens

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailListaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera a lista
        recuperarLista()

        // Configura o RecyclerView e o Adapter
        setupRecyclerView()

        // Carrega os itens da lista
        loadItems()

        // Botão para adicionar itens
        binding.btnFlutuanteAdd.setOnClickListener {
            val bundle = Bundle().apply {
                putString("listaId", lista.id) // Passa o ID da lista como argumento
            }
            findNavController().navigate(R.id.action_detailListaFragment_to_itemFragment, bundle)
        }

        // Botão para excluir a lista
        binding.btnExcluir.setOnClickListener {
            confirmarExclusaoLista()
        }
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

    private fun setupRecyclerView() {
        itemAdapter = ItemAdapter(itemList) // Adapter para exibir os itens
        binding.recyclerViewItensLista.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewItensLista.setHasFixedSize(true)
        binding.recyclerViewItensLista.adapter = itemAdapter
        Log.d("DetailListaFragment", "RecyclerView configurado")
    }

    private fun loadItems() {
        val userId = FirebaseHelper.getUserId() ?: return

        Log.d("DetailListaFragment", "Carregando itens para a lista: ${lista.id}")

        FirebaseHelper.getDatabase()
            .child("itens")
            .child(userId)
            .child(lista.id) // Usa o ID da lista para carregar os itens
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    itemList.clear()

                    if (!snapshot.exists()) {
                        Log.d("DetailListaFragment", "Nenhum item encontrado para esta lista")
                        return
                    }

                    for (snap in snapshot.children) {
                        val item = snap.getValue(Item::class.java) as? Item
                        if (item != null) {
                            itemList.add(item)
                            Log.d("DetailListaFragment", "Item carregado: ${item.nome}")
                        }
                    }

                    itemAdapter.notifyDataSetChanged() // Atualiza o adapter
                    Log.d("DetailListaFragment", "Itens carregados: ${itemList.size}")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro ao carregar itens.", Toast.LENGTH_SHORT).show()
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
        if (lista.id.isEmpty()) {
            Toast.makeText(requireContext(), "Erro: ID da lista não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseHelper
            .getDatabase()
            .child("lista")
            .child(FirebaseHelper.getUserId() ?: "")
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