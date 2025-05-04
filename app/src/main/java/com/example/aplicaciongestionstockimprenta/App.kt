package com.example.aplicaciongestionstockimprenta

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class App : Application() {

    fun getLetterTile(name: String): Bitmap {
        val tileSize = 100
        val bmp = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        // Fondo gris claro
        val background = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, tileSize.toFloat(), tileSize.toFloat(), background)

        // Letra negra centrada
        val text = name.take(1).uppercase()
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 40f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(text, tileSize / 2f, tileSize / 2f + 15f, paint)

        return bmp
    }
}