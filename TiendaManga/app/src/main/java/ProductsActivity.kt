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

class ProductsActivity : ComponentActivity() {

    private lateinit var repository: RoomProductRepository
    private lateinit var listContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = RoomProductRepository(this)

        val bg = "#05040A".toColorInt()
        val accent = "#B97AFF".toColorInt()


        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            val p = (16 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }


        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val title = TextView(this).apply {
            text = "Productos (Panel nativo)"
            setTextColor(Color.WHITE)
            textSize = 18f
        }

        val btnRefresh = Button(this).apply {
            text = "Refrescar"
            setBackgroundColor(accent)
            setTextColor(Color.WHITE)
            setOnClickListener { loadProducts() }
        }

        headerRow.addView(
            title,
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )
        headerRow.addView(btnRefresh)


        val btnRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val btnAddManual = Button(this).apply {
            text = "Agregar manual"
            setBackgroundColor(accent)
            setTextColor(Color.WHITE)
            setOnClickListener { showCreateDialog() }
        }

        val btnAddCamera = Button(this).apply {
            text = "Agregar con cámara"
            setBackgroundColor(accent)
            setTextColor(Color.WHITE)
            setOnClickListener {
                startActivity(
                    android.content.Intent(
                        this@ProductsActivity,
                        CameraActivity::class.java
                    )
                )
            }
        }

        btnRow.addView(
            btnAddManual,
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )
        btnRow.addView(
            btnAddCamera,
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


        root.addView(headerRow)
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
                val empty = TextView(this@ProductsActivity).apply {
                    text = "No hay productos en la base de datos."
                    setTextColor(Color.LTGRAY)
                }
                listContainer.addView(empty)
                return@launch
            }

            products.forEach { product ->
                listContainer.addView(createProductCard(product))
            }
        }
    }

    private fun createProductCard(product: ProductEntity): LinearLayout {
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

            product.imageUrl?.let { url ->
                try {
                    if (url.startsWith("content://") || url.startsWith("file://")) {
                        setImageURI(Uri.parse(url))
                    }
                } catch (_: Exception) {

                }
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

        val stockView = TextView(this).apply {
            text = "Stock: ${product.stock}"
            setTextColor(Color.LTGRAY)
        }


        val btnRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
        }

        val btnEdit = Button(this).apply {
            text = "Editar"
            setBackgroundColor(accent)
            setTextColor(Color.WHITE)
            setOnClickListener { showEditDialog(product) }
        }

        val btnDelete = Button(this).apply {
            text = "Eliminar"
            setBackgroundColor(Color.RED)
            setTextColor(Color.WHITE)
            setOnClickListener { confirmDelete(product) }
        }

        btnRow.addView(btnEdit)
        btnRow.addView(btnDelete)

        infoCol.addView(nameView)
        infoCol.addView(priceView)
        infoCol.addView(stockView)
        infoCol.addView(btnRow)

        card.addView(imageView)
        card.addView(infoCol)

        return card
    }


    private fun showCreateDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val p = (16 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }

        val nameInput = EditText(this).apply { hint = "Nombre" }
        val priceInput = EditText(this).apply {
            hint = "Precio"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val stockInput = EditText(this).apply {
            hint = "Stock"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(nameInput)
        layout.addView(priceInput)
        layout.addView(stockInput)

        android.app.AlertDialog.Builder(this)
            .setTitle("Nuevo producto")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val name = nameInput.text.toString().trim()
                val price = priceInput.text.toString().toDoubleOrNull() ?: 0.0
                val stock = stockInput.text.toString().toIntOrNull() ?: 0

                if (name.isEmpty() || price <= 0.0) {
                    Toast.makeText(
                        this,
                        "Nombre y precio deben ser válidos",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    repository.addProduct(
                        name = name,
                        price = price,
                        stock = stock,
                        imageUrl = null
                    )
                    loadProducts()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    private fun showEditDialog(product: ProductEntity) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val p = (16 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }

        val nameInput = EditText(this).apply {
            hint = "Nombre"
            setText(product.name)
        }
        val priceInput = EditText(this).apply {
            hint = "Precio"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(product.price.toInt().toString())
        }
        val stockInput = EditText(this).apply {
            hint = "Stock"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(product.stock.toString())
        }

        layout.addView(nameInput)
        layout.addView(priceInput)
        layout.addView(stockInput)

        android.app.AlertDialog.Builder(this)
            .setTitle("Editar producto")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val name = nameInput.text.toString().trim()
                val price = priceInput.text.toString().toDoubleOrNull() ?: 0.0
                val stock = stockInput.text.toString().toIntOrNull() ?: 0

                if (name.isEmpty() || price <= 0.0) {
                    Toast.makeText(
                        this,
                        "Nombre y precio deben ser válidos",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setPositiveButton
                }

                val updated = product.copy(
                    name = name,
                    price = price,
                    stock = stock
                )

                lifecycleScope.launch {
                    repository.updateProduct(updated)
                    loadProducts()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
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
}