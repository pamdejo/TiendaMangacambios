package com.example.tiendamanga

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.activity.ComponentActivity
import com.example.tiendamanga.ProductListActivity

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val bgColor = Color.parseColor("#0E0B12")
        val accent = Color.parseColor("#B97AFF")
        val textMain = Color.WHITE

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(bgColor)
            val p = (40 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }


        val logo = ImageView(this).apply {
            setImageResource(R.drawable.logo)
            val size = (100 * resources.displayMetrics.density).toInt()
            layoutParams = LinearLayout.LayoutParams(size, size)
        }


        val title = TextView(this).apply {
            text = "MangaZone Admin"
            textSize = 26f
            setTextColor(accent)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }


        fun createButton(texto: String, icon: String = ""): Button {
            val btn = Button(this)
            btn.text = "$icon $texto"
            btn.setTextColor(textMain)
            btn.textSize = 18f
            btn.setBackgroundColor(accent)
            btn.setAllCaps(false)
            btn.background = resources.getDrawable(
                android.R.drawable.btn_default,
                theme
            ).apply {
                btn.setBackgroundColor(accent)
            }
            btn.setPadding(20, 20, 20, 20)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 16)
            }
            btn.layoutParams = params
            return btn
        }


        val btnTienda = createButton("Ver tienda Web")
        val btnCamara = createButton("Abrir cámara")
        val btnApi = createButton("Ver productos")              // 👉 ahora abre SQLite
        val btnTiendas = createButton("Ver tiendas")
        val btnLogout = createButton("Cerrar sesión")
        val btnSqlite = createButton("Ver productos SQLite")    // 👉 también SQLite


        layout.addView(logo)
        layout.addView(Space(this).apply { minimumHeight = 20 })
        layout.addView(title)
        layout.addView(Space(this).apply { minimumHeight = 40 })
        layout.addView(btnTienda)
        layout.addView(btnCamara)
        layout.addView(btnApi)
        layout.addView(btnTiendas)
        layout.addView(btnLogout)
        layout.addView(btnSqlite)

        setContentView(layout)


        btnTienda.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnCamara.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }


        btnApi.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        btnTiendas.setOnClickListener {
            startActivity(Intent(this, StoresActivity::class.java))
        }


        btnSqlite.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        btnLogout.setOnClickListener {
            val prefs = getSharedPreferences("tiendamanga_login", MODE_PRIVATE)
            prefs.edit().clear().apply()

            // (Completas la config de Google)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}