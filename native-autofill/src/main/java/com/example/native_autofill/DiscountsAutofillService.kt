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

@RequiresApi(Build.VERSION_CODES.O)
class DiscountsAutofillService : AutofillService() {

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal, callback: FillCallback) {
        // Get the structure from the request
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

//        // Traverse the structure looking for nodes to fill out
//        val parsedStructure: ParsedStructure =
        val autofillMap = mutableMapOf<Field, AutofillId>()
        parseStructure(structure, autofillMap)
//
//        // Fetch user data that matches the fields
//        val (username: String, password: String) = fetchUserData(parsedStructure)

        // Build the presentation of the datasets
        if (autofillMap.isNotEmpty()) {
            val usernamePresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
            usernamePresentation.setTextViewText(android.R.id.text1, "Ramsi Johnson")
            val emailPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
            emailPresentation.setTextViewText(android.R.id.text1, "ramsi.johnson@jmail.com")
//
            // Add a dataset to the response
            val fillResponse: FillResponse = FillResponse.Builder()
                .addDataset(
                    Dataset.Builder()
                        .setValue(
                            autofillMap[Field.NAME]!!,
                            AutofillValue.forText("Ramsi Johnson"),
                            usernamePresentation
                        )
                        .setValue(
                            autofillMap[Field.EMAIL]!!,
                            AutofillValue.forText("ramsi.johnson@jmail.com"),
                            emailPresentation
                        )
                        .build()
                )
                .addDataset(
                    Dataset.Builder()
                        .setValue(
                            autofillMap[Field.NAME]!!,
                            AutofillValue.forText("Jack Nickolson"),
                            usernamePresentation
                        )
                        .setValue(
                            autofillMap[Field.EMAIL]!!,
                            AutofillValue.forText("jacki123@jmail.com"),
                            emailPresentation
                        )
                        .build()
                )
                .build()

            // If there are no errors, call onSuccess() and pass the response
            callback.onSuccess(fillResponse)
        } else {
            callback.onSuccess(null)
        }
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
                if (attrName.equals("name", ignoreCase = true) && attrValue.equals("name", ignoreCase = true)) {
                    autofillMap[Field.NAME] = viewNode.autofillId!!
                }
                if (attrName.equals("name", ignoreCase = true) && attrValue.equals("email", ignoreCase = true)) {
                    autofillMap[Field.EMAIL] = viewNode.autofillId!!
                }
            }

        val children: List<ViewNode> =
            viewNode.run {
                (0 until childCount).map { getChildAt(it) }
            }

        children.forEach { childNode: ViewNode ->
            traverseNode(childNode, autofillMap)
        }
    }

    data class ParsedStructure(var usernameId: AutofillId, var passwordId: AutofillId)

    data class UserData(var username: String, var password: String)

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {

    }

    enum class Field {
        NAME,
        EMAIL
    }
}