package com.example.aplicaciongestionstockimprenta

import android.util.Base64
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.domatix.yevbes.nucleus.core.OdooUser

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("image_small", "name")
    fun loadImage(view: ImageView, imageSmall: String, name: String) {
        Glide.with(view.context)
            .asBitmap()
            .load(
                if (imageSmall.isNotEmpty()) {
                    Base64.decode(imageSmall, Base64.DEFAULT)
                } else {
                    OdooUser.getLetterTile(view.context, if (name.isNotEmpty()) name else "X")
                }
            )
            .into(view)
    }
}
