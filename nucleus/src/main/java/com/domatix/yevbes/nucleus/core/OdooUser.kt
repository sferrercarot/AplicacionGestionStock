package com.domatix.yevbes.nucleus.core

import android.accounts.Account
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.domatix.yevbes.nucleus.core.utils.Retrofit2Helper
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class OdooUser(
    val protocol: Retrofit2Helper.Protocol = Retrofit2Helper.Protocol.HTTP,
    val host: String = "",
    val login: String = "",
    val password: String = "",
    val database: String = "",
    val serverVersion: String = "",
    val isAdmin: Boolean = false,
    val id: Int = 0,
    val name: String = "",
    val imageSmall: String = "",
    val partnerId: Int = 0,
    val context: JsonObject = JsonObject(),
    val isActive: Boolean = false,
    val account: Account = Account("false", "com.dummy.account")
) {
    val androidName: String
        get() = "$login[$database]"

    val timezone: String
        get() = context["tz"].asString

    companion object {

        @JvmStatic
        fun getLetterTile(context: Context, name: String): Bitmap {
            val letter = name.take(1).uppercase()
            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.textSize = 30f
            canvas.drawText(letter, 20f, 50f, paint)
            return bitmap
        }

        private val retrofit = Retrofit.Builder()
            .baseUrl("https://your-odoo-api-url.com/") // Cambia esta URL por la real
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        private val apiService = retrofit.create(OdooApiService::class.java)

        interface OdooApiService {
            @GET("getUser")
            fun getUser(@Query("userId") userId: Int): Call<OdooUser>
        }

        fun fetchUserData(userId: Int) {
            val call = apiService.getUser(userId)

            call.enqueue(object : Callback<OdooUser> {
                override fun onResponse(call: Call<OdooUser>, response: Response<OdooUser>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        // haz algo con user
                    }
                }

                override fun onFailure(call: Call<OdooUser>, t: Throwable) {
                    // manejar error
                }
            })
        }
    }
}
