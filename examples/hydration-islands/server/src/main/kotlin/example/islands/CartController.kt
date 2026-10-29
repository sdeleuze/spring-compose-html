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

import org.springframework.experimental.compose.servlet.ComposeHtmlView
import org.springframework.experimental.compose.servlet.composeDocument
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/cart")
class CartController(private val cart: Cart) {

    /** Render the cart page, with one quantity island per cart line. */
    @GetMapping
    fun page(): ComposeHtmlView {
        val summary = cart.summary()
        return composeDocument(key = "cart-page") {
            CartDocument(summary)
        }
    }

    /**
     * Add the mug configured on the product page, submitted with the hidden inputs of the islands,
     * then redirect to the cart page (Post/Redirect/Get).
     */
    @PostMapping
    fun add(
        @RequestParam glaze: Glaze?,
        @RequestParam quantity: Int?,
        @RequestParam engraving: String?,
    ): String {
        cart.add(MugOrder.of(glaze, quantity, engraving))
        return "redirect:/cart"
    }

    /** Update the quantity of a line, submitted by its cart line island. */
    @PostMapping("/update")
    fun update(@RequestParam id: Int, @RequestParam quantity: Int): String {
        cart.update(id, quantity)
        return "redirect:/cart"
    }

    @PostMapping("/remove")
    fun remove(@RequestParam id: Int): String {
        cart.remove(id)
        return "redirect:/cart"
    }
}
