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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class HydrationIslandsApplicationTests(@Autowired private val mockMvc: MockMvc) {

    @Test
    fun pageRendersIslandsWithDefaultState() {
        val html = render("/")

        assertThat(html)
            .startsWith("<!doctype html><html lang=\"en\">")
            .contains("<div data-compose-hydration-root=\"${IslandIds.GLAZE}\">")
            .contains(state(IslandIds.GLAZE, "CLAY"))
            .contains(state(IslandIds.QUANTITY, "1"))
            .contains(state(IslandIds.ENGRAVING, "Good morning"))
            .contains("<form action=\"/cart\" method=\"post\">")
            .contains("<input type=\"hidden\" name=\"${OrderFields.GLAZE}\" value=\"CLAY\">")
            .contains("<a href=\"/cart\" class=\"cartLink\">CART (0)</a>")
            .contains("<script type=\"module\" src=\"/app.js\"></script>")
    }

    @Test
    fun pageRendersIslandsWithRequestState() {
        val html = render("/?glaze=SAGE&quantity=42&engraving=For Ada")

        assertThat(html)
            .contains(state(IslandIds.GLAZE, "SAGE"))
            .contains(state(IslandIds.QUANTITY, "$MAX_QUANTITY"))
            .contains(state(IslandIds.ENGRAVING, "For Ada"))
    }

    @Test
    fun cartIsEmptyByDefault() {
        val html = render("/cart")

        assertThat(html)
            .startsWith("<!doctype html><html lang=\"en\">")
            .contains("Your cart is empty.")
            .doesNotContain("data-compose-hydration-root")
            .doesNotContain("/app.js")
    }

    @Test
    fun addToCartRedirectsToCartPage() {
        val session = MockHttpSession()
        addToCart(session, glaze = "SAGE", quantity = "2", engraving = "For Ada")
        addToCart(session, glaze = "SAGE", quantity = "1", engraving = "For Ada")
        addToCart(session, glaze = "INDIGO", quantity = "42", engraving = "")

        val html = render("/cart", session)

        assertThat(html)
            .contains("CART (${3 + MAX_QUANTITY})")
            .contains("“For Ada”")
            .contains(state(IslandIds.cartLine(1), "1:3"))
            .contains("No engraving")
            .contains(state(IslandIds.cartLine(2), "2:$MAX_QUANTITY"))
            .contains("<script type=\"module\" src=\"/app.js\"></script>")
            .contains("<dd>€${(3 + MAX_QUANTITY) * MUG_PRICE}</dd>")
            .contains("<dd>Free</dd>")
        assertThat(render("/", session)).contains("CART (${3 + MAX_QUANTITY})")
    }

    @Test
    fun singleMugIsChargedShipping() {
        val session = MockHttpSession()
        addToCart(session, glaze = "CREAM", quantity = "1", engraving = "")

        assertThat(render("/cart", session))
            .contains("<dd>€$SHIPPING_PRICE</dd>")
            .contains("<dd>€${MUG_PRICE + SHIPPING_PRICE}</dd>")
    }

    @Test
    fun mergedLinesAreCappedToMaxQuantity() {
        val session = MockHttpSession()
        addToCart(session, glaze = "SAGE", quantity = "$MAX_QUANTITY", engraving = "")
        addToCart(session, glaze = "SAGE", quantity = "1", engraving = "")

        assertThat(render("/cart", session)).contains(state(IslandIds.cartLine(1), "1:$MAX_QUANTITY"))
    }

    @Test
    fun updateCartLineQuantity() {
        val session = MockHttpSession()
        addToCart(session, glaze = "CLAY", quantity = "1", engraving = "")

        this.mockMvc.post("/cart/update") {
            this.session = session
            param(OrderFields.ID, "1")
            param(OrderFields.QUANTITY, "4")
        }.andExpect {
            redirectedUrl("/cart")
        }

        assertThat(render("/cart", session))
            .contains(state(IslandIds.cartLine(1), "1:4"))
            .contains("<dd>€${4 * MUG_PRICE}</dd>")
    }

    @Test
    fun removeFromCart() {
        val session = MockHttpSession()
        addToCart(session, glaze = "CLAY", quantity = "1", engraving = "Hello")
        assertThat(render("/cart", session)).contains("<input type=\"hidden\" name=\"id\" value=\"1\">")

        this.mockMvc.post("/cart/remove") {
            this.session = session
            param("id", "1")
        }.andExpect {
            redirectedUrl("/cart")
        }

        assertThat(render("/cart", session)).contains("Your cart is empty.")
    }

    @Test
    fun browserBundleIsServed() {
        this.mockMvc.get("/app.js").andExpect {
            status { isOk() }
        }
    }

    private fun addToCart(session: MockHttpSession, glaze: String, quantity: String, engraving: String) {
        this.mockMvc.post("/cart") {
            this.session = session
            param(OrderFields.GLAZE, glaze)
            param(OrderFields.QUANTITY, quantity)
            param(OrderFields.ENGRAVING, engraving)
        }.andExpect {
            redirectedUrl("/cart")
        }
    }

    private fun render(uri: String, session: MockHttpSession = MockHttpSession()): String = this.mockMvc.get(uri) {
        this.session = session
    }.andExpect {
        status { isOk() }
        content { contentTypeCompatibleWith(MediaType.TEXT_HTML) }
    }.andReturn().response.contentAsString

    private fun state(hydrationId: String, value: String) =
        "data-compose-hydration-for=\"$hydrationId\" type=\"text/plain\">$value</script>"
}
