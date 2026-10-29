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

package example.islands

import org.jetbrains.compose.web.css.*

object PageStyles : StyleSheet(usePrefix = false) {
    private val ink = Color("#3a281f")
    private val terracotta = Color("#a45f4d")
    private val rule = Color("#e6d8c9")
    private const val serif = "Georgia, 'Iowan Old Style', 'Palatino Linotype', serif"

    val page by style { maxWidth(1300.px); property("margin", "auto"); padding(0.px, 48.px, 55.px) }
    val masthead by style {
        display(DisplayStyle.Flex); alignItems(AlignItems.Center); justifyContent(JustifyContent.SpaceBetween)
        gap(24.px); fontSize(13.px); fontWeight(700); letterSpacing(0.15.em)
        padding(27.px, 0.px); property("border-bottom", "1px solid #d9c7bb")
    }
    val brand by style {
        display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(12.px)
        color(ink); textDecoration("none")
    }
    val mastheadNav by style { display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(32.px) }
    val cartLink by style { color(terracotta); textDecoration("underline"); property("text-underline-offset", "4px") }
    val eyebrow by style {
        margin(0.px); color(terracotta); fontSize(12.px)
        fontWeight(700); letterSpacing(0.17.em)
    }
    val hero by style { padding(95.px, 5.percent, 0.px) }
    val lead by style {
        margin(0.px, 0.px, 88.px); color(terracotta)
        property("font-size", "clamp(17px, 1.5vw, 22px)"); lineHeight("1.45")
    }
    val heroFoot by style {
        display(DisplayStyle.Flex); alignItems(AlignItems.Center); justifyContent(JustifyContent.SpaceBetween)
        gap(24.px); fontSize(12.px); fontWeight(700); letterSpacing(0.15.em)
        padding(22.px, 0.px); property("border-top", "1px solid #d9c7bb")
    }
    val paper by style {
        overflow("hidden"); background("#fffaf1"); border(1.px, LineStyle.Solid, rule)
        borderRadius(36.px); property("box-shadow", "0 36px 100px #805b4730")
    }
    val paperHeader by style {
        display(DisplayStyle.Flex); justifyContent(JustifyContent.SpaceBetween); gap(20.px)
        padding(28.px, 6.percent); property("border-bottom", "1px solid $rule"); fontSize(12.px)
        fontWeight(700); letterSpacing(0.17.em)
    }
    val islandSection by style { padding(70.px, 6.percent); property("border-bottom", "1px solid $rule") }
    val islandGrid by style {
        display(DisplayStyle.Grid); gridTemplateColumns("repeat(2, minmax(0, 1fr))")
        property("border-bottom", "1px solid $rule")
    }
    val islandHeading by style {
        display(DisplayStyle.Flex); justifyContent(JustifyContent.SpaceBetween); alignItems(AlignItems.Start)
        gap(24.px); marginBottom(28.px)
    }
    val islandValue by style {
        flex("none"); alignSelf(AlignSelf.End); color(terracotta)
        font("400 32px/1 $serif"); letterSpacing((-0.04).em); whiteSpace("nowrap")
    }
    val islandBody by style {
        padding(30.px, 36.px); background("#f5e9db"); border(1.px, LineStyle.Solid, Color("#e4cdbd"))
        borderRadius(24.px)
    }
    val mugPreview by style {
        display(DisplayStyle.Flex); alignItems(AlignItems.Center); justifyContent(JustifyContent.Center)
        minHeight(210.px)
    }
    val mug by style {
        position(Position.Relative); width(120.px); height(140.px); marginRight(40.px)
        background("currentColor"); borderRadius(8.px, 8.px, 36.px, 36.px)
        property("box-shadow", "inset -18px 0 0 #00000014, 0 18px 30px #805b4733")
        property("transition", "color .25s")
    }
    val swatches by style {
        display(DisplayStyle.Flex); justifyContent(JustifyContent.Center); gap(14.px); paddingTop(20.px)
        property("border-top", "1px solid #dfc9b9")
    }
    val swatch by style {
        width(36.px); height(36.px); padding(0.px); borderRadius(50.percent)
        border(3.px, LineStyle.Solid, Color("#f5e9db")); property("box-shadow", "0 0 0 1px #cba891")
        cursor("pointer")
    }
    val stepper by style {
        display(DisplayStyle.Flex); alignItems(AlignItems.Center); justifyContent(JustifyContent.Center)
        gap(32.px); minHeight(210.px)
    }
    val stepButton by style {
        width(56.px); height(56.px); padding(0.px); borderRadius(50.percent)
        border(1.px, LineStyle.Solid, Color("#cba891")); background("#fffaf1"); color(ink)
        font("28px/1 $serif"); cursor("pointer")
    }
    val quantityValue by style {
        minWidth(1.2.em); textAlign("center"); color(ink)
        font("400 clamp(96px, 12vw, 150px)/1 $serif"); letterSpacing((-0.06).em)
    }
    val controls by style {
        display(DisplayStyle.Flex); justifyContent(JustifyContent.SpaceBetween); alignItems(AlignItems.Center)
        gap(20.px); paddingTop(20.px); property("border-top", "1px solid #dfc9b9")
        fontSize(12.px); fontWeight(700); letterSpacing(0.12.em)
    }
    val linkButton by style {
        padding(8.px, 0.px); border(0.px); background("transparent")
        color(terracotta); font("inherit"); cursor("pointer")
        textDecoration("underline"); property("text-underline-offset", "4px")
    }
    val engravingPreview by style {
        display(DisplayStyle.Flex); alignItems(AlignItems.Center); justifyContent(JustifyContent.Center)
        minHeight(210.px); margin(0.px); font("italic clamp(36px, 6vw, 76px)/1.12 $serif")
        letterSpacing((-0.055).em); textAlign("center"); property("overflow-wrap", "anywhere")
    }
    val inputLabel by style {
        display(DisplayStyle.Block); margin(28.px, 0.px, 8.px); fontSize(12.px)
        fontWeight(700); letterSpacing(0.14.em)
    }
    val textInput by style {
        width(100.percent); padding(14.px, 16.px); border(1.px, LineStyle.Solid, Color("#cba891"))
        borderRadius(9.px); background("#fffaf1"); color(ink)
        font("18px $serif")
    }
    val detailsSection by style { padding(70.px, 6.percent) }
    val details by style {
        display(DisplayStyle.Grid); gridTemplateColumns("repeat(3, minmax(0, 1fr))")
        gap(36.px, 24.px); margin(36.px, 0.px, 0.px)
    }
    val cartHero by style { padding(80.px, 5.percent, 56.px) }
    val emptyCart by style {
        display(DisplayStyle.Flex); flexDirection(FlexDirection.Column); alignItems(AlignItems.Start); gap(12.px)
        padding(70.px, 6.percent); fontSize(12.px); fontWeight(700); letterSpacing(0.12.em)
    }
    val cartItems by style { margin(0.px); padding(0.px); listStyle("none") }
    val cartItem by style {
        display(DisplayStyle.Grid); gridTemplateColumns("auto minmax(0, 1fr) auto auto"); alignItems(AlignItems.Center)
        gap(36.px); padding(36.px, 6.percent); property("border-bottom", "1px solid $rule")
        fontSize(12.px); fontWeight(700); letterSpacing(0.12.em)
    }
    val cartMug by style {
        width(48.px); height(56.px); background("currentColor"); borderRadius(4.px, 4.px, 16.px, 16.px)
        property("box-shadow", "inset -8px 0 0 #00000014, 0 10px 18px #805b4733")
    }
    val cartItemText by style { minWidth(0.px) }
    val cartEngraving by style {
        margin(0.px); color(Color("#795e51")); font("italic 18px/1.3 $serif"); letterSpacing(0.em)
        property("overflow-wrap", "anywhere")
    }
    val cartLineForm by style { display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(28.px) }
    val cartStepper by style { display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(14.px) }
    val cartQuantity by style {
        minWidth(1.2.em); textAlign("center"); color(ink); font("400 26px/1 $serif"); letterSpacing(0.em)
    }
    val cartPrice by style {
        minWidth(3.em); textAlign("right"); color(terracotta); font("400 32px/1 $serif"); letterSpacing((-0.04).em)
    }
    val updateButton by style { whiteSpace("nowrap") }
    val cartLineIsland by style { minWidth(0.px) }
    val cartSummary by style { padding(52.px, 6.percent) }
    val totals by style {
        display(DisplayStyle.Grid); gridTemplateColumns("repeat(3, minmax(0, 1fr))")
        gap(24.px); margin(0.px, 0.px, 36.px)
    }
    val paperFooter by style {
        padding(30.px, 6.percent); property("border-top", "1px solid $rule"); color(Color("#8a685a"))
        fontSize(12.px); fontWeight(700); letterSpacing(0.15.em)
    }

