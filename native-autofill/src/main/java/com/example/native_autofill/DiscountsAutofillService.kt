package com.example.native_autofill

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.*
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.annotation.RequiresApi

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.O)
class DiscountsAutofillService : AutofillService() {

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {

        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        val autofillStructure = parseStructure(structure)

        if (autofillStructure.autofillMap.containsKey(Field.COUPON)) {

            val promocodes = getPromocodes(autofillStructure.url)

            val fillResponse: FillResponse = FillResponse.Builder()
                .apply {
                    promocodes.forEach { promocode ->
                        val promocodePresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
                            setTextViewText(android.R.id.text1, "Promocode $promocode")
                        }
                        addDataset(
                            Dataset.Builder()
                                .setValue(
                                    autofillStructure.autofillMap[Field.COUPON]!!,
                                    AutofillValue.forText(promocode),
                                    promocodePresentation
                                )
                                .build()
                        )
                    }
                }
                .build()

            // If there are no errors, call onSuccess() and pass the response
            callback.onSuccess(fillResponse)
        } else {
            callback.onSuccess(null)
        }
    }

    private fun getPromocodes(url: String?): List<String> {
        return if (url?.contains("starfish") == true) { // тут может быть логика вашего приложения
            listOf("NEWYEAR23", "VALENTINE14", "1PURCHASE")
        } else {
            emptyList()
        }
    }

    private fun parseStructure(structure: AssistStructure): ResultStructure {

        val resultStructure = ResultStructure()

        val windowNodes: List<AssistStructure.WindowNode> =
            structure.run {
                (0 until windowNodeCount).map { getWindowNodeAt(it) }
            }

        windowNodes.forEach { windowNode: AssistStructure.WindowNode ->
            val viewNode: ViewNode? = windowNode.rootViewNode
            viewNode?.let { traverseNode(it, resultStructure) }
        }
        return resultStructure
    }

    private fun traverseNode(viewNode: ViewNode, resultStructure: ResultStructure) {

        if (viewNode.className?.contains("android.webkit.WebView") == true) {
            resultStructure.url = viewNode.webDomain
        }

        viewNode.htmlInfo?.attributes
            ?.map { pair -> pair.first to pair.second }
            ?.forEach { (attrName, attrValue) ->
                if (attrName.equals("name", ignoreCase = true) && attrValue.equals("promocode", ignoreCase = true) ||
                    attrName.equals("label", ignoreCase = true) && attrValue.equals("promocode", ignoreCase = true)
                ) {
                    resultStructure.autofillMap[Field.COUPON] = viewNode.autofillId!!
                }
            }

        val children: List<ViewNode> = viewNode.run {
            (0 until childCount).map { getChildAt(it) }
        }

        children.forEach { childNode: ViewNode ->
            traverseNode(childNode, resultStructure)
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {

    }

    data class ResultStructure(
        var url: String? = null,
        val autofillMap: MutableMap<Field, AutofillId> = mutableMapOf()
    )

    enum class Field {
        COUPON,
    }
}