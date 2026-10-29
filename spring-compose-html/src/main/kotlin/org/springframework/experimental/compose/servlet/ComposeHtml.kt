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
import org.springframework.experimental.compose.ComposeHtmlRenderer

/**
 * Create a view that renders a complete HTML document.
 *
 * [content] must produce exactly one `Html` element. The output starts with a doctype and supports
 * [org.jetbrains.compose.web.HydrationRoot] islands that the browser hydrates with `hydrateRoot`.
 *
 * @param key stable key per page for best-effort reuse of rendering storage, `null` to disable pooling
 * @param validateStrictly whether to use strict HTML validation, `null` for the Compose HTML default
 * (enabled with the `COMPOSE_HTML_VALIDATE_STRICTLY=true` environment variable)
 * @param content the document content
 */
public fun composeDocument(
    key: String? = null,
    validateStrictly: Boolean? = null,
    content: @Composable () -> Unit,
): ComposeHtmlView = ComposeHtmlView(ComposeHtmlRenderer(key, validateStrictly, content))
