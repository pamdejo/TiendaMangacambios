package com.example.tiendamanga

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProductsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bg = Color.parseColor("#0E0B12")
        val accent = Color.parseColor("#B97AFF")

        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            val pad = (20 * resources.displayMetrics.density).toInt()
            setPadding(pad, pad, pad, pad)
        }
        scroll.addView(root)

        val title = TextView(this).apply {
            text = "Productos registrados"
            setTextColor(accent)
            textSize = 26f
            gravity = Gravity.CENTER_HORIZONTAL
        }
        root.addView(title)
        root.addView(space(16))

        val listContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        root.addView(listContainer)

        setContentView(scroll)

        // 🔄 Cargar productos desde el repositorio (JSON + usuario)
        lifecycleScope.launch {
            try {
                val products = ProductRepository.getAllProducts(this@ProductsActivity)

                if (products.isEmpty()) {
                    val empty = TextView(this@ProductsActivity).apply {
                        text = "No hay productos"
                        setTextColor(Color.WHITE)
                        textSize = 18f
                        gravity = Gravity.CENTER
                    }
                    listContainer.addView(empty)
                    return@launch
                }

                products.forEach { p ->
                    val card = productCard(p)
                    listContainer.addView(card)
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@ProductsActivity,
                    "Error al cargar productos",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }

    private fun space(dp: Int) = TextView(this).apply {
        height = (dp * resources.displayMetrics.density).toInt()
    }

    private fun productCard(p: ProductDTO): LinearLayout {
        val accent = Color.parseColor("#B97AFF")
        val cardBg = Color.parseColor("#1A1522")

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 30, 30, 30)
            setBackgroundColor(cardBg)

            val name = TextView(context).apply {
                text = p.nombre
                textSize = 20f
                setTextColor(accent)
            }
            addView(name)

            val price = TextView(context).apply {
                text = "Precio: $${p.precio}"
                textSize = 18f
                setTextColor(Color.WHITE)
            }
            addView(price)

            val category = TextView(context).apply {
                text = "Categoría: ${p.categoria}"
                textSize = 16f
                setTextColor(Color.LTGRAY)
            }
            addView(category)

            val stock = TextView(context).apply {
                text = if (p.stock > 0) "Stock: disponible" else "Stock: agotado"
                textSize = 16f
                setTextColor(if (p.stock > 0) Color.GREEN else Color.RED)
            }
            addView(stock)

            val separator = TextView(context).apply {
                height = 20
            }
            addView(separator)
        }
    }
}