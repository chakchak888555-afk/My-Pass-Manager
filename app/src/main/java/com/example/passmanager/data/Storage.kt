package com.example.passmanager.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Класс для работы с памятью телефона. 
 * Все данные шифруются перед сохранением.
 */
class Storage(context: Context) {
    private val preferences: SharedPreferences = 
        context.getSharedPreferences("secure_passwords", Context.MODE_PRIVATE)

    /**
     * Сохраняем данные: название сервиса и объект с логином/паролем.
     * Мы превращаем всё в одну строку и шифруем её.
     */
    fun saveEntry(entry: PasswordEntry, masterPassword: String) {
        val rawData = "${entry.login}|${entry.password}"
        val encryptedData = Encryption.encrypt(rawData, masterPassword)
        preferences.edit().putString(entry.serviceName, encryptedData).apply()
    }

    /**
     * Загружаем данные и расшифровываем их.
     */
    fun getEntry(serviceName: String, masterPassword: String): PasswordEntry? {
        val encryptedData = preferences.getString(serviceName, null) ?: return null
        return try {
            val decryptedData = Encryption.decrypt(encryptedData, masterPassword)
            val parts = decryptedData.split("|")
            PasswordEntry(serviceName, parts[0], parts[1])
        } catch (e: Exception) {
            null // Если пароль неверный, расшифровка не удастся
        }
    }

    fun getAllNames(): List<String> {
        return preferences.all.keys.toList().sorted()
    }

    fun deleteEntry(serviceName: String) {
        preferences.edit().remove(serviceName).apply()
    }
}
