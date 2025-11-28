package com.example.tiendamanga

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class CameraActivity : ComponentActivity() {

    private var pendingUri: Uri? = null

    // 🧩 contenedor donde pondremos el formulario después de tomar la foto
    private lateinit var formContainer: LinearLayout

    // Lanzadores
    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grantResults ->
        val granted = grantResults.values.all { it == true }
        if (granted) {
            Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
            capturePhoto()
        } else {
            Toast.makeText(this, "Permisos denegados", Toast.LENGTH_LONG).show()
        }
    }

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingUri
        if (success && uri != null) {
            Toast.makeText(this, "Foto guardada en la galería", Toast.LENGTH_SHORT).show()
            // 👇 mostramos el formulario para registrar el producto
            showProductForm(uri)
        } else {
            Toast.makeText(this, "Captura cancelada o fallida", Toast.LENGTH_SHORT).show()
            // Limpia el URI si no se usó
            uri?.let { contentResolver.delete(it, null, null) }
        }
        pendingUri = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bg = Color.parseColor("#0E0B12")
        val accent = Color.parseColor("#B97AFF")

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            val p = (24 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }

        val title = TextView(this).apply {
            text = "Registrar producto con cámara"
            setTextColor(accent)
            textSize = 20f
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val btn = Button(this).apply {
            text = "Abrir cámara"
            setBackgroundColor(accent)
            setTextColor(Color.WHITE)
            setOnClickListener { ensureAndOpenCamera() }
        }

        // Contenedor vacío donde luego pondremos el formulario
        formContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(title)
        root.addView(Space(this).apply { minimumHeight = 24 })
        root.addView(btn)
        root.addView(Space(this).apply { minimumHeight = 24 })
        root.addView(formContainer)

        setContentView(root)
    }

    private fun ensureAndOpenCamera() {
        // 1) ¿tiene cámara?
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(this, "Este dispositivo no tiene cámara disponible", Toast.LENGTH_LONG).show()
            return
        }

        // 2) Permisos en runtime si hacen falta
        val needsWrite = Build.VERSION.SDK_INT <= Build.VERSION_CODES.P // <= Android 9
        val needed = mutableListOf(Manifest.permission.CAMERA)
        if (needsWrite) needed += Manifest.permission.WRITE_EXTERNAL_STORAGE

        val missing = needed.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            requestPermissions.launch(missing.toTypedArray())
            return
        }

        // 3) Capturar
        capturePhoto()
    }

    private fun capturePhoto() {
        val name = "producto_${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TiendaManga")
            }
        }
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            else
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val uri = contentResolver.insert(collection, values)
        if (uri == null) {
            Toast.makeText(this, "No pude crear el archivo en la galería", Toast.LENGTH_LONG).show()
            // Fallback: intenta abrir la cámara sin URI (devuelve thumbnail)
            openBasicCameraFallback()
            return
        }
        pendingUri = uri
        takePicture.launch(uri)
    }

    private fun openBasicCameraFallback() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) == null) {
            Toast.makeText(this, "No hay app de cámara disponible", Toast.LENGTH_LONG).show()
            return
        }
        startActivity(intent) // solo para comprobar que la cámara abre
    }

    // --------------------------------------------------------------------
    // 📦 FORMULARIO PARA REGISTRAR PRODUCTO LUEGO DE TOMAR LA FOTO
    // --------------------------------------------------------------------
    private fun showProductForm(photoUri: Uri) {
        formContainer.removeAllViews() // limpiamos si ya había algo

        val accent = Color.parseColor("#B97AFF")

        val title = TextView(this).apply {
            text = "Nuevo producto"
            setTextColor(accent)
            textSize = 18f
        }

        val nombreInput = EditText(this).apply {
            hint = "Nombre del manga"
            setHintTextColor(Color.LTGRAY)
            setTextColor(Color.WHITE)
        }

        val precioInput = EditText(this).apply {
            hint = "Precio (ej: 8990)"
            setHintTextColor(Color.LTGRAY)
            setTextColor(Color.WHITE)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        val categoriaInput = EditText(this).apply {
            hint = "Categoría (Shonen, Seinen...)"
            setHintTextColor(Color.LTGRAY)
            setTextColor(Color.WHITE)
        }

        val stockInput = EditText(this).apply {
            hint = "Stock (ej: 5)"
            setHintTextColor(Color.LTGRAY)
            setTextColor(Color.WHITE)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        val btnGuardar = Button(this).apply {
            text = "Guardar producto"
            setBackgroundColor(accent)
            setTextColor(Color.WHITE)
            setOnClickListener {
                val nombre = nombreInput.text.toString().trim()
                val precio = precioInput.text.toString().trim().toIntOrNull() ?: 0
                val categoriaTexto = categoriaInput.text.toString().trim()   // 👈 siempre String
                val stock = stockInput.text.toString().trim().toIntOrNull() ?: 1

                if (nombre.isEmpty() || precio <= 0) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Completa al menos nombre y precio válidos",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                // 🧩 Creamos el ProductDTO usando la URI de la foto
                val nuevoProducto = ProductDTO(
                    id = 0, // el repositorio le pone uno único
                    nombre = nombre,
                    precio = precio,
                    imagen = photoUri.toString(), // 🔹 guardamos la URI como string
                    stock = stock,
                    destacado = false,
                    categoria = categoriaTexto   // 👈 ahora es String, no String?
                )

                // Guardamos en SharedPreferences a través del repositorio
                ProductRepository.addUserProduct(this@CameraActivity, nuevoProducto)

                Toast.makeText(
                    this@CameraActivity,
                    "Producto registrado correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                nombreInput.text.clear()
                precioInput.text.clear()
                categoriaInput.text.clear()
                stockInput.text.clear()
            }
        }

        val margin = (12 * resources.displayMetrics.density).toInt()

        fun addWithMargin(view: android.view.View) {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = margin
            formContainer.addView(view, params)
        }

        addWithMargin(title)
        addWithMargin(nombreInput)
        addWithMargin(precioInput)
        addWithMargin(categoriaInput)
        addWithMargin(stockInput)
        addWithMargin(btnGuardar)
    }
}