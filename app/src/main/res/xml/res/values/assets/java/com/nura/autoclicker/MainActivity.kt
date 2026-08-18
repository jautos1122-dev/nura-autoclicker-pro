package com.nura.autoclicker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            databaseEnabled = true
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                val message = consoleMessage?.message() ?: return false
                if (message.startsWith("NATIVE_ACTION:")) {
                    val jsonStr = message.removePrefix("NATIVE_ACTION:")
                    handleNativeAction(jsonStr)
                    return true
                }
                return super.onConsoleMessage(consoleMessage)
            }
        }

        webView.loadUrl("file:///android_asset/ui.html")
        checkPermissions()
    }

    private fun handleNativeAction(jsonStr: String) {
        try {
            val json = JSONObject(jsonStr)
            val action = json.optString("action")
            when (action) {
                "SUPPORT" -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Majinnura"))
                    startActivity(intent)
                }
                "SHOW_TOAST" -> {
                    val msg = json.optString("data")
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
                "TOGGLE_START" -> {
                    checkPermissions()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        }
    }
}
