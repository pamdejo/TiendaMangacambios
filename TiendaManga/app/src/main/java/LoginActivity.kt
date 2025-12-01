package com.example.tiendamanga

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.*
import androidx.activity.ComponentActivity

class LoginActivity : ComponentActivity() {


    private val VALID_EMAIL = "Admin@mangastore.cl"
    private val VALID_PASS  = "Admin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("tiendamanga", MODE_PRIVATE)


        if (prefs.getBoolean("logged_in", false)) {
            startActivity(android.content.Intent(this, HomeActivity::class.java))
            finish()
            return
        }


        val bg = Color.parseColor("#0E0B12")
        val accent = Color.parseColor("#B97AFF")
        val textMain = Color.WHITE
        val textSoft = Color.parseColor("#BEBEBE")


        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(bg)
            val p = (32 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }


        val logo = ImageView(this).apply {
            setImageResource(R.drawable.logo)
            val size = (96 * resources.displayMetrics.density).toInt()
            layoutParams = LinearLayout.LayoutParams(size, size)
        }


        val title = TextView(this).apply {
            text = "Iniciar sesión"
            setTextColor(accent)
            textSize = 26f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        fun spacer(dp: Int) = Space(this).apply {
            minimumHeight = (dp * resources.displayMetrics.density).toInt()
        }


        val email = EditText(this).apply {
            hint = "Correo"
            setHintTextColor(textSoft)
            setTextColor(textMain)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setBackgroundColor(Color.parseColor("#1A1523"))
            setPadding(32, 24, 32, 24)
        }


        val passLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        val pass = EditText(this).apply {
            hint = "Contraseña"
            setHintTextColor(textSoft)
            setTextColor(textMain)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setBackgroundColor(Color.parseColor("#1A1523"))
            setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val toggle = Button(this).apply {
            text = "👁"
            setAllCaps(false)
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(accent)
            setOnClickListener {
                val isHidden = (pass.inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0
                pass.inputType = if (isHidden)
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                else
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                pass.setSelection(pass.text?.length ?: 0)
            }
        }
        passLayout.addView(pass)
        passLayout.addView(toggle)


        val remember = CheckBox(this).apply {
            text = "Recordarme"
            setTextColor(textSoft)
            isChecked = true
        }


        val error = TextView(this).apply {
            setTextColor(Color.parseColor("#FF6B6B"))
            textSize = 14f
            text = ""
        }


        val btn = Button(this).apply {
            text = "Entrar"
            setAllCaps(false)
            setTextColor(Color.WHITE)
            setBackgroundColor(accent)
            textSize = 18f
            setOnClickListener {
                val ok = validate(email.text.toString(), pass.text.toString())
                if (ok) {
                    if (remember.isChecked) {
                        prefs.edit().putBoolean("logged_in", true).apply()
                    }
                    startActivity(android.content.Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                } else {
                    error.text = "Usuario o contraseña incorrectos"
                    Toast.makeText(this@LoginActivity, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                }
            }
        }

        root.addView(logo)
        root.addView(spacer(12))
        root.addView(title)
        root.addView(spacer(24))
        root.addView(email)
        root.addView(spacer(12))
        root.addView(passLayout)
        root.addView(spacer(8))
        root.addView(remember)
        root.addView(spacer(8))
        root.addView(btn)
        root.addView(spacer(8))
        root.addView(error)

        setContentView(root)
    }

    private fun validate(email: String, pass: String): Boolean {
        val normalized = email.trim()
        return normalized.equals(VALID_EMAIL, ignoreCase = true) && pass == VALID_PASS
    }
}

