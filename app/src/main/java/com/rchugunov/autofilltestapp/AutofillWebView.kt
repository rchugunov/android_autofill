package com.rchugunov.autofilltestapp

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.FrameLayout

class AutofillWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultAttr: Int = 0
) : FrameLayout(context, attrs, defaultAttr) {

    private val softKeyboardObserver: (WindowInsets) -> Unit = { newInsets ->
        val insetVerticalChanges = newInsets.systemWindowInsetBottom - newInsets.stableInsetBottom
        val imeVisible = insetVerticalChanges > 0
        suggestionsBarView.isVisible = imeVisible
        bottomBarView.isVisible = !imeVisible
    }

}