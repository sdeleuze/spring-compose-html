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
import org.jetbrains.compose.web.HydrationRoot
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.FormMethod
import org.jetbrains.compose.web.attributes.method
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.*

@Composable
fun CartDocument(cart: CartSummary) {
    ShopDocument("Your cart", hydrate = cart.items.isNotEmpty()) {
        CartPage(cart)
    }
}

@Composable
fun CartPage(cart: CartSummary) {
    Main(attrs = { classes(PageStyles.page) }) {
        Masthead(cart.count)

        Header(attrs = { classes(PageStyles.cartHero) }) {
            P(attrs = { classes(PageStyles.eyebrow) }) { Text("YOUR CART") }
            H1 { Text(if (cart.items.isEmpty()) "Nothing here yet." else "Almost yours.") }
        }

        Article(attrs = { classes(PageStyles.paper) }) {
            Header(attrs = { classes(PageStyles.paperHeader) }) {
                Span { Text(if (cart.count == 1) "1 MUG" else "${cart.count} MUGS") }
                Span { Text("FREE SHIPPING FROM TWO MUGS") }
            }

            if (cart.items.isEmpty()) {
                Section(attrs = { classes(PageStyles.emptyCart) }) {
                    P { Text("Your cart is empty.") }
                    A("/", attrs = { classes(PageStyles.linkButton) }) { Text("Choose your mug ↗") }
                }
            } else {
                Ul(attrs = { classes(PageStyles.cartItems) }) {
                    cart.items.forEach { CartLine(it) }
                }
                Section(attrs = { classes(PageStyles.cartSummary) }) {
                    DList(attrs = { classes(PageStyles.totals) }) {
                        Total("SUBTOTAL", "€${cart.subtotal}")
                        Total("SHIPPING", if (cart.shipping == 0) "Free" else "€${cart.shipping}")
                        Total("TOTAL", "€${cart.total}")
                    }
                    Div(attrs = { classes(PageStyles.controls) }) {
                        A("/", attrs = { classes(PageStyles.linkButton) }) { Text("Keep shopping ↗") }
                        Span { Text("SHIPS IN 3 DAYS") }
                    }
                }
            }

            Footer(attrs = { classes(PageStyles.paperFooter) }) {
                Span { Text("ATELIER TERRE / HANDMADE IN LYON") }
            }
        }
    }
}

@Composable
private fun CartLine(item: CartItem) {
    val order = item.order
    Li(attrs = { classes(PageStyles.cartItem) }) {
        Div(attrs = {
            classes(PageStyles.cartMug)
            style { color(Color(order.glaze.color)) }
        })
        Div(attrs = { classes(PageStyles.cartItemText) }) {
            P(attrs = { classes(PageStyles.eyebrow) }) { Text(order.glaze.label.uppercase() + " GLAZE") }
            H2 { Text("The morning mug") }
            P(attrs = { classes(PageStyles.cartEngraving) }) {
                Text(if (order.engraving.isBlank()) "No engraving" else "“${order.engraving}”")
            }
        }
        // One island per line, each with its own hydration ID
        HydrationRoot(
            CartLine(item.id, order.quantity),
            CartLine::serialize,
            rootAttrs = { classes(PageStyles.cartLineIsland) },
            hydrationId = IslandIds.cartLine(item.id),
        ) {
            CartLineIsland(it)
        }
        // Plain HTML form: removing a line needs no client-side state
        Form("/cart/remove", attrs = { method(FormMethod.Post) }) {
            HiddenInput { name(OrderFields.ID); value(item.id) }
            Button(attrs = {
                classes(PageStyles.linkButton)
                type(ButtonType.Submit)
                attr("aria-label", "Remove ${order.glaze.label} mug")
            }) { Text("Remove") }
        }
    }
}

@Composable
private fun Total(term: String, description: String) {
    Div {
        DTerm { Text(term) }
        DDescription { Text(description) }
    }
}
