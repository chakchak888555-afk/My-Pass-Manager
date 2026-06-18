package com.example.passmanager.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Класс для управления постоянным хранением данных.
 * Использует SharedPreferences в режиме private для изоляции данных приложения.
 */
class Storage(context: Context) {
    // Инициализация хранилища. Файл доступен только этому приложению (MODE_PRIVATE).
    private val preferences: SharedPreferences = 
        context.getSharedPreferences("secure_passwords", Context.MODE_PRIVATE)

    /**
     * Шифрует и сохраняет учетную запись.
     * Данные склеиваются через "|" перед шифрованием.
     */
    fun saveEntry(entry: PasswordEntry, masterPassword: String) {
        val rawData = "${entry.login}|${entry.password}"
        val encryptedData = Encryption.encrypt(rawData, masterPassword)
        // Запись зашифрованной Hex-строки под ключом имени сервиса
        preferences.edit().putString(entry.serviceName, encryptedData).apply()
    }

    /**
     * Извлекает, дешифрует и парсит учетную запись.
     */
    fun getEntry(serviceName: String, masterPassword: String): PasswordEntry? {
        val encryptedData = preferences.getString(serviceName, null) ?: return null
        val decryptedData = Encryption.decrypt(encryptedData, masterPassword) ?: return null
        
        return try {
            // Разделение расшифрованной строки обратно на логин и пароль
            val parts = decryptedData.split("|")
            if (parts.size >= 2) {
                PasswordEntry(serviceName, parts[0], parts[1])
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Возвращает список всех названий сервисов (ключей) для отображения в списке.
     */
    fun getAllNames(): List<String> {
        return preferences.all.keys.toList().sorted()
    }

    /**
     * Удаляет запись из локального хранилища по названию сервиса.
     */
    fun deleteEntry(serviceName: String) {
        preferences.edit().remove(serviceName).apply()
    }
}
