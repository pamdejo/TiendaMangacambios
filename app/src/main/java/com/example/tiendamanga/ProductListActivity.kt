package com.example.tiendamanga

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.example.tiendamanga.data.local.ProductEntity
import com.example.tiendamanga.data.local.RoomProductRepository
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductListActivity : ComponentActivity() {

    private lateinit var repository: RoomProductRepository
    private lateinit var listContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = RoomProductRepository(this)

        val bgColor = "#05040A".toColorInt()
        val accent = "#B97AFF".toColorInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            val p = (16 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }


        val title = TextView(this).apply {
            text = "Productos (SQLite)"
            setTextColor(accent)
            textSize = 22f
            gravity = Gravity.CENTER_HORIZONTAL
        }


        val btnRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val btnAddCamera = Button(this).apply {
            text = "Agregar con cámara"
            setBackgroundColor(accent)
            setTextColor(Color.WHITE)
            setOnClickListener {
                startActivity(
                    android.content.Intent(
                        this@ProductListActivity,
                        CameraActivity::class.java
                    )
                )
            }
        }

        val btnRefresh = Button(this).apply {
            text = "Refrescar"
            setBackgroundColor(accent)
            setTextColor(Color.WHITE)
            setOnClickListener { loadProducts() }
        }

        btnRow.addView(
            btnAddCamera,
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )
        btnRow.addView(
            btnRefresh,
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )


        listContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scroll = ScrollView(this).apply {
            addView(
                listContainer,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }

        root.addView(title)
        root.addView(Space(this).apply { minimumHeight = 16 })
        root.addView(btnRow)
        root.addView(Space(this).apply { minimumHeight = 16 })
        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)

        loadProducts()
    }


    private fun loadProducts() {
        lifecycleScope.launch {
            val products = repository.getAllProducts()
            listContainer.removeAllViews()

            if (products.isEmpty()) {
                val empty = TextView(this@ProductListActivity).apply {
                    text = "No hay productos registrados."
                    setTextColor(Color.LTGRAY)
                }
                listContainer.addView(empty)
                return@launch
            }

            products.forEach { product ->
                listContainer.addView(createProductRow(product))
            }
        }
    }

    private fun createProductRow(product: ProductEntity): LinearLayout {
        val accent = "#B97AFF".toColorInt()

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor("#141221".toColorInt())
            val p = (12 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = (12 * resources.displayMetrics.density).toInt()
            layoutParams = params
        }


        val imageView = ImageView(this).apply {
            val size = (64 * resources.displayMetrics.density).toInt()
            layoutParams = LinearLayout.LayoutParams(size, size)
            setBackgroundColor(Color.DKGRAY)
        }

        product.imageUrl?.let { url ->
            try {
                if (url.startsWith("content://") || url.startsWith("file://") || url.startsWith("http")) {
                    imageView.setImageURI(Uri.parse(url))
                }
            } catch (_: Exception) {
            }
        }


        val infoCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val lp = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            lp.leftMargin = (12 * resources.displayMetrics.density).toInt()
            layoutParams = lp
        }

        val nameView = TextView(this).apply {
            text = product.name
            setTextColor(Color.WHITE)
            textSize = 16f
        }

        val priceView = TextView(this).apply {
            text = "$${product.price.toInt()}"
            setTextColor(accent)
        }

        val categoryView = TextView(this).apply {
            text = "Género: ${product.category ?: "Sin género"}"
            setTextColor(Color.LTGRAY)
        }

        val stockView = TextView(this).apply {
            text = "Stock: ${product.stock}"
            setTextColor(Color.LTGRAY)
        }

        infoCol.addView(nameView)
        infoCol.addView(priceView)
        infoCol.addView(categoryView)
        infoCol.addView(stockView)


        val optionsButton = TextView(this).apply {
            text = "⋮"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            val size = (40 * resources.displayMetrics.density).toInt()
            layoutParams = LinearLayout.LayoutParams(size, size)
            setOnClickListener { showOptionsMenu(this, product) }
        }

        card.addView(imageView)
        card.addView(infoCol)
        card.addView(optionsButton)

        return card
    }

    private fun showOptionsMenu(anchor: TextView, product: ProductEntity) {
        val popup = android.widget.PopupMenu(this, anchor)
        popup.menu.add("Editar nombre")
        popup.menu.add("Editar precio")
        popup.menu.add("Editar stock")
        popup.menu.add("Editar género")
        popup.menu.add("Eliminar")

        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Editar nombre" -> showEditTextDialog(
                    title = "Editar nombre",
                    initial = product.name
                ) { value ->
                    val updated = product.copy(name = value)
                    saveUpdate(updated)
                }

                "Editar precio" -> showEditTextDialog(
                    title = "Editar precio",
                    initial = product.price.toInt().toString(),
                    inputType = android.text.InputType.TYPE_CLASS_NUMBER
                ) { value ->
                    val price = value.toDoubleOrNull() ?: return@showEditTextDialog
                    val updated = product.copy(price = price)
                    saveUpdate(updated)
                }

                "Editar stock" -> showEditTextDialog(
                    title = "Editar stock",
                    initial = product.stock.toString(),
                    inputType = android.text.InputType.TYPE_CLASS_NUMBER
                ) { value ->
                    val stock = value.toIntOrNull() ?: return@showEditTextDialog
                    val updated = product.copy(stock = stock)
                    saveUpdate(updated)
                }

                "Editar género" -> showEditTextDialog(
                    title = "Editar género",
                    initial = product.category ?: ""
                ) { value ->
                    val updated = product.copy(category = value.ifBlank { null })
                    saveUpdate(updated)
                }

                "Eliminar" -> confirmDelete(product)
            }
            true
        }

        popup.show()
    }


    private fun showEditTextDialog(
        title: String,
        initial: String,
        inputType: Int = android.text.InputType.TYPE_CLASS_TEXT,
        onValue: (String) -> Unit
    ) {
        val input = EditText(this).apply {
            setText(initial)
            this.inputType = inputType
        }

        android.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val value = input.text.toString().trim()
                if (value.isNotEmpty()) {
                    onValue(value)
                } else {
                    Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun saveUpdate(updated: ProductEntity) {
        lifecycleScope.launch {
            repository.updateProduct(updated)
            loadProducts()
        }
    }


    private fun confirmDelete(product: ProductEntity) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage("¿Eliminar \"${product.name}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    repository.deleteProduct(product)
                    loadProducts()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    private fun provideApi(): ApiService {
        val baseUrl = "http://10.0.2.2/tienda_api/" // ✅ Emulador (XAMPP en tu PC)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}