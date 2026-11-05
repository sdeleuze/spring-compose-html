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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.Snapshot
import org.jetbrains.compose.web.renderHydratedDocumentToStream
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.web.reactive.result.view.View
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.io.OutputStreamWriter

/**
 * Spring WebFlux [View] that renders Compose HTML content as a complete document, buffered before
 * being written to the response.
 *
 * Create instances with [composeDocument] for complete HTML documents, including
 * [org.jetbrains.compose.web.HydrationRoot] islands. The view can be returned from a controller
 * method, as is or wrapped in a [Mono].
 *
 * The content gets its data from the values it captures, typically the typed parameters and local
 * variables of the handler method, so the model passed to [render] is ignored.
 *
 * Each render runs in its own mutable snapshot that is discarded afterwards, so state changes made
 * during server-side composition never leak into the global snapshot or into concurrent requests.
 */
public class ComposeHtmlView internal constructor(
    private val key: String?,
    private val validateStrictly: Boolean?,
    private val content: @Composable () -> Unit,
) : View {

    override fun getSupportedMediaTypes(): List<MediaType> = SUPPORTED_MEDIA_TYPES

    override fun render(model: Map<String, *>?, contentType: MediaType?, exchange: ServerWebExchange): Mono<Void> {
        val response = exchange.response
        response.headers.contentType = CONTENT_TYPE
        return response.writeWith(Mono.fromCallable { render(response.bufferFactory()) })
    }

    private fun render(bufferFactory: DataBufferFactory): DataBuffer {
        val buffer = bufferFactory.allocateBuffer(INITIAL_BUFFER_SIZE)
        try {
            val writer = OutputStreamWriter(buffer.asOutputStream(), Charsets.UTF_8)
            val snapshot = Snapshot.takeMutableSnapshot()
            try {
                snapshot.enter {
                    val strict = this.validateStrictly
                    if (strict != null) {
                        renderHydratedDocumentToStream(writer, validateStrictly = strict, key = this.key, content = this.content)
                    } else {
                        renderHydratedDocumentToStream(writer, key = this.key, content = this.content)
                    }
                }
            } finally {
                snapshot.dispose()
            }
            writer.flush()
            return buffer
        } catch (ex: Throwable) {
            DataBufferUtils.release(buffer)
            throw ex
        }
    }

    override fun toString(): String = "ComposeHtmlView" + (if (this.key != null) " [key=${this.key}]" else "")

    private companion object {
        val CONTENT_TYPE = MediaType("text", "html", Charsets.UTF_8)
        val SUPPORTED_MEDIA_TYPES = listOf(CONTENT_TYPE)
        const val INITIAL_BUFFER_SIZE = 8192
    }
}
