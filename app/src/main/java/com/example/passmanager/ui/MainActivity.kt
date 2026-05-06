package com.example.passmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passmanager.R
import com.example.passmanager.data.PasswordEntry
import com.example.passmanager.data.Storage
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var storage: Storage
    private lateinit var adapter: PasswordAdapter
    private var masterPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Получаем мастер-пароль из предыдущего экрана
        masterPassword = intent.getStringExtra("MASTER_PASSWORD") ?: ""
        
        storage = Storage(this)
        
        val rvPasswords = findViewById<RecyclerView>(R.id.rvPasswords)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        // Настраиваем список
        adapter = PasswordAdapter(emptyList()) { serviceName ->
            showDeleteConfirmation(serviceName)
        }
        rvPasswords.layoutManager = LinearLayoutManager(this)
        rvPasswords.adapter = adapter

        fabAdd.setOnClickListener {
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
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_password, null)
        val etName = dialogView.findViewById<EditText>(R.id.etServiceName)
        val etLogin = dialogView.findViewById<EditText>(R.id.etLogin)
        val etPass = dialogView.findViewById<EditText>(R.id.etPassword)

        AlertDialog.Builder(this)
            .setTitle("Добавить пароль")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = etName.text.toString()
                if (name.isNotEmpty()) {
                    val entry = PasswordEntry(
                        name,
                        etLogin.text.toString(),
                        etPass.text.toString()
                    )
                    storage.saveEntry(entry, masterPassword)
                    refreshList()
                }
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
