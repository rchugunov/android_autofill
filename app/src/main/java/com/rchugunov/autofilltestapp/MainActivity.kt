package com.rchugunov.autofilltestapp

import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.rchugunov.autofilltestapp.databinding.ActivityMainBinding
import io.cobrowse.CobrowseIO

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        CobrowseIO.instance().license("Av3013hRuDUy2g");
        CobrowseIO.instance().start(this);

        binding.webView.loadUrl("https://www.amazon.co.uk/")
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return true
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view!!.loadUrl(request!!.url.toString())
                return true
            }
        }
    }
}