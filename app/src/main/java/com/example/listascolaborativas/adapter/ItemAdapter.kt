package com.example.listascolaborativas.adapter

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.listascolaborativas.R
import com.example.listascolaborativas.model.Item
import com.example.listascolaborativas.helper.FirebaseHelper

class ItemAdapter(
    private val context: Context,
    private val itemList: MutableList<Item>,
    private val listaId: String // Adicionado para passar o ID da lista
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_adapter, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)

        // Ao clicar no item, navega para o EditItemFragment
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("listaId", listaId) // Passa o ID da lista
                putString("itemId", item.id)
                putString("itemNome", item.nome)
            }
            it.findNavController().navigate(R.id.action_detailListaFragment_to_editItemFragment, bundle)
        }

        // Ao clicar por tempo longo, exibe o diálogo de editar/excluir
        holder.itemView.setOnLongClickListener {
            showEditDeleteDialog(item, position, holder.itemView)
            true
        }
    }

    override fun getItemCount(): Int = itemList.size

    private fun showEditDeleteDialog(item: Item, position: Int, view: View) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Escolha uma ação")
        builder.setMessage("Deseja editar ou excluir este item?")

        builder.setPositiveButton("Editar") { _, _ ->
            val bundle = Bundle().apply {
                putString("listaId", listaId) // Passa o ID da lista
                putString("itemId", item.id)
                putString("itemNome", item.nome)
            }
            view.findNavController().navigate(R.id.action_detailListaFragment_to_editItemFragment, bundle)
        }

        builder.setNegativeButton("Excluir") { _, _ ->
            val userId = FirebaseHelper.getUserId()
            if (userId != null && !item.id.isNullOrEmpty()) {
                FirebaseHelper.getDatabase()
                    .child("itens")
                    .child(userId)
                    .child(listaId)
                    .child(item.id)
                    .removeValue()
                    .addOnSuccessListener {
                        itemList.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(context, "Item excluído", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Erro ao excluir", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        builder.setNeutralButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNome: TextView = itemView.findViewById(R.id.ItemNome)

        fun bind(item: Item) {
            itemNome.text = item.nome
        }
    }
}