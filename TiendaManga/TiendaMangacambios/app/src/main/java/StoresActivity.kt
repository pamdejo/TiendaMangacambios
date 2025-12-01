package com.example.tiendamanga

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.ComponentActivity

class StoresActivity : ComponentActivity() {

    private val tiendas = listOf(
        Store("MangaStore Centro",      "MangaStore Centro, Santiago, Chile", 8, 1200, "Lun–Sab 10:00–19:00"),
        Store("MangaStore Providencia", "MangaStore Providencia, Santiago",    6,  950, "Lun–Sab 10:00–19:30"),
        Store("MangaStore Maipú",       "MangaStore Maipú, Santiago",          5,  800, "Lun–Vie 10:30–18:30")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bg = Color.parseColor("#0E0B12")
        val accent = Color.parseColor("#B97AFF")
        val soft = Color.parseColor("#BEBEBE")

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            val p = (16 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }

        val title = TextView(this).apply {
            text = "Sucursales"
            setTextColor(accent)
            textSize = 22f
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val list = ListView(this).apply { divider = null }

        root.addView(title)
        root.addView(space(8))
        root.addView(list)
        setContentView(root)

        // Adapter con fila personalizada
        list.adapter = object : BaseAdapter() {
            override fun getCount() = tiendas.size
            override fun getItem(i: Int) = tiendas[i]
            override fun getItemId(i: Int) = i.toLong()

            override fun getView(i: Int, convert: View?, parent: ViewGroup?): View {
                val dpi = resources.displayMetrics.density
                val item = LinearLayout(this@StoresActivity).apply {
                    orientation = LinearLayout.VERTICAL
                    setBackgroundColor(Color.parseColor("#15101E"))
                    val pad = (12 * dpi).toInt()
                    setPadding(pad, pad, pad, pad)
                }
                val name = TextView(this@StoresActivity).apply {
                    setTextColor(Color.WHITE); textSize = 16f
                    text = getItem(i).nombre
                }
                val info = TextView(this@StoresActivity).apply {
                    setTextColor(soft); textSize = 13f
                    text = " ${getItem(i).trabajadores}  •  ${getItem(i).stock}  •  ${getItem(i).horario}"
                }
                item.addView(name); item.addView(info)
                return item
            }
        }

        // Click → abre detalle
        list.setOnItemClickListener { _, _, pos, _ ->
            val t = tiendas[pos]
            startActivity(Intent(this, StoreDetailActivity::class.java).apply {
                putExtra("nombre", t.nombre)
                putExtra("direccion", t.direccion)
                putExtra("trabajadores", t.trabajadores)
                putExtra("stock", t.stock)
                putExtra("horario", t.horario)
            })
        }
    }

    private fun space(dp: Int) = Space(this).apply {
        minimumHeight = (dp * resources.displayMetrics.density).toInt()
    }
}

data class Store(
    val nombre: String,
    val direccion: String,
    val trabajadores: Int,
    val stock: Int,
    val horario: String
)


