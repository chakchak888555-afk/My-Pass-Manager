package com.example.passmanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.passmanager.data.PasswordEntry
import com.example.passmanager.databinding.ItemPasswordBinding

/**
 * Адаптер для отображения списка паролей в RecyclerView.
 * Связывает данные PasswordEntry с элементами интерфейса item_password.xml.
 */
class PasswordAdapter(
    private var items: List<PasswordEntry>,
    private val onEditClick: (PasswordEntry) -> Unit, // Коллбэк для редактирования
    private val onDeleteClick: (String) -> Unit        // Коллбэк для удаления
) : RecyclerView.Adapter<PasswordAdapter.ViewHolder>() {

    /**
     * ViewHolder удерживает ссылку на сгенерированный класс binding для каждой строки списка.
     */
    class ViewHolder(val binding: ItemPasswordBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * Создает новый элемент списка (инфлейтит разметку).
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPasswordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    /**
     * Наполняет созданный элемент списка данными из конкретного объекта PasswordEntry.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvName.text = item.serviceName
        holder.binding.tvLogin.text = "Логин: ${item.login}"
        holder.binding.tvPassword.text = "Пароль: ${item.password}"
        
        // Обработка короткого клика — переход к редактированию
        holder.itemView.setOnClickListener {
            onEditClick(item)
        }

        // Обработка длинного нажатия — вызов удаления
        holder.itemView.setOnLongClickListener {
            onDeleteClick(item.serviceName)
            true
        }
    }

    /**
     * Возвращает общее количество элементов в текущем списке.
     */
    override fun getItemCount() = items.size

    /**
     * Обновляет набор данных и уведомляет RecyclerView о необходимости перерисовки.
     */
    fun updateData(newItems: List<PasswordEntry>) {
        items = newItems
        notifyDataSetChanged()
    }
}
