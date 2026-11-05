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

package org.springframework.experimental.compose.servlet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.Snapshot
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.jetbrains.compose.web.renderHydratedDocumentToStream
import org.springframework.web.servlet.View

/**
 * Spring MVC [View] that renders Compose HTML content as a complete document, streamed to the
 * response writer.
 *
 * Create instances with [composeDocument] for complete HTML documents, including
 * [org.jetbrains.compose.web.HydrationRoot] islands. The view can be returned from a controller
 * method or wrapped in a [org.springframework.web.servlet.ModelAndView].
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

    override fun getContentType(): String = CONTENT_TYPE

    override fun render(model: Map<String, *>?, request: HttpServletRequest, response: HttpServletResponse) {
        response.contentType = CONTENT_TYPE
        val writer = response.writer
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
    }

    override fun toString(): String = "ComposeHtmlView" + (if (this.key != null) " [key=${this.key}]" else "")

    private companion object {
        const val CONTENT_TYPE = "text/html;charset=UTF-8"
    }
}
