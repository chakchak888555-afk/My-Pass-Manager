package com.example.passmanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.passmanager.R
import com.example.passmanager.data.PasswordEntry

class PasswordAdapter(
    private var items: List<PasswordEntry>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<PasswordAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvLogin: TextView = view.findViewById(R.id.tvLogin)
        val tvPassword: TextView = view.findViewById(R.id.tvPassword)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.serviceName
        holder.tvLogin.text = "Логин: ${item.login}"
        holder.tvPassword.text = "Пароль: ${item.password}"
        
        // Долгое нажатие для удаления
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
