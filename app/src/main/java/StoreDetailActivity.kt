package com.example.tiendamanga

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.activity.ComponentActivity

class StoreDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bg = Color.parseColor("#0E0B12")
        val accent = Color.parseColor("#B97AFF")
        val soft = Color.parseColor("#BEBEBE")

        // Datos recibidos
        val nombre = intent.getStringExtra("nombre") ?: "Tienda"
        val direccion = intent.getStringExtra("direccion") ?: ""
        val trabajadores = intent.getIntExtra("trabajadores", 0)
        val stock = intent.getIntExtra("stock", 0)
        val horario = intent.getStringExtra("horario") ?: "-"

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            val p = (24 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }

        val title = TextView(this).apply {
            text = nombre
            setTextColor(accent)
            textSize = 24f
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val info = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 18f
            text = """
                 Trabajadores: $trabajadores
                 Stock total: $stock
                 Horario: $horario
                 Dirección: $direccion
            """.trimIndent()
        }

        val btnMaps = Button(this).apply {
            text = "Ver en Google Maps"
            setTextColor(Color.WHITE)
            setBackgroundColor(accent)
            setOnClickListener { openMaps(direccion) }
        }

        root.addView(title)
        root.addView(space(12))
        root.addView(info)
        root.addView(space(12))
        root.addView(btnMaps)

        setContentView(root)
    }

    private fun space(dp: Int) = Space(this).apply {
        minimumHeight = (dp * resources.displayMetrics.density).toInt()
    }

    private fun openMaps(query: String) {
        val geo = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
        val intent = Intent(Intent.ACTION_VIEW, geo).apply {
            setPackage("com.google.android.apps.maps")
        }
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")))
        }
    }
}

