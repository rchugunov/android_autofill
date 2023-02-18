package com.rchugunov.autofilljs

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.rchugunov.autofilljs.databinding.LayoutAutofillBinding

class AutofillJSWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultAttr: Int = 0
) : FrameLayout(context, attrs, defaultAttr) {

    private val binding: LayoutAutofillBinding by lazy {
        LayoutAutofillBinding.inflate(LayoutInflater.from(context))
    }

    init {
        addView(binding.root)

        binding.inAppBrowserViewWebView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view!!.loadUrl(request!!.url.toString())
                return true
            }
        }


        binding.inAppBrowserViewSuggestions.apply {
            adapter = SuggestionsAdapter { suggestion ->

            }
        }
    }

    fun loadUrl(url: String) {
        binding.inAppBrowserViewWebView.loadUrl(url)
    }
}