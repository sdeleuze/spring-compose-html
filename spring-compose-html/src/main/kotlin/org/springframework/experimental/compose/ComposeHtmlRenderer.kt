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

package org.springframework.experimental.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.Snapshot
import org.jetbrains.compose.web.renderHydratedDocumentToStream
import java.io.Writer

/**
 * Render Compose HTML content as a complete document, used by the Spring MVC views.
 *
 * The content receives its data from the enclosing scope, typically typed values captured from the
 * handler method, so the Spring model is not used.
 *
 * Each render runs in its own mutable snapshot that is discarded afterwards, so state changes made
 * during server-side composition never leak into the global snapshot or into concurrent requests.
 */
internal class ComposeHtmlRenderer(
    private val key: String?,
    private val validateStrictly: Boolean?,
    private val content: @Composable () -> Unit,
) {

    fun render(writer: Writer) {
        val snapshot = Snapshot.takeMutableSnapshot()
        try {
            snapshot.enter { render(writer, this.content) }
        } finally {
            snapshot.dispose()
        }
    }

    private fun render(writer: Writer, content: @Composable () -> Unit) {
        val strict = validateStrictly
        if (strict != null) {
            renderHydratedDocumentToStream(writer, validateStrictly = strict, key = key, content = content)
        } else {
            renderHydratedDocumentToStream(writer, key = key, content = content)
        }
    }

    override fun toString(): String = "ComposeHtmlRenderer" + (if (key != null) " [key=$key]" else "")
}
