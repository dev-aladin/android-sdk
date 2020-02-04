package org.aladin.android.sdk.model

import org.json.JSONObject

/**
 * An object to configure options for `putFile` operations.
 *
 * @property encrypt encrypt the with the private key of the current user before writing to storage
 * @property contentType contentType of file to be used only if not encrypted
 * @property sign (Boolean or String) If set to `true` the data is signed using ECDSA on SHA256 hashes
 * with the user's app private key. If a string is specified, it is used as the private key instead
 * of the user's app private key. The signature can be verified with `GetFileOptions.verify` set to
 * true when retrieving the data.
 */
public class PutFileOptions(val encrypt: Boolean = true, val contentType: String? = null, val sign: Any = false) {

    /**
     * json representation of these options as used by aladin.js
     */
    fun toJSON(): JSONObject {
        val optionsObject = JSONObject()
        optionsObject.put("encrypt", encrypt)
        if (!encrypt && contentType != null) {
            optionsObject.put("contentType", contentType)
        }
        optionsObject.put("sign", sign)
        return optionsObject
    }

    /**
     * string representation in json format used by aladin.js
     */
    override fun toString(): String {
        return toJSON().toString()
    }

}