package hr.trailovic.weatherqinfo.model

import android.content.SharedPreferences

private const val KEY_API_KEY = "1101"
private const val KEY_FIRST_START = "1102"

class SharedPreferencesHelper(private val sharedPreferences: SharedPreferences) {

    /* Api Key */
    fun readApiKey(): String{
        return sharedPreferences.getString(KEY_API_KEY, "") ?: ""
    }

    fun storeApiKey(apiKey: String){
        sharedPreferences.edit().apply {
            putString(KEY_API_KEY, apiKey)
            apply()
        }
    }

    /* Application first start */
    fun readFirstStart(): Boolean{
        return sharedPreferences.getBoolean(KEY_FIRST_START, true)
    }

    fun storeFirstStart(firstStart: Boolean){
        sharedPreferences.edit().apply {
            putBoolean(KEY_FIRST_START, false)
            apply()
        }
    }
}