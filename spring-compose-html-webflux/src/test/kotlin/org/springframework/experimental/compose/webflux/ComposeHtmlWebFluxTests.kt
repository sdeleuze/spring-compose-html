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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.experimental.compose.TestDocument
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.reactive.result.view.View
import reactor.core.publisher.Mono

class ComposeHtmlWebFluxTests {

    private val client: WebTestClient = WebTestClient.bindToController(TestController()).build()

    @Test
    fun document() {
        assertDocument("/document")
    }

    @Test
    fun monoDocument() {
        assertDocument("/mono-document")
    }

    private fun assertDocument(path: String) {
        val body = this.client.get().uri(path).exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
            .expectBody<String>().returnResult().responseBody

        assertThat(body).startsWith("<!doctype html><html><head><title>Spring</title>")
    }

    @Controller
    class TestController {

        @GetMapping("/document")
        fun document(): View = composeDocument { TestDocument("Spring") }

        @GetMapping("/mono-document")
        fun monoDocument(): Mono<View> = Mono.fromSupplier { composeDocument { TestDocument("Spring") } }

    }
}
