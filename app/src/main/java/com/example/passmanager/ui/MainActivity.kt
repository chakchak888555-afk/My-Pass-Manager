package com.example.passmanager.ui

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passmanager.data.PasswordEntry
import com.example.passmanager.data.Storage
import com.example.passmanager.databinding.ActivityMainBinding
import com.example.passmanager.databinding.DialogAddPasswordBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storage: Storage
    private lateinit var adapter: PasswordAdapter
    private var masterPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        masterPassword = intent.getStringExtra("MASTER_PASSWORD") ?: ""
        storage = Storage(this)
        
        adapter = PasswordAdapter(
            emptyList(),
            onEditClick = { entry -> showEditPasswordDialog(entry) },
            onDeleteClick = { serviceName -> showDeleteConfirmation(serviceName) }
        )
        binding.rvPasswords.layoutManager = LinearLayoutManager(this)
        binding.rvPasswords.adapter = adapter

        binding.fabAdd.setOnClickListener {
            showAddPasswordDialog()
        }

        refreshList()
    }

    private fun refreshList() {
        val names = storage.getAllNames()
        val entries = names.mapNotNull { name ->
            storage.getEntry(name, masterPassword)
        }
        adapter.updateData(entries)
    }

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
                    storage.saveEntry(entry, masterPassword)
                    refreshList()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditPasswordDialog(entry: PasswordEntry) {
        val dialogBinding = DialogAddPasswordBinding.inflate(layoutInflater)

        dialogBinding.etServiceName.setText(entry.serviceName)
        dialogBinding.etServiceName.isEnabled = false 
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
                storage.saveEntry(updatedEntry, masterPassword)
                refreshList()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

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
