package com.example.tiendamanga

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.tiendamanga.com.example.tiendamanga.data.local.RoomProductRepository
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt

class ProductListActivity : ComponentActivity() {

    private lateinit var repository: RoomProductRepository

    // Lista en memoria para saber qué producto corresponde a cada posición del ListView
    private var currentProducts =
        emptyList<com.example.tiendamanga.com.example.tiendamanga.data.local.ProductEntity>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = RoomProductRepository(this)

        // === LAYOUT PRINCIPAL (LINEARLAYOUT) ===
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor("#0E0B12".toColorInt())
            val p = (30 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }

        // === TÍTULO ===
        val title = TextView(this).apply {
            text = "Products (SQLite)"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        // === BOTÓN AGREGAR ===
        val btnAdd = Button(this).apply {
            text = "Agregar producto demo"
            setBackgroundColor("#B97AFF".toColorInt())
            setTextColor(Color.WHITE)
        }

        // === LISTVIEW ===
        val listView = ListView(this)

        // --- función para recargar la lista ---
        fun cargarProductos() {
            lifecycleScope.launch {
                val products = repository.getAllProducts()
                currentProducts = products

                val adapter = ArrayAdapter(
                    this@ProductListActivity,
                    android.R.layout.simple_list_item_1,
                    products.map { "${it.id} — ${it.name} (${it.price})" }
                )

                listView.adapter = adapter
            }
        }

        // 👇 DELETE: eliminar producto con long-click
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val product = currentProducts.getOrNull(position)
                ?: return@setOnItemLongClickListener true

            android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Eliminar \"${product.name}\"?")
                .setPositiveButton("Eliminar") { _, _ ->
                    lifecycleScope.launch {
                        repository.deleteProduct(product)
                        cargarProductos()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()

            true
        }

        // 👇 UPDATE: editar stock con click normal
        listView.setOnItemClickListener { _, _, position, _ ->
            val product = currentProducts.getOrNull(position)
                ?: return@setOnItemClickListener

            val input = EditText(this).apply {
                hint = "Stock actual: ${product.stock}"
                setText(product.stock.toString())
                inputType = android.text.InputType.TYPE_CLASS_NUMBER
            }

            android.app.AlertDialog.Builder(this)
                .setTitle("Editar stock")
                .setView(input)
                .setPositiveButton("Guardar") { _, _ ->
                    val newStock = input.text.toString().toIntOrNull()
                    if (newStock != null) {
                        val updated = product.copy(stock = newStock)
                        lifecycleScope.launch {
                            repository.updateProduct(updated)
                            cargarProductos()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Stock inválido",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Cargar al entrar
        cargarProductos()

        // Acción del botón Agregar
        btnAdd.setOnClickListener {
            lifecycleScope.launch {
                repository.addProduct(
                    name = "Producto demo",
                    price = 19990.0,
                    stock = 5,
                    imageUrl = null
                )
                cargarProductos()
            }
        }

        // --- Agregar vistas al layout ---
        layout.addView(title)
        layout.addView(Space(this).apply { minimumHeight = 20 })
        layout.addView(btnAdd)
        layout.addView(Space(this).apply { minimumHeight = 20 })
        layout.addView(listView)

        setContentView(layout)
    }
}