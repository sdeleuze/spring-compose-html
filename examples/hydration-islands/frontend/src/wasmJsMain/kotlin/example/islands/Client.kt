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
import kotlinx.browser.document
import org.jetbrains.compose.web.hydrateRoot
import org.w3c.dom.Element
import org.w3c.dom.asList

/**
 * Browser entry point shared by all the pages: hydrate only the islands present in the current
 * page, the rest of the page stays static HTML.
 */
fun main() {
    // Product page
    hydrateIfPresent(IslandIds.GLAZE, Glaze::valueOf) { GlazeIsland(it) }
    hydrateIfPresent(IslandIds.QUANTITY, String::toInt) { QuantityIsland(it) }
    hydrateIfPresent(IslandIds.ENGRAVING, { it }) { EngravingIsland(it) }

    // Cart page: as many islands as cart lines, discovered from the server-rendered markup
    hydrationIds(startingWith = IslandIds.CART_LINE_PREFIX).forEach { hydrationId ->
        hydrateRoot(CartLine::deserialize, hydrationId = hydrationId) { CartLineIsland(it) }
    }
}

private const val HYDRATION_ROOT_ATTRIBUTE = "data-compose-hydration-root"

private fun <T> hydrateIfPresent(hydrationId: String, deserializeState: (String) -> T, content: @Composable (T) -> Unit) {
    if (document.querySelector("[$HYDRATION_ROOT_ATTRIBUTE=\"$hydrationId\"]") != null) {
        hydrateRoot(deserializeState, hydrationId = hydrationId) { content(it) }
    }
}

private fun hydrationIds(startingWith: String): List<String> =
    document.querySelectorAll("[$HYDRATION_ROOT_ATTRIBUTE^=\"$startingWith\"]").asList()
        .mapNotNull { (it as Element).getAttribute(HYDRATION_ROOT_ATTRIBUTE) }
