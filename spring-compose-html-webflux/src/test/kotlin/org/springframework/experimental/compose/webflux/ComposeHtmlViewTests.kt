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

package org.springframework.experimental.compose.webflux

import androidx.compose.runtime.mutableStateOf
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.compose.web.dom.Body
import org.jetbrains.compose.web.dom.Html
import org.jetbrains.compose.web.dom.Text
import org.junit.jupiter.api.Test
import org.springframework.experimental.compose.TestDocument
import org.springframework.http.MediaType
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange

class ComposeHtmlViewTests {

    private val exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"))

    @Test
    fun renderDocument() {
        composeDocument { TestDocument("Café ✓") }.render(null, null, this.exchange).block()

        assertThat(this.exchange.response.headers.contentType).isEqualTo(MediaType.parseMediaType("text/html;charset=UTF-8"))
        assertThat(this.exchange.response.bodyAsString.block())
            .startsWith("<!doctype html><html>")
            .contains("<title>Café ✓</title>")
            .contains("<div data-compose-hydration-root=\"counter\">")
            .contains("data-compose-hydration-for=\"counter\" type=\"text/plain\">42</script>")
    }

    @Test
    fun renderIsDeferredUntilSubscription() {
        var rendered = false
        val result = composeDocument {
            rendered = true
            TestDocument("Spring")
        }.render(null, null, this.exchange)

        assertThat(rendered).isFalse()
        result.block()
        assertThat(rendered).isTrue()
    }

    @Test
    fun stateChangesDoNotLeakOutsideRendering() {
        val state = mutableStateOf("initial")
        val view = composeDocument {
            Html {
                Body {
                    state.value = "changed"
                    Text(state.value)
                }
            }
        }

        view.render(null, null, this.exchange).block()

        assertThat(this.exchange.response.bodyAsString.block()).contains("<body>changed</body>")
        assertThat(state.value).isEqualTo("initial")
    }
}
