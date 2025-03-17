package com.example.listascolaborativas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listascolaborativas.databinding.ListaAdapterBinding
import com.example.listascolaborativas.model.Lista

class ListaAdapter(private val ListaLista: List<Lista>) : RecyclerView.Adapter<ListaAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaAdapter.MyViewHolder {
        return MyViewHolder(
            ListaAdapterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ListaAdapter.MyViewHolder, position: Int) {
        val lista = ListaLista[position]

        holder.binding.textTituloLista.text = lista.titulo
        holder.binding.btnDetalharLista
    }

    override fun getItemCount() = ListaLista.size

    inner class MyViewHolder(val binding: ListaAdapterBinding) : RecyclerView.ViewHolder(binding.root)

}