/*
 * Copyright 2026-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package example.islands

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.FormMethod
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.maxLength
import org.jetbrains.compose.web.attributes.method
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

/** Hydration IDs shared by the server-rendered page and the browser entry point. */
object IslandIds {
    const val GLAZE = "island-glaze"
    const val QUANTITY = "island-quantity"
    const val ENGRAVING = "island-engraving"

    /** Prefix of the cart line islands, one per line of the cart page. */
    const val CART_LINE_PREFIX = "island-cart-line-"

    fun cartLine(id: Int) = "$CART_LINE_PREFIX$id"
}

const val MUG_PRICE = 32
const val MAX_QUANTITY = 6
const val MAX_ENGRAVING_LENGTH = 24

/** Names of the form fields carrying the state of the islands when adding the mug to the cart. */
object OrderFields {
    const val GLAZE = "glaze"
    const val QUANTITY = "quantity"
    const val ENGRAVING = "engraving"
    const val ID = "id"
}

/** Initial state of a cart line island: the cart line to update and its quantity. */
data class CartLine(val id: Int, val quantity: Int) {

    fun serialize(): String = "$id:$quantity"

    companion object {
        fun deserialize(state: String): CartLine {
            val (id, quantity) = state.split(':')
            return CartLine(id.toInt(), quantity.toInt())
        }
    }
}

@Composable
fun GlazeIsland(initialGlaze: Glaze) {
    var glaze by remember(initialGlaze) { mutableStateOf(initialGlaze) }

    IslandHeading("01", "Glaze", glaze.label)
    HiddenInput { name(OrderFields.GLAZE); value(glaze.name) }
    Div(attrs = { classes(PageStyles.islandBody) }) {
        Div(attrs = { classes(PageStyles.mugPreview) }) {
            Div(attrs = {
                classes(PageStyles.mug)
                style { color(Color(glaze.color)) }
            })
        }
        Div(attrs = { classes(PageStyles.swatches) }) {
            Glaze.entries.forEach { option ->
                Button(attrs = {
                    classes(PageStyles.swatch)
                    type(ButtonType.Button)
                    attr("aria-label", option.label)
                    attr("aria-pressed", (option == glaze).toString())
                    style { backgroundColor(Color(option.color)) }
                    onClick { glaze = option }
                })
            }
        }
    }
}

@Composable
fun QuantityIsland(initialQuantity: Int) {
    var quantity by remember(initialQuantity) { mutableStateOf(initialQuantity) }

    IslandHeading("02", "Quantity", "€${quantity * MUG_PRICE}")
    HiddenInput { name(OrderFields.QUANTITY); value(quantity) }
    Div(attrs = { classes(PageStyles.islandBody) }) {
        Div(attrs = { classes(PageStyles.stepper) }) {
            StepButton("−", "Remove one mug", enabled = quantity > 1) { quantity-- }
            Output(attrs = { classes(PageStyles.quantityValue) }) { Text("$quantity") }
            StepButton("+", "Add one mug", enabled = quantity < MAX_QUANTITY) { quantity++ }
        }
        Div(attrs = { classes(PageStyles.controls) }) {
            // Submits the enclosing server-rendered form, with the hidden inputs of all islands
            Button(attrs = {
                classes(PageStyles.linkButton)
                type(ButtonType.Submit)
            }) { Text("Add to cart ↗") }
            Span { Text("€$MUG_PRICE EACH") }
        }
    }
}

@Composable
fun EngravingIsland(initialEngraving: String) {
    var engraving by remember(initialEngraving) { mutableStateOf(initialEngraving) }
    var editing by remember { mutableStateOf(false) }

    IslandHeading("03", "Engraving", "${engraving.length} / $MAX_ENGRAVING_LENGTH")
    HiddenInput { name(OrderFields.ENGRAVING); value(engraving) }
    Div(attrs = { classes(PageStyles.islandBody) }) {
        P(attrs = { classes(PageStyles.engravingPreview) }) { Text(engraving.ifBlank { "No engraving" }) }
        Div(attrs = { classes(PageStyles.controls) }) {
            Button(attrs = {
                classes(PageStyles.linkButton)
                type(ButtonType.Button)
                onClick { editing = !editing }
            }) { Text(if (editing) "Done ↗" else "Edit engraving ↗") }
            Span { Text("HAND-CARVED UNDER THE HANDLE") }
        }
        if (editing) {
            Label(forId = "engraving-input", attrs = { classes(PageStyles.inputLabel) }) {
                Text("YOUR ENGRAVING")
            }
            TextInput(value = engraving) {
                id("engraving-input")
                classes(PageStyles.textInput)
                maxLength(MAX_ENGRAVING_LENGTH)
                onInput { engraving = it.value.take(MAX_ENGRAVING_LENGTH) }
            }
        }
    }
}

/**
 * Quantity of a cart line: the stepper updates the price right away, the "Update" button submits
 * the new quantity to the server, which recomputes the cart totals.
 */
@Composable
fun CartLineIsland(line: CartLine) {
    var quantity by remember(line) { mutableStateOf(line.quantity) }

    Form("/cart/update", attrs = { classes(PageStyles.cartLineForm); method(FormMethod.Post) }) {
        HiddenInput { name(OrderFields.ID); value(line.id) }
        HiddenInput { name(OrderFields.QUANTITY); value(quantity) }
        Div(attrs = { classes(PageStyles.cartStepper) }) {
            StepButton("−", "Remove one mug", enabled = quantity > 1) { quantity-- }
            Output(attrs = { classes(PageStyles.cartQuantity) }) { Text("$quantity") }
            StepButton("+", "Add one mug", enabled = quantity < MAX_QUANTITY) { quantity++ }
        }
        Output(attrs = { classes(PageStyles.cartPrice) }) { Text("€${quantity * MUG_PRICE}") }
        // Always rendered to keep the layout stable, hidden by the stylesheet while disabled
        Button(attrs = {
            classes(PageStyles.linkButton, PageStyles.updateButton)
            type(ButtonType.Submit)
            if (quantity == line.quantity) disabled()
        }) { Text("Update ↗") }
    }
}

@Composable
fun IslandHeading(number: String, title: String, value: String) {
    Div(attrs = { classes(PageStyles.islandHeading) }) {
        Div {
            P(attrs = { classes(PageStyles.eyebrow) }) { Text("STEP $number") }
            H2 { Text(title) }
        }
        Output(attrs = { classes(PageStyles.islandValue) }) { Text(value) }
    }
}

@Composable
private fun StepButton(label: String, description: String, enabled: Boolean, onClick: () -> Unit) {
    Button(attrs = {
        classes(PageStyles.stepButton)
        type(ButtonType.Button)
        attr("aria-label", description)
        if (!enabled) disabled()
        onClick { onClick() }
    }) { Text(label) }
}
