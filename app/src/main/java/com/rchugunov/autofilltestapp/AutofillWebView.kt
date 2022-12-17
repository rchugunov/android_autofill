package com.rchugunov.autofilltestapp

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.KeyCharacterMap
import android.view.LayoutInflater
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.rchugunov.autofilltestapp.databinding.LayoutAutofillBinding

class AutofillWebView @JvmOverloads constructor(
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
                fillCurrentInput(suggestion)
            }
            layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        }
    }

    private fun fillCurrentInput(suggestion: Suggestion) {
        val charMap = KeyCharacterMap.load(-1)
        val events = charMap.getEvents(suggestion.value.toCharArray())
        events.forEach {
            dispatchKeyEvent(it)
        }
    }

    private val softKeyboardObserver: (WindowInsetsCompat) -> Unit = { insets ->
        val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
        binding.inAppBrowserViewSuggestions.isVisible = isKeyboardVisible
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        ViewCompat.setOnApplyWindowInsetsListener((context as Activity).window.decorView) { view, insets ->
            softKeyboardObserver(insets)
            return@setOnApplyWindowInsetsListener ViewCompat.onApplyWindowInsets(view, insets)
        }
    }

    fun loadUrl(url: String) {
        binding.inAppBrowserViewWebView.loadUrl(url)
    }
}