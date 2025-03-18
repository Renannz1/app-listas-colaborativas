package com.example.listascolaborativas.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listascolaborativas.R
import com.example.listascolaborativas.adapter.ListaAdapter
import com.example.listascolaborativas.databinding.FragmentHomeBinding
import com.example.listascolaborativas.helper.FirebaseHelper
import com.example.listascolaborativas.model.Lista
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var listaAdapter: ListaAdapter
    private val listaLista = mutableListOf<Lista>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        binding.btnFlutuanteAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addListaFragment)
        }

        initAdapter()
        getLista()
    }

    private fun getLista() {
        FirebaseHelper
            .getDatabase()
            .child("lista")
            .child(FirebaseHelper.getUserId() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        listaLista.clear()

                        for (snap in snapshot.children) {
                            snap.getValue(Lista::class.java)?.let { listaLista.add(it) }
                        }

                        listaAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (_binding != null) {
                        Toast.makeText(
                            requireContext(),
                            "Erro ao tentar recuperar as tabelas.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

    private fun initAdapter() {
        binding.recyclerViewLista.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewLista.setHasFixedSize(true)
        listaAdapter = ListaAdapter(listaLista) { lista ->
            val bundle = Bundle().apply {
                putString("id", lista.id)
                putString("titulo", lista.titulo)
            }
            findNavController().navigate(R.id.action_homeFragment_to_detailListaFragment, bundle)
        }
        binding.recyclerViewLista.adapter = listaAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
