package com.domatix.yevbes.nucleus.core.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import android.text.TextPaint
import java.io.ByteArrayOutputStream

class LetterTileProvider(context: Context) {

    private val paint = TextPaint().apply {
        typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        textSize = 48f // tamaño fijo para letras
    }

    private val canvas = Canvas()
    private val bounds = Rect()

    private val defaultSize = 100
    private val defaultBitmap = drawableToBitmap(context, android.R.drawable.sym_def_app_icon)

    fun getLetterTile(displayName: String): ByteArray {
        val width = defaultSize
        val height = defaultSize
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        canvas.setBitmap(bitmap)
        canvas.drawColor(pickColor(displayName))

        val letter = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

        paint.getTextBounds(letter, 0, 1, bounds)
        val x = width / 2f
        val y = height / 2f + (bounds.height() / 2f)

        canvas.drawText(letter, x, y, paint)

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    private fun drawableToBitmap(context: Context, @DrawableRes id: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, id)
            ?: throw IllegalStateException("Drawable can't be null")
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.RGB_565
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    @ColorInt
    private fun pickColor(key: String): Int {
        val colors = listOf(
            Color.RED, Color.BLUE, Color.GREEN, Color.CYAN,
            Color.MAGENTA, Color.DKGRAY, Color.GRAY, Color.YELLOW,
            Color.LTGRAY, Color.BLACK
        )
        return colors[Math.abs(key.hashCode()) % colors.size]
    }
}