    init {
        "*" style { boxSizing("border-box") }
        "body" style {
            margin(0.px); minHeight(100.vh); color(ink); fontFamily("Arial", "Helvetica", "sans-serif")
            background("radial-gradient(circle at 90% 8%, #f7d5bf, transparent 38%), linear-gradient(145deg, #fff7e9, #f2dcc9 65%, #e9c9b6)")
        }
        ".${hero} h1" style {
            margin(32.px, 0.px); font("400 clamp(64px, 8.8vw, 126px)/.98 $serif"); letterSpacing((-0.075).em)
        }
        ".${hero} em" style { fontWeight(400) }
        ".${islandGrid} .${islandSection}" style {
            minWidth(0.px); padding(52.px, 6.percent, 58.px); property("border-bottom", "0")
        }
        ".${islandGrid} .${islandSection} + .${islandSection}" style { property("border-left", "1px solid $rule") }
        ".${islandGrid} .${islandHeading}" style { flexWrap(FlexWrap.Wrap); gap(12.px) }
        ".${islandGrid} .${islandHeading} h2" style { fontSize(48.px) }
        ".${islandGrid} .${islandBody}" style { padding(24.px) }
        ".${islandHeading} h2" style {
            margin(8.px, 0.px, 0.px); font("400 54px/1 $serif"); letterSpacing((-0.05).em)
        }
        ".${mug}::after" style {
            property("content", "\"\""); position(Position.Absolute); top(28.px); left(100.percent)
            width(46.px); height(72.px); marginLeft((-4).px)
            border(14.px, LineStyle.Solid, Color("currentColor")); property("border-left", "0")
            borderRadius(0.px, 36.px, 36.px, 0.px)
        }
        ".${swatch}[aria-pressed=true]" style { property("box-shadow", "0 0 0 3px $terracotta") }
        ".${stepButton}:hover:not(:disabled)" style { background("#f0dccb") }
        ".${stepButton}:disabled" style { opacity(0.35); cursor("default") }
        ".${cartStepper} .${stepButton}" style { width(36.px); height(36.px); fontSize(20.px) }
        ".${updateButton}:disabled" style { property("visibility", "hidden") }
        ".${linkButton}:hover, .${cartLink}:hover" style { color(Color("#693626")) }
        ".${swatch}:focus-visible, .${stepButton}:focus-visible, .${linkButton}:focus-visible, .${textInput}:focus-visible, .${brand}:focus-visible, .${cartLink}:focus-visible" style {
            outline("3px solid $terracotta"); property("outline-offset", "4px")
        }
        ".${detailsSection} h2" style {
            margin(18.px, 0.px, 12.px); font("400 48px/1 $serif"); letterSpacing((-0.05).em)
        }
        ".${detailsSection} > p:not(.${eyebrow})" style {
            maxWidth(650.px); color(Color("#795e51")); fontSize(17.px)
            lineHeight("1.55")
        }
        ".${cartHero} h1" style {
            margin(24.px, 0.px, 0.px); font("400 clamp(40px, 5vw, 64px)/1 $serif"); letterSpacing((-0.05).em)
        }
        ".${cartItem} h2" style {
            margin(8.px, 0.px, 6.px); font("400 32px/1 $serif"); letterSpacing((-0.04).em)
        }
        ".${details} dt, .${totals} dt" style {
            color(terracotta); fontSize(12.px); fontWeight(700); letterSpacing(0.15.em)
        }
        ".${details} dd, .${totals} dd" style {
            margin(10.px, 0.px, 0.px); paddingTop(10.px); property("border-top", "1px solid $rule")
            font("400 22px/1.3 $serif")
        }
        media("(max-width: 1000px)") {
            ".${islandGrid}" style { gridTemplateColumns("1fr") }
            ".${islandGrid} .${islandSection} + .${islandSection}" style {
                property("border-left", "0"); property("border-top", "1px solid $rule")
            }
            ".${details}" style { gridTemplateColumns("repeat(2, minmax(0, 1fr))") }
            ".${cartItem}" style { gridTemplateColumns("auto minmax(0, 1fr) auto"); gap(16.px, 28.px) }
            ".${cartItem} > .${cartLineIsland}" style { gridRow("2"); gridColumn("1 / 3") }
            ".${cartItem} > form" style { gridRow("2") }
        }
        media("(max-width: 720px)") {
            ".${page}" style { padding(0.px, 18.px, 55.px) }
            ".${mastheadNav} > span" style { display(DisplayStyle.None) }
            ".${hero}" style { padding(78.px, 3.percent, 0.px) }
            ".${hero} h1" style { property("font-size", "clamp(52px, 11vw, 80px)") }
            ".${lead}" style { marginBottom(72.px) }
            ".${paper}" style { borderRadius(22.px) }
            ".${paperHeader}" style { flexWrap(FlexWrap.Wrap); padding(22.px, 6.percent) }
            ".${islandSection}, .${detailsSection}" style { padding(50.px, 6.percent) }
            ".${islandHeading}" style { flexWrap(FlexWrap.Wrap) }
            ".${islandBody}" style { padding(20.px) }
            ".${details}, .${totals}" style { gridTemplateColumns("1fr") }
            ".${cartHero}" style { padding(64.px, 3.percent, 44.px) }
            ".${cartItem}, .${cartSummary}, .${emptyCart}" style { padding(32.px, 6.percent) }
        }
        media("(max-width: 430px)") {
            ".${controls}" style {
                alignItems(AlignItems.Start); flexDirection(FlexDirection.Column); gap(4.px)
            }
        }
    }
}
