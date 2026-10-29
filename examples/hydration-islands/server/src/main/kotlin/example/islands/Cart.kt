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

import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.SessionScope

const val SHIPPING_PRICE = 6

/** Cart line, identified so that it can be removed from the cart page. */
data class CartItem(val id: Int, val order: MugOrder) {
    val price: Int get() = order.quantity * MUG_PRICE
}

/** Shopping cart stored in the HTTP session. */
@Component
@SessionScope
class Cart {

    private val items = mutableListOf<CartItem>()

    private var nextId = 1

    /** Add an order, merging it with an existing line for the same glaze and engraving, up to [MAX_QUANTITY]. */
    @Synchronized
    fun add(order: MugOrder) {
        val index = items.indexOfFirst { it.order.glaze == order.glaze && it.order.engraving == order.engraving }
        if (index >= 0) {
            val item = items[index]
            val quantity = (item.order.quantity + order.quantity).coerceAtMost(MAX_QUANTITY)
            items[index] = item.copy(order = item.order.copy(quantity = quantity))
        } else {
            items += CartItem(nextId++, order)
        }
    }

    @Synchronized
    fun update(id: Int, quantity: Int) {
        val index = items.indexOfFirst { it.id == id }
        if (index >= 0) {
            val item = items[index]
            items[index] = item.copy(order = item.order.copy(quantity = quantity.coerceIn(1, MAX_QUANTITY)))
        }
    }

    @Synchronized
    fun remove(id: Int) {
        items.removeIf { it.id == id }
    }

    /** Immutable snapshot of the cart, safe to capture in a composition. */
    @Synchronized
    fun summary(): CartSummary = CartSummary(items.toList())
}

data class CartSummary(val items: List<CartItem>) {
    val count: Int get() = items.sumOf { it.order.quantity }
    val subtotal: Int get() = items.sumOf { it.price }
    /** Free from two mugs, as advertised on the product page. */
    val shipping: Int get() = if (count == 0 || count >= 2) 0 else SHIPPING_PRICE
    val total: Int get() = subtotal + shipping
}
