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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Output
import org.jetbrains.compose.web.dom.Text

/** Hydration ID shared by the server-rendered page and the browser entry point. */
const val COUNTER_ID = "counter"

/**
 * Counter island, rendered on the server with its initial count, then hydrated in the browser
 * where the button increments it and notifies [onCountChange], which only the browser uses.
 */
@Composable
fun Counter(initialCount: Int, onCountChange: (Int) -> Unit = {}) {
    var count by remember(initialCount) { mutableStateOf(initialCount) }

    Div(attrs = { classes(CounterStyles.card) }) {
        Output(attrs = { classes(CounterStyles.count) }) { Text("$count") }
        Button(attrs = {
            classes(CounterStyles.button)
            type(ButtonType.Button)
            onClick {
                count++
                onCountChange(count)
            }
        }) { Text("Increment") }
    }
}
