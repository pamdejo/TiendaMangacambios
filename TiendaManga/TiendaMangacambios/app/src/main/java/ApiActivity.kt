package com.example.tiendamanga

import android.graphics.*
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ApiActivity : ComponentActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ProductsAdapter
    private var allItems: List<ProductDTO> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  Paleta MangaZone
        val bg = Color.parseColor("#0E0B12")
        val accent = Color.parseColor("#B97AFF")
        val textMain = Color.WHITE
        val textSoft = Color.parseColor("#BEBEBE")

        // raíz
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            val p = (16 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }

        val title = TextView(this).apply {
            text = "Productos"
            setTextColor(accent)
            textSize = 22f
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val search = EditText(this).apply {
            hint = "Buscar por nombre…"
            setHintTextColor(textSoft)
            setTextColor(textMain)
            setBackgroundColor(Color.parseColor("#1A1523"))
            setPadding(32, 24, 32, 24)
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val chkFeatured = CheckBox(this).apply {
            text = "Solo destacados"
            setTextColor(textSoft)
        }

        val lblCount = TextView(this).apply {
            setTextColor(textSoft)
            text = ""
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        listView = ListView(this).apply {
            divider = null
        }

        row.addView(chkFeatured)
        row.addView(lblCount)

        root.addView(title)
        root.addView(space(8))
        root.addView(search)
        root.addView(space(6))
        root.addView(row)
        root.addView(space(6))
        root.addView(listView)

        setContentView(root)

        // ----- Carga de datos desde assets/products.json -----
        val api = provideApi(this)
        lifecycleScope.launch {
            try {
                allItems = api.getProducts()
                adapter = ProductsAdapter(allItems.toMutableList())
                listView.adapter = adapter
                lblCount.text = "Total: ${allItems.size}"
            } catch (e: Exception) {
                Toast.makeText(this@ApiActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // ----- Filtros -----
        fun applyFilters() {
            val q = search.text.toString().trim().lowercase()
            val onlyFeatured = chkFeatured.isChecked
            val filtered = allItems.filter { p ->
                (!onlyFeatured || p.destacado) &&
                        p.nombre.lowercase().contains(q)
            }
            adapter.update(filtered)
            lblCount.text = "Mostrando: ${filtered.size}"
        }

        chkFeatured.setOnCheckedChangeListener { _, _ -> applyFilters() }
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { applyFilters() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Click en item
        listView.setOnItemClickListener { _, _, pos, _ ->
            val p = adapter.getItem(pos)!!
            Toast.makeText(this, "${p.nombre} — $${p.precio}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun space(dp: Int) = Space(this).apply {
        minimumHeight = (dp * resources.displayMetrics.density).toInt()
    }

    // --------- Adapter con fila personalizada ----------
    inner class ProductsAdapter(private var data: MutableList<ProductDTO>) :
        BaseAdapter() {

        fun update(newData: List<ProductDTO>) {
            data = newData.toMutableList()
            notifyDataSetChanged()
        }

        override fun getCount() = data.size
        override fun getItem(position: Int) = data[position]
        override fun getItemId(position: Int) = data[position].id.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val dpi = resources.displayMetrics.density
            val root = LinearLayout(this@ApiActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                val pad = (12 * dpi).toInt()
                setPadding(pad, pad, pad, pad)
                setBackgroundColor(Color.parseColor("#15101E"))
            }

            val img = ImageView(this@ApiActivity).apply {
                val size = (64 * dpi).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            val col = LinearLayout(this@ApiActivity).apply {
                orientation = LinearLayout.VERTICAL
                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                lp.setMargins((12 * dpi).toInt(), 0, 0, 0)
                layoutParams = lp
            }

            val name = TextView(this@ApiActivity).apply {
                setTextColor(Color.WHITE)
                textSize = 16f
            }
            val price = TextView(this@ApiActivity).apply {
                setTextColor(Color.parseColor("#B97AFF"))
                textSize = 14f
            }

            val badge = TextView(this@ApiActivity).apply {
                setTextColor(Color.BLACK)
                textSize = 12f
                setPadding((8 * dpi).toInt(), (4 * dpi).toInt(), (8 * dpi).toInt(), (4 * dpi).toInt())
                setBackgroundColor(Color.parseColor("#FFD54F"))
                visibility = View.GONE
            }

            col.addView(name)
            col.addView(price)
            root.addView(img)
            root.addView(col)
            root.addView(badge)

            // --- Bind ---
            val p = getItem(position)
            name.text = p.nombre
            price.text = "$ ${p.precio}"

            // imagen desde assets/img/xxx.webp
            img.setImageBitmap(loadBitmapFromAssets(p.imagen))

            if (p.destacado) {
                badge.text = "Destacado"
                badge.visibility = View.VISIBLE
            } else {
                badge.visibility = View.GONE
            }

            return root
        }
    }

    // Carga bitmaps desde assets (imagen: "img/archivo.webp")
    private fun loadBitmapFromAssets(path: String): Bitmap? =
        try {
            assets.open(path).use { input ->
                BitmapFactory.decodeStream(input)
            }
        } catch (_: Exception) { null }
}



