package com.example.tiendamanga

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.Gravity
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : ComponentActivity() {

    // LOGIN LOCAL
    private val VALID_EMAIL = "admin@mangastore.cl"
    private val VALID_PASSWORD = "Admin123"

    // GOOGLE SIGN-IN
    private lateinit var googleSignInClient: GoogleSignInClient

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val email = account.email ?: ""
            val name  = account.displayName ?: ""
            onGoogleSignInSuccess(email, name)
        } catch (e: ApiException) {
            Toast.makeText(
                this,
                "Error al iniciar sesión con Google (código ${e.statusCode})",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("tiendamanga_login", MODE_PRIVATE)

        // Si ya está logeado (local o google) y marcó "recordarme", ir directo al Home
        val loggedIn = prefs.getBoolean("logged_in", false)
        val remember = prefs.getBoolean("remember_me", false)
        if (loggedIn && remember) {
            goToHome()
            return
        }

        // CONFIGURAR GOOGLE SIGN-IN
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val bg = Color.parseColor("#0E0B12")
        val accent = Color.parseColor("#B97AFF")
        val soft = Color.parseColor("#BEBEBE")

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(bg)
            val p = (32 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }

        fun space(dp: Int) = Space(this).apply {
            minimumHeight = (dp * resources.displayMetrics.density).toInt()
        }

        val logo = ImageView(this).apply {
            // asegúrate de tener res/drawable/logo.png
            setImageResource(R.drawable.logo)
            val size = (96 * resources.displayMetrics.density).toInt()
            layoutParams = LinearLayout.LayoutParams(size, size)
        }

        val title = TextView(this).apply {
            text = "MangaZone Admin"
            setTextColor(accent)
            textSize = 24f
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Inicia sesión para administrar productos y sucursales."
            setTextColor(soft)
            textSize = 14f
            gravity = Gravity.CENTER
        }

        // Campos login local
        val txtEmail = EditText(this).apply {
            hint = "Correo electrónico"
            setHintTextColor(soft)
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#1A1523"))
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        val txtPassword = EditText(this).apply {
            hint = "Contraseña"
            setHintTextColor(soft)
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#1A1523"))
            inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        val chkRemember = CheckBox(this).apply {
            text = "Recordarme"
            setTextColor(soft)
            isChecked = remember
        }

        val btnLoginLocal = Button(this).apply {
            text = "Iniciar sesión"
            setBackgroundColor(accent)
            setTextColor(Color.WHITE)
        }

        // Botón Google
        val btnLoginGoogle = Button(this).apply {
            text = "Continuar con Google"
            setBackgroundColor(Color.WHITE)
            setTextColor(Color.BLACK)
        }

        root.addView(logo)
        root.addView(space(12))
        root.addView(title)
        root.addView(space(8))
        root.addView(subtitle)
        root.addView(space(24))
        root.addView(txtEmail)
        root.addView(space(8))
        root.addView(txtPassword)
        root.addView(space(4))
        root.addView(chkRemember)
        root.addView(space(12))
        root.addView(btnLoginLocal)
        root.addView(space(16))
        root.addView(btnLoginGoogle)

        setContentView(root)

        // LÓGICA LOGIN LOCAL
        btnLoginLocal.setOnClickListener {
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo no tiene un formato válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email == VALID_EMAIL && password == VALID_PASSWORD) {
                prefs.edit()
                    .putBoolean("logged_in", true)
                    .putBoolean("remember_me", chkRemember.isChecked)
                    .putString("user_email", email)
                    .putString("login_type", "local")
                    .apply()

                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                goToHome()
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }

        // LÓGICA LOGIN GOOGLE
        btnLoginGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    // Lanza el intent de Google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    // Cuando Google responde OK
    private fun onGoogleSignInSuccess(email: String, name: String) {
        val prefs = getSharedPreferences("tiendamanga_login", MODE_PRIVATE)
        prefs.edit()
            .putBoolean("logged_in", true)
            .putBoolean("remember_me", true) // si entra por Google, lo recordamos
            .putString("user_email", email)
            .putString("user_name", name)
            .putString("login_type", "google")
            .apply()

        Toast.makeText(this, "Bienvenido $name", Toast.LENGTH_SHORT).show()
        goToHome()
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}


