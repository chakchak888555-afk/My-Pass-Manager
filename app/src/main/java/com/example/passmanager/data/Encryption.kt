package com.example.passmanager.data

import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

/**
 * Объект для выполнения криптографических операций.
 * Использует AES-256-GCM для шифрования и PBKDF2 для деривации ключа.
 */
object Encryption {
    // Константы алгоритмов и длин параметров
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH = 128 // Длина тега аутентификации GCM в битах
    private const val IV_LENGTH = 12 // Рекомендуемая длина IV для GCM
    private const val SALT_LENGTH = 32 // 256-битная соль для PBKDF2
    private const val ITERATIONS = 256000 // Количество итераций для защиты от брутфорса
    private const val KEY_LENGTH = 256 // Длина итогового AES ключа

    /**
     * Превращает мастер-пароль и соль в криптографический ключ.
     */
    private fun deriveKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Вспомогательная функция для перевода байтов в шестнадцатеричную строку.
     */
    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    /**
     * Вспомогательная функция для перевода шестнадцатеричной строки в байты.
     */
    private fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }
        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    /**
     * Основная функция шифрования.
     * Возвращает Hex-строку, содержащую Salt + IV + Ciphertext.
     */
    fun encrypt(plainText: String, masterPassword: String): String {
        val random = SecureRandom()
        
        // Генерация уникальной соли для этой операции
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        
        // Генерация уникального вектора инициализации (IV)
        val iv = ByteArray(IV_LENGTH)
        random.nextBytes(iv)
        
        // Генерация ключа из пароля
        val secretKey = deriveKey(masterPassword, salt)
        val cipher = Cipher.getInstance(ALGORITHM)
        val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
        
        // Инициализация шифра и выполнение операции
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())
        
        // Склейка всех компонентов в один блок данных
        val combined = salt + iv + encryptedBytes
        return combined.toHex()
    }

    /**
     * Функция дешифрования.
     * Разбирает Hex-строку, восстанавливает ключ и возвращает исходный текст.
     */
    fun decrypt(encryptedText: String, masterPassword: String): String? {
        return try {
            val combined = encryptedText.decodeHex()
            
            // Проверка минимально допустимой длины данных
            if (combined.size < SALT_LENGTH + IV_LENGTH) return null

            // Распиливание комбинированного массива на составляющие
            val salt = combined.sliceArray(0 until SALT_LENGTH)
            val iv = combined.sliceArray(SALT_LENGTH until SALT_LENGTH + IV_LENGTH)
            val encryptedBytes = combined.sliceArray(SALT_LENGTH + IV_LENGTH until combined.size)
            
            // Восстановление ключа с использованием той же соли
            val secretKey = deriveKey(masterPassword, salt)
            val cipher = Cipher.getInstance(ALGORITHM)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
            
            // Дешифрование и проверка целостности (GCM tag)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
            String(cipher.doFinal(encryptedBytes))
        } catch (e: Exception) {
            // Возврат null при ошибке ключа или повреждении данных
            null
        }
    }
}
