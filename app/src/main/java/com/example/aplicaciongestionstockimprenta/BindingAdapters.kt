package com.example.aplicaciongestionstockimprenta

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("defaultIcon")
    fun loadDefaultImage(view: ImageView, iconResId: Int) {
        Glide.with(view.context)
            .load(iconResId)
            .into(view)
    }
}
