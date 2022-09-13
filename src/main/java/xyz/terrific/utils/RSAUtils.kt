package xyz.terrific.utils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

class RSAUtils {
    var privateKey: PrivateKey? = null
    var publicKey: PublicKey? = null
    @Throws(NoSuchAlgorithmException::class, IOException::class)
    fun generateKeyPair(size: Int) {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(size)
        val pair = keyGen.generateKeyPair()
        privateKey = pair.private
        publicKey = pair.public
        writeToFile("key.priv", _getStringPrivateKey(), "PRIVATE")
        writeToFile("key.pub", _getStringPublicKey(), "PUBLIC")
    }

    @Throws(IOException::class)
    fun readKeyFile(path: String?): String {
        val pubKeyFile = File(path)
        return String(Files.readAllBytes(pubKeyFile.toPath()))
            .replace("\n", "")
            .replace("\r", "")
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
    }

    @Throws(IOException::class)
    fun writeToFile(path: String?, key: String, type: String) {
        val f = File(path)
        val fos = FileOutputStream(f)
        val res = key.replace(".{77}".toRegex(), "$0\r\n").transform { s: String ->
            s.substring(
                0,
                s.length - if (s.length % 77 == 0) 1 else 0
            )
        }
        fos.write("-----BEGIN $type KEY-----\r\n".toByteArray())
        fos.write(res.toByteArray())
        fos.write("\r\n-----END $type KEY-----\r\n".toByteArray())
        fos.flush()
        fos.close()
    }

    fun _getPrivateKey(): PrivateKey? {
        return privateKey
    }

    fun _getPublicKey(): PublicKey? {
        return publicKey
    }

    fun _getStringPrivateKey(): String {
        return Base64.getEncoder().encodeToString(_getPrivateKey()!!.encoded)
    }

    fun _getStringPublicKey(): String {
        return Base64.getEncoder().encodeToString(_getPublicKey()!!.encoded)
    }

    fun getPublicKey(base64PublicKey: String): PublicKey? {
        var publicKey: PublicKey? = null
        try {
            val keySpec = X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.toByteArray()))
            val keyFactory = KeyFactory.getInstance("RSA")
            publicKey = keyFactory.generatePublic(keySpec)
            return publicKey
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        }
        return publicKey
    }

    fun getPrivateKey(base64PrivateKey: String): PrivateKey? {
        var privateKey: PrivateKey? = null
        val keySpec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.toByteArray()))
        var keyFactory: KeyFactory? = null
        try {
            keyFactory = KeyFactory.getInstance("RSA")
            privateKey = keyFactory.generatePrivate(keySpec)
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return privateKey
    }

    @Throws(
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidKeyException::class,
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class
    )
    fun encrypt(data: String, publicKey: String): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey))
        return cipher.doFinal(data.toByteArray())
    }

    @Throws(
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidKeyException::class,
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class
    )
    fun encrypt(data: String, publicKey: PublicKey?): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data.toByteArray())
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun decrypt(data: ByteArray?, privateKey: PrivateKey?): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return String(cipher.doFinal(data))
    }

    @Throws(
        IllegalBlockSizeException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class
    )
    fun decrypt(data: String, base64PrivateKey: String): String {
        return decrypt(Base64.getDecoder().decode(data.toByteArray()), getPrivateKey(base64PrivateKey))
    }
}
