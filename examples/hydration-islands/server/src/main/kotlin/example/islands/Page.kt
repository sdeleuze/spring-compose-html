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
import org.jetbrains.compose.web.attributes.FormMethod
import org.jetbrains.compose.web.attributes.ScriptType
import org.jetbrains.compose.web.attributes.method
import org.jetbrains.compose.web.attributes.src
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.*

/** Complete document shared by the pages, loading the browser bundle only when the page has islands. */
@Composable
fun ShopDocument(title: String, hydrate: Boolean = false, content: @Composable () -> Unit) {
    Html(attrs = { lang("en") }) {
        Head {
            Meta(attrs = { attr("charset", "utf-8") })
            Meta(attrs = {
                attr("name", "viewport")
                attr("content", "width=device-width, initial-scale=1")
            })
            Title { Text("$title — Atelier Terre") }
            Style(PageStyles)
        }
        Body {
            content()
            if (hydrate) {
                Script(attrs = { type(ScriptType.Module); src("/app.js") })
            }
        }
    }
}

@Composable
fun ProductDocument(order: MugOrder, cartCount: Int) {
    ShopDocument("The Morning Mug", hydrate = true) {
        ProductPage(order, cartCount)
    }
}

@Composable
fun Masthead(cartCount: Int) {
    Div(attrs = { classes(PageStyles.masthead) }) {
        A("/", attrs = { classes(PageStyles.brand) }) {
            Span { Text("ATELIER TERRE") }
        }
        Nav(attrs = { classes(PageStyles.mastheadNav) }) {
            Span { Text("HANDMADE STONEWARE / LYON") }
            A("/cart", attrs = { classes(PageStyles.cartLink) }) { Text("CART ($cartCount)") }
        }
    }
}

@Composable
fun ProductPage(order: MugOrder, cartCount: Int) {
    Main(attrs = { classes(PageStyles.page) }) {
        Masthead(cartCount)

        Header(attrs = { classes(PageStyles.hero) }) {
            P(attrs = { classes(PageStyles.eyebrow) }) { Text("STONEWARE / N° 07") }
            H1 {
                Text("The morning mug,")
                Br()
                Em { Text("thrown by hand.") }
            }
            P(attrs = { classes(PageStyles.lead) }) {
                Text("Wheel-thrown in our Lyon studio, dipped in one of four glazes and fired twice. No two are exactly alike.")
            }
            Div(attrs = { classes(PageStyles.heroFoot) }) {
                Span { Text("€$MUG_PRICE / SHIPS IN 3 DAYS") }
                Span { Text("CHOOSE YOURS ↓") }
            }
        }

        Article(attrs = { classes(PageStyles.paper) }) {
            Header(attrs = { classes(PageStyles.paperHeader) }) {
                Span { Text("MAKE IT YOURS") }
                Span { Text("FREE SHIPPING FROM TWO MUGS") }
            }
            // Plain HTML form around the islands: their hidden inputs carry the configured mug
            Form("/cart", attrs = { method(FormMethod.Post) }) {
                Div(attrs = { classes(PageStyles.islandGrid) }) {
                    Section(attrs = { classes(PageStyles.islandSection) }) {
                        HydrationRoot(order.glaze, Glaze::name, hydrationId = IslandIds.GLAZE) {
                            GlazeIsland(it)
                        }
                    }

                    Section(attrs = { classes(PageStyles.islandSection) }) {
                        HydrationRoot(order.quantity, Int::toString, hydrationId = IslandIds.QUANTITY) {
                            QuantityIsland(it)
                        }
                    }
                }

                Section(attrs = { classes(PageStyles.islandSection) }) {
                    HydrationRoot(order.engraving, { it }, hydrationId = IslandIds.ENGRAVING) {
                        EngravingIsland(it)
                    }
                }
            }

            Section(attrs = { classes(PageStyles.detailsSection) }) {
                P(attrs = { classes(PageStyles.eyebrow) }) { Text("THE DETAILS") }
                H2 { Text("Made to be used every day.") }
                P { Text("Each mug is thrown, trimmed and glazed by hand, so expect small variations in shape and colour.") }
                DList(attrs = { classes(PageStyles.details) }) {
                    Detail("CAPACITY", "350 ml")
                    Detail("SIZE", "9 cm high, 8.5 cm wide")
                    Detail("CLAY", "Stoneware from the Rhône valley")
                    Detail("FIRING", "Bisque, then glaze at 1280 °C")
                    Detail("CARE", "Dishwasher and microwave safe")
                    Detail("SHIPPING", "Wrapped in recycled paper, 3 days")
                }
            }

            Footer(attrs = { classes(PageStyles.paperFooter) }) {
                Span { Text("ATELIER TERRE / HANDMADE IN LYON") }
            }
        }
    }
}

@Composable
private fun Detail(term: String, description: String) {
    Div {
        DTerm { Text(term) }
        DDescription { Text(description) }
    }
}
