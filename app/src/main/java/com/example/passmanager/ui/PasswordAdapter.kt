package com.example.passmanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.passmanager.data.PasswordEntry
import com.example.passmanager.databinding.ItemPasswordBinding

class PasswordAdapter(
    private var items: List<PasswordEntry>,
    private val onEditClick: (PasswordEntry) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<PasswordAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPasswordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPasswordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvName.text = item.serviceName
        holder.binding.tvLogin.text = "Логин: ${item.login}"
        holder.binding.tvPassword.text = "Пароль: ${item.password}"
        
        holder.itemView.setOnClickListener {
            onEditClick(item)
        }

        holder.itemView.setOnLongClickListener {
            onDeleteClick(item.serviceName)
            true
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<PasswordEntry>) {
        items = newItems
        notifyDataSetChanged()
    }
}
