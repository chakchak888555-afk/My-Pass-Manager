package com.example.passmanager.data

// Это простая модель данных для хранения одного пароля
data class PasswordEntry(
    val serviceName: String, // Название (например, Google или VK)
    val login: String,       // Логин
    val password: String     // Пароль
)
