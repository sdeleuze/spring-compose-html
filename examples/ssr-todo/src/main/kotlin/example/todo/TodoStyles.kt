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

package example.todo

import org.jetbrains.compose.web.css.*

object TodoStyles : StyleSheet(usePrefix = false) {
    private val ink = Color("#1f2a2e")
    private val accent = Color("#2f6f5e")
    private val muted = Color("#7a8580")
    private val rule = Color("#e7e2d8")
    private const val serif = "Georgia, 'Iowan Old Style', 'Palatino Linotype', serif"

    val app by style { maxWidth(640.px); property("margin", "auto"); padding(72.px, 20.px, 48.px) }
    val header by style {
        display(DisplayStyle.Flex); alignItems(AlignItems.Baseline); justifyContent(JustifyContent.SpaceBetween)
        gap(16.px); marginBottom(24.px)
    }
    val progress by style { margin(0.px); color(muted); fontSize(14.px) }
    val card by style {
        overflow("hidden"); background("#fffdf9"); border(1.px, LineStyle.Solid, rule)
        borderRadius(18.px); property("box-shadow", "0 24px 60px #2f3b3a1f")
    }
    val entry by style {
        display(DisplayStyle.Grid); gridTemplateColumns("56px minmax(0, 1fr)"); alignItems(AlignItems.Center)
        property("border-bottom", "1px solid $rule")
    }
    val toggleAll by style {
        width(56.px); height(64.px); padding(0.px); border(0.px); background("transparent")
        color(Color("#c4c9c4")); fontSize(18.px); cursor("pointer"); property("transform", "rotate(90deg)")
    }
    val newTodo by style {
        display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(12.px); padding(12.px, 16.px, 12.px, 0.px)
    }
    val editForm by style {
        display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(12.px); width(100.percent)
    }
    val textInput by style {
        property("flex", "1"); minWidth(0.px); padding(10.px, 12.px); border(1.px, LineStyle.Solid, Color("transparent"))
        borderRadius(10.px); background("transparent"); color(ink); font("20px/1.3 $serif")
    }
    val addButton by style {
        padding(9.px, 16.px); border(0.px); borderRadius(999.px); background(accent.toString())
        color(Color("#fff")); fontSize(14.px); fontWeight(700); cursor("pointer")
    }
    val linkButton by style {
        padding(4.px, 0.px); border(0.px); background("transparent"); color(muted)
        font("inherit"); cursor("pointer"); textDecoration("none")
    }
    val items by style { margin(0.px); padding(0.px); listStyle("none") }
    val item by style {
        display(DisplayStyle.Grid); gridTemplateColumns("56px minmax(0, 1fr) 48px"); alignItems(AlignItems.Center)
        minHeight(64.px); property("border-bottom", "1px solid $rule")
    }
    val completed by style {}
    val editing by style { gridTemplateColumns("56px minmax(0, 1fr)"); padding(8.px, 16.px, 8.px, 0.px) }
    val check by style {
        display(DisplayStyle.Block); width(28.px); height(28.px); margin(0.px, 14.px); padding(0.px)
        border(1.5.px, LineStyle.Solid, Color("#b9c2bd")); borderRadius(50.percent); background("transparent")
        color(accent); fontSize(15.px); fontWeight(700); cursor("pointer")
    }
    val title by style {
        padding(18.px, 12.px); color(ink); font("20px/1.3 $serif"); textDecoration("none")
        property("overflow-wrap", "anywhere"); property("transition", "color .2s")
    }
    val destroy by style {
        width(48.px); height(48.px); padding(0.px); border(0.px); background("transparent")
        color(Color("#c0857a")); fontSize(26.px); cursor("pointer"); opacity(0)
    }
    val emptyState by style {
        margin(0.px); padding(36.px, 24.px); color(muted); textAlign("center"); font("italic 18px $serif")
        property("border-bottom", "1px solid $rule")
    }
    val footer by style {
        display(DisplayStyle.Grid); gridTemplateColumns("1fr auto 1fr"); alignItems(AlignItems.Center)
        gap(12.px); padding(10.px, 18.px); color(muted); fontSize(14.px)
    }
    val count by style {}
    val filters by style { display(DisplayStyle.Flex); gap(4.px) }
    val filter by style {
        padding(4.px, 10.px); border(1.px, LineStyle.Solid, Color("transparent")); borderRadius(8.px)
        color(muted); textDecoration("none")
    }
    val clear by style { justifySelf("end") }
    val hint by style { marginTop(28.px); color(muted); fontSize(13.px); textAlign("center") }

    init {
        "*" style { boxSizing("border-box") }
        "body" style {
            margin(0.px); minHeight(100.vh); color(ink)
            fontFamily("system-ui", "-apple-system", "Segoe UI", "Helvetica", "Arial", "sans-serif")
            background("radial-gradient(circle at 15% 0%, #dbe8de, transparent 45%), #f4f1ea")
        }
        ".${header} h1" style {
            margin(0.px); font("400 64px/1 $serif"); letterSpacing((-0.04).em)
        }
        ".${count} b" style { color(ink) }
        ".${toggleAll}[aria-pressed=true]" style { color(ink) }
        ".${completed} .${check}" style { property("border-color", accent) }
        ".${completed} .${title}" style { color(Color("#a7aea9")); textDecoration("line-through") }
        ".${item}:hover .${destroy}, .${destroy}:focus-visible" style { opacity(1) }
        ".${destroy}:hover" style { color(Color("#a64b3b")) }
        ".${check}:hover" style { property("border-color", accent) }
        ".${textInput}:focus" style { outline("none"); property("border-color", Color("#b9c2bd")); background("#fff") }
        ".${textInput}::placeholder" style { color(Color("#b0b6b1")); fontStyle("italic") }
        ".${editing} .${textInput}" style { property("border-color", Color("#b9c2bd")); background("#fff") }
        ".${addButton}:hover" style { background("#24584a") }
        ".${linkButton}:hover, .${filter}:hover" style { color(ink) }
        ".${filter}[aria-current=page]" style { property("border-color", Color("#cfd6d1")); color(ink) }
        ".${toggleAll}:focus-visible, .${check}:focus-visible, .${title}:focus-visible, .${destroy}:focus-visible, .${addButton}:focus-visible, .${linkButton}:focus-visible, .${filter}:focus-visible" style {
            outline("2px solid $accent"); property("outline-offset", "2px")
        }
        media("(hover: none)") {
            ".${destroy}" style { opacity(1) }
        }
        media("(max-width: 520px)") {
            ".${app}" style { padding(40.px, 12.px, 32.px) }
            ".${header} h1" style { fontSize(48.px) }
            ".${footer}" style { gridTemplateColumns("1fr auto") }
            ".${filters}" style { gridRow("2"); gridColumn("1 / 3"); justifyContent(JustifyContent.Center) }
        }
    }
}
