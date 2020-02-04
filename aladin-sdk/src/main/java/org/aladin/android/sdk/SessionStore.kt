package org.aladin.android.sdk

import android.content.SharedPreferences
import android.util.Log
import org.aladin.android.sdk.model.SessionData
import org.json.JSONObject

val ALADIN_SESSION = "aladinSession"

private val EMPTY_DATA = "{}"
private val TAG = SessionStore::class.java.simpleName


interface ISessionStore {
    var sessionData: SessionData
    fun deleteSessionData()
}

class SessionStore(private val prefs: SharedPreferences) : ISessionStore {
    private var sessionDataObject = SessionData(JSONObject(prefs.getString(ALADIN_SESSION, EMPTY_DATA)))
    override var sessionData: SessionData
        get() = sessionDataObject
        set(value) {
            Log.d(TAG, "set session data in store " + value.json.toString())
            sessionDataObject = value
            prefs.edit().putString(ALADIN_SESSION, value.json.toString()).apply()
        }

    override fun deleteSessionData() {
        prefs.edit().putString(ALADIN_SESSION, EMPTY_DATA).apply()
        sessionDataObject = SessionData(JSONObject())
    }

}