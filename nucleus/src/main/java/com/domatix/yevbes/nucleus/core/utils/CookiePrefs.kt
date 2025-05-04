package com.domatix.yevbes.nucleus.core.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Cookie

class CookiePrefs(context: Context) : Prefs(CookiePrefs.TAG, context) {

    companion object {
        const val TAG = "CookiePrefs"
    }

    private val gson = Gson()
    private val type = object : TypeToken<ArrayList<ClonedCookie>>() {}.type

    // Usuario simulado para que compile (solo para pruebas)
    private val fakeUserName = "demoUser[demoDB]"

    fun getCookies(): ArrayList<Cookie> {
        val cookiesStr = getString(fakeUserName)
        if (cookiesStr.isNotEmpty()) {
            val clonedCookies: ArrayList<ClonedCookie> = gson.fromJson(cookiesStr, type)
            val cookies = arrayListOf<Cookie>()
            for (clonedCookie in clonedCookies) {
                cookies += clonedCookie.toCookie()
            }
            return cookies
        }
        return arrayListOf()
    }

    fun setCookies(cookies: ArrayList<Cookie>) {
        val clonedCookies = cookies.map { ClonedCookie.fromCookie(it) }
        val cookiesStr = gson.toJson(clonedCookies, type)
        putString(fakeUserName, cookiesStr)
    }
}
