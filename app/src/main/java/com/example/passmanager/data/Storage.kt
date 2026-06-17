package com.example.passmanager.data

import android.content.Context
import android.content.SharedPreferences

class Storage(context: Context) {
    private val preferences: SharedPreferences = 
        context.getSharedPreferences("secure_passwords", Context.MODE_PRIVATE)

    fun saveEntry(entry: PasswordEntry, masterPassword: String) {
        val rawData = "${entry.login}|${entry.password}"
        val encryptedData = Encryption.encrypt(rawData, masterPassword)
        preferences.edit().putString(entry.serviceName, encryptedData).apply()
    }

    fun getEntry(serviceName: String, masterPassword: String): PasswordEntry? {
        val encryptedData = preferences.getString(serviceName, null) ?: return null
        val decryptedData = Encryption.decrypt(encryptedData, masterPassword) ?: return null
        
        return try {
            val parts = decryptedData.split("|")
            if (parts.size >= 2) {
                PasswordEntry(serviceName, parts[0], parts[1])
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun getAllNames(): List<String> {
        return preferences.all.keys.toList().sorted()
    }

    fun deleteEntry(serviceName: String) {
        preferences.edit().remove(serviceName).apply()
    }
}
