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

/**
 * Mug configuration: initial state of the islands rendered on the server and handed over to the
 * browser for hydration, then the content of a cart line once added to the cart.
 */
data class MugOrder(
    val glaze: Glaze = Glaze.CLAY,
    val quantity: Int = 1,
    val engraving: String = "Good morning",
) {

    companion object {

        /** Create an order from optional request values, applying the defaults and the limits of the islands. */
        fun of(glaze: Glaze?, quantity: Int?, engraving: String?): MugOrder {
            val defaults = MugOrder()
            return MugOrder(
                glaze = glaze ?: defaults.glaze,
                quantity = (quantity ?: defaults.quantity).coerceIn(1, MAX_QUANTITY),
                engraving = (engraving ?: defaults.engraving).take(MAX_ENGRAVING_LENGTH),
            )
        }
    }
}
