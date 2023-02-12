package com.example.native_autofill

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.autofill.AutofillManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.native_autofill.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.webview.settings.javaScriptEnabled = true
        binding.webview.loadUrl("https://stripe-payments-demo.appspot.com/")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.enableAutofillButton.setOnClickListener {
                startActivity(
                    Intent(
                        Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE,
                        Uri.parse("package:$packageName")
                    )
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val afm = getSystemService(AutofillManager::class.java)
            binding.enableAutofillButton.isVisible =
                !afm.hasEnabledAutofillServices() && afm.isAutofillSupported
            binding.enableAutofillButton.setOnClickListener {
                startActivity(
                    Intent(
                        Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE,
                        Uri.parse("package:$packageName")
                    )
                )
            }
        }
    }
}