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
class PromocodesAutofillService : AutofillService() {

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {

        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        val autofillMap = mutableMapOf<Field, AutofillId>()
        parseStructure(structure, autofillMap)

        if (autofillMap.containsKey(Field.PROMOCODE)) {

            val promocodes = getPromocodes()

            val fillResponse: FillResponse = FillResponse.Builder()
                .apply {
                    val promocodePresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                    promocodes.forEach { promocode ->
                        promocodePresentation.setTextViewText(android.R.id.text1, "Promocode $promocode")
                        addDataset(
                            Dataset.Builder()
                                .setValue(
                                    autofillMap[Field.PROMOCODE]!!,
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

    private fun getPromocodes(): List<String> {
        TODO("Not yet implemented")
    }

    private fun parseStructure(
        structure: AssistStructure,
        autofillMap: MutableMap<Field, AutofillId>
    ) {
        val windowNodes: List<AssistStructure.WindowNode> =
            structure.run {
                (0 until windowNodeCount).map { getWindowNodeAt(it) }
            }

        windowNodes.forEach { windowNode: AssistStructure.WindowNode ->
            val viewNode: ViewNode? = windowNode.rootViewNode
            viewNode?.let { traverseNode(it, autofillMap) }
        }
    }

    private fun traverseNode(viewNode: ViewNode, autofillMap: MutableMap<Field, AutofillId>) {
        viewNode.htmlInfo?.attributes
            ?.map { pair -> pair.first to pair.second }
            ?.forEach { (attrName, attrValue) ->
                if (attrName.equals("name", ignoreCase = true) && attrValue.equals("promocode", ignoreCase = true) ||
                    attrName.equals("label", ignoreCase = true) && attrValue.equals("promocode", ignoreCase = true)
                ) {
                    autofillMap[Field.PROMOCODE] = viewNode.autofillId!!
                }
            }

        val children: List<ViewNode> = viewNode.run {
            (0 until childCount).map { getChildAt(it) }
        }

        children.forEach { childNode: ViewNode ->
            traverseNode(childNode, autofillMap)
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {

    }

    enum class Field {
        PROMOCODE,
//        NAME,
//        EMAIL
    }
}