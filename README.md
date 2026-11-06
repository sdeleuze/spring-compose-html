# Spring Compose HTML

Experimental Spring MVC and Spring WebFlux integrations for the
[Compose HTML EAP](https://github.com/JetBrains/compose-multiplatform/tree/master/html/eap):
render typed `@Composable` functions as complete HTML documents, with optional client-side [hydration](https://en.wikipedia.org/wiki/Hydration_(web_development)).

Compose HTML reuses the programming model that millions of Android developers use to build mobile
UIs with [Jetpack Compose](https://developer.android.com/compose), applied to HTML and CSS to build
websites and web applications. Rendering pages on the server and hydrating only the interactive
parts in the browser shares some principles with the
[Astro Islands architecture](https://docs.astro.build/en/concepts/islands/) or
[Next.js Server-Side Rendering](https://nextjs.org/docs/pages/building-your-application/rendering/server-side-rendering).

For Spring developers, it is an alternative to traditional templating engines like
[Thymeleaf](https://www.thymeleaf.org/), [FreeMarker](https://freemarker.apache.org/index.html) or
[JMustache](https://github.com/samskivert/jmustache), or to full-stack UI frameworks like
[Vaadin](https://vaadin.com/).

See the [Exploring Compose HTML for Server-Side Rendering](https://blog.jetbrains.com/kotlin/2026/08/exploring-compose-html-for-server-side-rendering/)
blog post for more context about Compose HTML.

![Spring Compose HTML](assets/spring-compose-html.png)

## Usage

The libraries are not published yet: install them in your local Maven repository with:

```shell
./gradlew publishToMavenLocal
```

Then add the dependency matching your web stack:

- `org.springframework.experimental:spring-compose-html-webmvc` for Spring MVC
- `org.springframework.experimental:spring-compose-html-webflux` for Spring WebFlux

The Compose compiler plugin (`org.jetbrains.kotlin.plugin.compose`) must be applied to the
application, and the Compose HTML EAP artifacts come from the JetBrains development repository:

```kotlin
repositories {
    mavenLocal()
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/cmp/dev")
}

dependencies {
    implementation("org.springframework.experimental:spring-compose-html-webmvc:0.1.0-SNAPSHOT")
}
```

The Compose HTML lowering plugin (`org.jetbrains.compose.html.lowering`) can optionally be applied
for faster rendering.

Components are plain composable functions with typed parameters, no view names, no template
files, no `Map` model:

```kotlin
@Composable
fun TodoItem(todo: Todo) {
    Li(attrs = { id("todo-${todo.id}") }) { Text(todo.title) }
}
```

Spring MVC controllers use `org.springframework.experimental.compose.servlet`, Spring WebFlux ones
`org.springframework.experimental.compose.webflux`, both providing the same `composeDocument` function
returning a `ComposeHtmlView` for the related web stack:

```kotlin
@Controller
class TodoController(private val repository: TodoRepository) {

    // Complete document: doctype, head, styles and optional hydration islands
    @GetMapping("/todos")
    fun page(): View {
        val todos = repository.findAll()
        return composeDocument {
            Html {
                Head { Title { Text("Todos") } }
                Body { todos.forEach { TodoItem(it) } }
            }
        }
    }
}
```

### Hydration

Within `composeDocument`, `HydrationRoot` renders an island and its public initial state. The
browser hydrates the islands with `hydrateRoot`, from a Kotlin/Wasm (or Kotlin/JS) bundle
sharing the same composables. See the [hydration counter](#hydration-counter) and [hydration islands](#hydration-islands) examples.

## Examples

### Hydration counter

[`examples/hydration-counter`](examples/hydration-counter) is the most basic hydration example: a page with
a counter rendered on the server, and a button incrementing it once the browser has hydrated it.

- `shared`: Kotlin Multiplatform library (JVM and Wasm) with the `Counter` composable, used by both
  the server and the frontend.
- `frontend`: Kotlin/Wasm browser application whose entry point hydrates the counter, packaged as
  the `counter.js` bundle.
- `server`: Spring Boot application rendering the page, with the counter wrapped in a
  `HydrationRoot` carrying its initial count, and serving the frontend bundle as static resources.

```shell
./gradlew :examples:hydration-counter:server:bootRun
```

Then open <http://localhost:8080/> or, for example, <http://localhost:8080/?count=5> to start
from another count.

### Hydration islands

A mostly static product page for a handmade mug, with three interactive islands to choose the
glaze, the quantity and an engraving, and a cart page.

The islands sit inside a plain HTML form and expose their state as hidden inputs: "Add to cart"
posts the configured mug to the server, which stores it in a session-scoped cart and redirects to
the cart page. The cart page renders one quantity island per cart line, each with its own
hydration ID: the stepper updates the line price in the browser, "Update" posts the new quantity
so the server recomputes the totals.

Both pages share a single browser bundle whose `main()` hydrates only the islands present in the
current page: the fixed product page islands when found, and the cart line islands discovered
from their hydration ID prefix.

- `shared`: Kotlin Multiplatform library (JVM and Wasm) with the island composables and styles,
  used by both the server and the frontend so the browser hydrates exactly what the server rendered.
- `frontend`: Kotlin/Wasm browser application whose entry point hydrates the islands of any
  page, packaged as the `islands.js` bundle.
- `server`: Spring Boot application rendering the pages with `@Controller`s and serving the
  frontend bundle as static resources. Pages and bundle are gzip-compressed (`server.compression` in
  `application.properties`). The initial state of the islands comes from typed request parameters.

```shell
./gradlew :examples:hydration-islands:server:bootRun
```

Then open <http://localhost:8080/> or, for example,
<http://localhost:8080/?glaze=SAGE&quantity=2&engraving=For%20Ada>, and the cart at
<http://localhost:8080/cart>.

### Server-Side Rendering TODO application

[`examples/ssr-todo`](examples/ssr-todo) is a typical TODO application, fully rendered on the server
without any JavaScript or Wasm.

Every action is a plain HTML form posted to a `@Controller` that redirects back to the page it was
submitted from (Post/Redirect/Get). Clicking a todo renders the same page with this todo in edit
mode (`/?edit=1`), saving it empty deletes it. The todos are stored in an in-memory H2 database
with Spring Data JDBC.

```shell
./gradlew :examples:ssr-todo:bootRun
```

Then open <http://localhost:8080/>.

## Notes

- This is a prototype built on Compose HTML EAP artifacts with Kotlin 2.4, Spring Boot 4.2 and Spring Framework 7.1.