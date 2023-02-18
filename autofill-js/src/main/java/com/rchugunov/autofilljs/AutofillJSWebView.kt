package com.rchugunov.autofilljs

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rchugunov.autofilljs.databinding.LayoutAutofillBinding
import java.io.InputStreamReader


@SuppressLint("SetJavaScriptEnabled")
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
        val js = readJSFromAsset(FOCUSED_INPUT_JS_FILENAME)

        binding.inAppBrowserViewWebView.apply {
            settings.javaScriptEnabled = true

            addJavascriptInterface(JSInterface(), "Android")

            webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    view!!.loadUrl(request!!.url.toString())
                    return true
                }

                override fun onPageFinished(view: WebView, url: String?) {
                    super.onPageFinished(view, url)
                    view.evaluateJavascript(js, null)
                }
            }
        }


        binding.inAppBrowserViewSuggestions.apply {
            adapter = SuggestionsAdapter { suggestion ->
                sendSuggestionToFocusedField(suggestion)
            }
        }
    }

    private fun readJSFromAsset(fileName: String): String {
        return InputStreamReader(context.assets.open(fileName)).buffered().use { it.readText() }
    }

    private fun sendSuggestionToFocusedField(suggestion: Suggestion) {
        val js = readJSFromAsset(UPDATE_TEXT_JS_FILENAME)
            .replace(SUGGESTION_PLACEHOLDER, suggestion.value)
        binding.inAppBrowserViewWebView.evaluateJavascript(js, null)
    }

    fun loadUrl(url: String) {
        binding.inAppBrowserViewWebView.loadUrl(url)
    }

    inner class JSInterface {
        @JavascriptInterface
        fun handleInputAttrs(attrsMapJson: String) {
            post {
                processFocusedInput(attrsMapJson)
            }
        }
    }

    private fun processFocusedInput(attrsMapJson: String) {
        val attrsMap = Gson().fromJson<Map<String, String>>(attrsMapJson, object : TypeToken<Map<String, String>>() {}.type)
        when {
            attrsMap.containsKey("name") && attrsMap["name"]!!.equals("coupon", ignoreCase = true) ||
                    attrsMap.containsKey("label") && attrsMap["label"]!!.equals("coupon", ignoreCase = true) -> {
                showPromocodesSuggestions()
            }
            else -> {
                hidePromocodesSuggestions()
            }
        }
    }

    private fun showPromocodesSuggestions() {
        binding.inAppBrowserViewSuggestions.isVisible = true
    }

    private fun hidePromocodesSuggestions() {
        binding.inAppBrowserViewSuggestions.isVisible = false
    }

    companion object {
        private const val SUGGESTION_PLACEHOLDER = "<AUTOFILL_TEXT>"
        private const val UPDATE_TEXT_JS_FILENAME = "set_text_to_focused_input.js"
        private const val FOCUSED_INPUT_JS_FILENAME = "focused_input_attrs.js"
    }
}