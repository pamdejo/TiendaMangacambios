package com.example.tiendamanga

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.ComponentActivity
import com.example.tiendamanga.com.example.tiendamanga.ProductJsBridge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val web = WebView(this)

        // Configuración del WebView
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.cacheMode = WebSettings.LOAD_DEFAULT
        web.webChromeClient = WebChromeClient()

        // Puente JS Android (Room / Repository)
        web.addJavascriptInterface(
            ProductJsBridge(this@MainActivity),
            "AndroidProduct"   // ← así se llamará en JavaScript
        )

        // Cargar tu página
        web.loadUrl("file:///android_asset/index.html")

        setContentView(web)
    }
}