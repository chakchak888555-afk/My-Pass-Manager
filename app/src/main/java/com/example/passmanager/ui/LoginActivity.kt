package com.example.passmanager.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.passmanager.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        val etMasterPassword = findViewById<EditText>(R.id.etMasterPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val password = etMasterPassword.text.toString()

            if (password.length >= 4) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("MASTER_PASSWORD", password)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Пароль должен быть не менее 4 символов", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
