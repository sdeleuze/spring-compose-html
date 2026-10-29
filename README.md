# Spring Compose HTML

Experimental Spring MVC integration for the
[Compose HTML EAP](https://github.com/JetBrains/compose-multiplatform/tree/master/html/eap):
render typed `@Composable` functions as complete HTML documents with hydration islands.

## Usage

The library is not published yet: use it as a project dependency
(`implementation(project(":spring-compose-html"))`) as the example does, or as an included build.

The Compose compiler plugin (`org.jetbrains.kotlin.plugin.compose`) must be applied to the
application, and the Compose HTML EAP artifacts come from the JetBrains development repository:

```kotlin
repositories {
    maven("https://packages.jetbrains.team/maven/p/cmp/dev")
}
```

Components are plain composable functions with typed parameters, no view names, no template
files, no `Map` model:

```kotlin
@Composable
fun TodoItem(todo: Todo) {
    Li(attrs = { id("todo-${todo.id}") }) { Text(todo.title) }
}
```

Spring MVC controllers use `org.springframework.experimental.compose.servlet`:

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

### Hydration islands

Within `composeDocument`, `HydrationRoot` renders an island and its public initial state. The
browser hydrates the islands with `hydrateRoot`, from a Kotlin/Wasm (or Kotlin/JS) bundle
sharing the same composables. See the [hydration islands example](#example).

### Details

- `key`: optional stable key per page (`composeDocument(key = "home") { ... }`)
  enabling best-effort reuse of Compose HTML rendering storage across requests.
- `validateStrictly`: overrides the Compose HTML validation mode, which defaults to fast
  validation, or strict with the `COMPOSE_HTML_VALIDATE_STRICTLY=true` environment variable.
- The Spring model is not used: content gets its data from what it captures, typically typed
  handler parameters and local variables. Attributes contributed outside the handler, like
  `@ModelAttribute` methods (including in `@ControllerAdvice`), can be injected as
  `@ModelAttribute` handler parameters, a CSRF token as a `CsrfToken` parameter.
- Each render runs in its own mutable snapshot that is discarded afterwards, so state written
  during server-side composition never leaks into the global snapshot or other requests.
- Spring MVC views stream the output to the response writer.

## Example

[`examples/hydration-islands`](examples/hydration-islands) is the Spring version of the
[Compose HTML EAP hydration islands example](https://github.com/JetBrains/compose-multiplatform/tree/master/html/eap/examples/hydration-islands):
a mostly static product page for a handmade mug, with three interactive islands to choose the
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
  page, packaged as the `app.js` bundle.
- `server`: Spring Boot application rendering the pages with `@Controller`s and serving the
  frontend bundle as static resources. Pages and bundle are gzip-compressed (`server.compression` in
  `application.properties`). The initial state of the islands comes from typed request parameters.

```shell
./gradlew :examples:hydration-islands:server:bootRun
```

Then open <http://localhost:8080/> or, for example,
<http://localhost:8080/?glaze=SAGE&quantity=2&engraving=For%20Ada>, and the cart at
<http://localhost:8080/cart>.

To reload the page automatically on frontend changes, also run the Kotlin/Wasm development server
in another terminal and open <http://localhost:8081/> instead: it serves the development bundle,
with source maps for debugging, and proxies the other requests to Spring Boot.

```shell
./gradlew -t :examples:hydration-islands:frontend:wasmJsBrowserDevelopmentRun
```

## Build

```shell
./gradlew build
```

Requires JDK 25 to build the example (the library itself targets Java 17), which runs on virtual threads.

## Notes

- This is a prototype built on EAP artifacts (`org.jetbrains.compose.html.eap:*:0.0.2+dev3`)
  with Kotlin 2.4, Spring Boot 4.2 and Spring Framework 7.1.
- `org.jetbrains.compose.runtime:runtime-desktop` is an empty stub jar with the same file name
  as `androidx.compose.runtime:runtime-desktop`. Spring Boot executable jars need to filter it
  out, see the `bootJar` configuration of the example server.
