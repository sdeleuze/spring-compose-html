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

import org.jetbrains.compose.web.HydrationRoot
import org.jetbrains.compose.web.attributes.ScriptType
import org.jetbrains.compose.web.attributes.src
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.*
import org.springframework.experimental.compose.servlet.ComposeHtmlView
import org.springframework.experimental.compose.servlet.composeDocument
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class CounterController {

    /** Render the page on the server, with the initial count from the request, for example `/?count=5`. */
    @GetMapping("/")
    fun page(@RequestParam count: Int?): ComposeHtmlView = composeDocument(key = "counter") {
        Html(attrs = { lang("en") }) {
            Head {
                Meta(attrs = { attr("charset", "utf-8") })
                Meta(attrs = {
                    attr("name", "viewport")
                    attr("content", "width=device-width, initial-scale=1")
                })
                // Empty inline icon, so the browser does not request /favicon.ico
                Link(attrs = {
                    attr("rel", "icon")
                    attr("href", "data:,")
                })
                Title { Text("Counter") }
                Style(CounterStyles)
            }
            Body {
                H1 { Text("Counter") }
                // Server-rendered counter and its initial state, hydrated by the browser bundle
                HydrationRoot(count ?: 0, Int::toString, hydrationId = COUNTER_ID) {
                    Counter(it)
                }
                Script(attrs = { type(ScriptType.Module); src("/counter.js") })
            }
        }
    }
}
