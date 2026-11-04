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

package example.counter

import kotlinx.browser.window
import org.jetbrains.compose.web.hydrateRoot

/**
 * Browser entry point: hydrate the server-rendered counter, the rest of the page stays static HTML.
 * Each increment updates the URL without reloading the page, so reloading or sharing it renders
 * the current count on the server.
 */
fun main() {
    hydrateRoot(String::toInt, hydrationId = COUNTER_ID) {
        Counter(it) { count -> window.history.replaceState(null, "", "?count=$count") }
    }
}
