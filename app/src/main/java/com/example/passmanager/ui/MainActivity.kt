package com.example.passmanager.ui

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passmanager.data.PasswordEntry
import com.example.passmanager.data.Storage
import com.example.passmanager.databinding.ActivityMainBinding
import com.example.passmanager.databinding.DialogAddPasswordBinding

/**
 * Главный экран приложения.
 * Управляет отображением списка паролей и взаимодействием с пользователем.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storage: Storage
    private lateinit var adapter: PasswordAdapter
    private var masterPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация ViewBinding для доступа к компонентам макета
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Получение мастер-пароля, переданного из экрана логина
        masterPassword = intent.getStringExtra("MASTER_PASSWORD") ?: ""
        storage = Storage(this)
        
        // Настройка адаптера для RecyclerView с обработчиками кликов
        adapter = PasswordAdapter(
            emptyList(),
            onEditClick = { entry -> showEditPasswordDialog(entry) },
            onDeleteClick = { serviceName -> showDeleteConfirmation(serviceName) }
        )
        binding.rvPasswords.layoutManager = LinearLayoutManager(this)
        binding.rvPasswords.adapter = adapter

        // Слушатель для кнопки добавления нового пароля
        binding.fabAdd.setOnClickListener {
            showAddPasswordDialog()
        }

        // Первичная загрузка данных
        refreshList()
    }

    /**
     * Обновляет список паролей на экране, считывая их из хранилища.
     */
    private fun refreshList() {
        val names = storage.getAllNames()
        val entries = names.mapNotNull { name ->
            // Дешифровка каждой записи с использованием мастер-пароля
            storage.getEntry(name, masterPassword)
        }
        adapter.updateData(entries)
    }

    /**
     * Отображает диалоговое окно для ввода данных нового сервиса.
     */
    private fun showAddPasswordDialog() {
        val dialogBinding = DialogAddPasswordBinding.inflate(layoutInflater)

        AlertDialog.Builder(this)
            .setTitle("Добавить пароль")
            .setView(dialogBinding.root)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = dialogBinding.etServiceName.text.toString()
                if (name.isNotEmpty()) {
                    val entry = PasswordEntry(
                        name,
                        dialogBinding.etLogin.text.toString(),
                        dialogBinding.etPassword.text.toString()
                    )
                    // Сохранение зашифрованных данных
                    storage.saveEntry(entry, masterPassword)
                    refreshList()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    /**
     * Отображает диалоговое окно для редактирования существующей записи.
     */
    private fun showEditPasswordDialog(entry: PasswordEntry) {
        val dialogBinding = DialogAddPasswordBinding.inflate(layoutInflater)

        // Предзаполнение полей текущими данными
        dialogBinding.etServiceName.setText(entry.serviceName)
        dialogBinding.etServiceName.isEnabled = false // Имя сервиса — уникальный ключ, менять нельзя
        dialogBinding.etLogin.setText(entry.login)
        dialogBinding.etPassword.setText(entry.password)

        AlertDialog.Builder(this)
            .setTitle("Редактировать")
            .setView(dialogBinding.root)
            .setPositiveButton("Обновить") { _, _ ->
                val updatedEntry = PasswordEntry(
                    entry.serviceName,
                    dialogBinding.etLogin.text.toString(),
                    dialogBinding.etPassword.text.toString()
                )
                // Перезапись данных в хранилище
                storage.saveEntry(updatedEntry, masterPassword)
                refreshList()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    /**
     * Запрашивает подтверждение перед безвозвратным удалением записи.
     */
    private fun showDeleteConfirmation(serviceName: String) {
        AlertDialog.Builder(this)
            .setTitle("Удаление")
            .setMessage("Удалить пароль для $serviceName?")
            .setPositiveButton("Да") { _, _ ->
                storage.deleteEntry(serviceName)
                refreshList()
            }
            .setNegativeButton("Нет", null)
            .show()
    }
}
