package org.aladin.android.sdk.model.network

import org.json.JSONObject
import java.math.BigInteger

/**
 *  Object describing unit and amount of a price request.
 */
class Denomination(private val jsonObject: JSONObject) {
    /**
     * The unit of the cryptocurrency, e.g. BTC, STACKS.
     */
    val units: String?
        get() {
            return jsonObject.optString("units")
        }

    /**
     * The amount of the price information in the smallest denomination
     * e.g. if `units` is BTC, `amount` will be in satoshis; if `units` is STACKS, `amount` will be in microStacks.
     */
    val amount: BigInteger?
        get() = jsonObject.optString("amount")?.let { BigInteger(it, 16) }

    /**
     * The `JSONObject` that backs this object. You use this object to
     * access properties that are not yet exposed by this class.
     */
    val json: JSONObject
        get() {
            return jsonObject
        }
}