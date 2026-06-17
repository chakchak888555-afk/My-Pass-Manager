package com.example.passmanager.data

import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

object Encryption {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH = 128
    private const val IV_LENGTH = 12
    private const val SALT_LENGTH = 32
    private const val ITERATIONS = 256000
    private const val KEY_LENGTH = 256

    private fun deriveKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }
        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    fun encrypt(plainText: String, masterPassword: String): String {
        val random = SecureRandom()
        
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        
        val iv = ByteArray(IV_LENGTH)
        random.nextBytes(iv)
        
        val secretKey = deriveKey(masterPassword, salt)
        val cipher = Cipher.getInstance(ALGORITHM)
        val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
        
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
        
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())
        
        val combined = salt + iv + encryptedBytes
        return combined.toHex()
    }

    fun decrypt(encryptedText: String, masterPassword: String): String? {
        return try {
            val combined = encryptedText.decodeHex()
            
            if (combined.size < SALT_LENGTH + IV_LENGTH) return null

            val salt = combined.sliceArray(0 until SALT_LENGTH)
            val iv = combined.sliceArray(SALT_LENGTH until SALT_LENGTH + IV_LENGTH)
            val encryptedBytes = combined.sliceArray(SALT_LENGTH + IV_LENGTH until combined.size)
            
            val secretKey = deriveKey(masterPassword, salt)
            val cipher = Cipher.getInstance(ALGORITHM)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
            
            String(cipher.doFinal(encryptedBytes))
        } catch (e: Exception) {
            null
        }
    }
}
