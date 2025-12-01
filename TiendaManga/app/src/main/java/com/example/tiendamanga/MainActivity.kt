package com.example.tiendamanga

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.ComponentActivity
import com.example.tiendamanga.ProductJsBridge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val web = WebView(this)


        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.cacheMode = WebSettings.LOAD_DEFAULT
        web.webChromeClient = WebChromeClient()


        web.addJavascriptInterface(
            ProductJsBridge(this@MainActivity),
            "AndroidProduct"
        )


        web.loadUrl("file:///android_asset/index.html")

        setContentView(web)
    }
}