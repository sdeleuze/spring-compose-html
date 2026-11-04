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

import org.jetbrains.compose.web.css.*

object CounterStyles : StyleSheet(usePrefix = false) {
    private val ink = Color("#1f2a2e")
    private val accent = Color("#2f6f5e")

    val card by style {
        display(DisplayStyle.Flex); flexDirection(FlexDirection.Column); alignItems(AlignItems.Center)
        gap(24.px); padding(48.px, 64.px); background("#fffdf9")
        border(1.px, LineStyle.Solid, Color("#e7e2d8")); borderRadius(18.px)
        property("box-shadow", "0 24px 60px #2f3b3a1f")
    }
    val count by style {
        minWidth(2.em); textAlign("center"); color(ink)
        font("400 96px/1 Georgia, serif"); property("font-variant-numeric", "tabular-nums")
    }
    val button by style {
        padding(12.px, 28.px); border(0.px); borderRadius(999.px); background(accent.toString())
        color(Color.white); fontSize(16.px); fontWeight(700); cursor("pointer")
    }

    init {
        "body" style {
            display(DisplayStyle.Flex); flexDirection(FlexDirection.Column); alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center); gap(24.px); margin(0.px); minHeight(100.vh)
            color(ink); fontFamily("system-ui", "-apple-system", "Segoe UI", "Helvetica", "Arial", "sans-serif")
            background("radial-gradient(circle at 15% 0%, #dbe8de, transparent 45%), #f4f1ea")
        }
        "h1" style { margin(0.px); font("400 48px/1 Georgia, serif"); letterSpacing((-0.04).em) }
        ".${button}:hover" style { background("#24584a") }
        ".${button}:focus-visible" style { outline("2px solid $accent"); property("outline-offset", "3px") }
    }
}
