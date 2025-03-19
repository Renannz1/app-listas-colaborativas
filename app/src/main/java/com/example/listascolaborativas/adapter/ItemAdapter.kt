package com.example.listascolaborativas.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Paint
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
import android.widget.CheckBox


class ItemAdapter(
    private val context: Context,
    private val itemList: MutableList<Item>,
    private val listaId: String
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_adapter, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)

        // Ao clicar no checkbox, marca o item como concluído
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            item.concluido = isChecked
            atualizarItemNoFirebase(item)
            if (isChecked) {
                // Move o item para o final da lista
                itemList.removeAt(position)
                itemList.add(item)
                notifyItemMoved(position, itemList.size - 1)
            }
        }

        // Ao clicar no item, navega para o EditItemFragment
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("listaId", listaId)
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

        // Botão para editar
        builder.setPositiveButton("Editar") { _, _ ->
            val bundle = Bundle().apply {
                putString("listaId", listaId)
                putString("itemId", item.id)
                putString("itemNome", item.nome)
            }
            view.findNavController().navigate(R.id.action_detailListaFragment_to_editItemFragment, bundle)
        }

        // Botão para excluir
        builder.setNegativeButton("Excluir") { _, _ ->
            // Exibe uma mensagem de confirmação antes de excluir
            confirmarExclusaoItem(item, position)
        }

        // Botão para cancelar
        builder.setNeutralButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun confirmarExclusaoItem(item: Item, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirmar exclusão")
        builder.setMessage("Tem certeza que deseja excluir este item?")

        // Botão para confirmar a exclusão
        builder.setPositiveButton("Sim") { _, _ ->
            excluirItemDoFirebase(item, position)
        }

        // Botão para cancelar
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun excluirItemDoFirebase(item: Item, position: Int) {
        val userId = FirebaseHelper.getUserId()
        if (userId != null && !item.id.isNullOrEmpty()) {
            FirebaseHelper.getDatabase()
                .child("itens")
                .child(userId)
                .child(listaId)
                .child(item.id)
                .removeValue()
                .addOnSuccessListener {
                    if (position >= 0 && position < itemList.size) {
                        itemList.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(context, "Item excluído com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Erro: Posição inválida.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao excluir o item.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun atualizarItemNoFirebase(item: Item) {
        val userId = FirebaseHelper.getUserId()
        if (userId != null && !item.id.isNullOrEmpty()) {
            FirebaseHelper.getDatabase()
                .child("itens")
                .child(userId)
                .child(listaId)
                .child(item.id)
                .setValue(item)
                .addOnSuccessListener {
                    Toast.makeText(context, "Item atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao atualizar o item.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemNome: TextView = itemView.findViewById(R.id.ItemNome)
        val checkbox: CheckBox = itemView.findViewById(R.id.checkboxItem)

        fun bind(item: Item) {
            itemNome.text = item.nome
            checkbox.isChecked = item.concluido ?: false

            // Aplica o estilo riscado quando o item estiver concluído
            if (item.concluido == true) {
                itemNome.paintFlags = itemNome.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                itemNome.paintFlags = itemNome.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
    }
}



