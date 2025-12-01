package com.example.tiendamanga.com.example.tiendamanga

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.graphics.toColorInt

fun createBackButton(
    context: Context,
    onClick: () -> Unit
): Button {

    val density = context.resources.displayMetrics.density
    val size = (44 * density).toInt()      // tamaño del círculo
    val padding = (4 * density).toInt()

    val bg = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor("#B97AFF".toColorInt())   // morado
    }

    return Button(context).apply {
        text = "←"
        textSize = 20f
        setTextColor(Color.WHITE)
        background = bg
        setPadding(padding, padding, padding, padding)

        layoutParams = LinearLayout.LayoutParams(size, size).apply {
            gravity = Gravity.START
        }

        setOnClickListener { onClick() }
    }
}