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

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.FormMethod
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.maxLength
import org.jetbrains.compose.web.attributes.method
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.*

/** Names of the form fields submitted by the page. */
object TodoFields {
    const val TITLE = "title"
    const val FILTER = "filter"
    const val COMPLETED = "completed"
}

/**
 * Complete todo list document. Every action is a plain HTML form posted to the server, so the
 * page works without any JavaScript.
 */
@Composable
fun TodoDocument(list: TodoList) {
    Html(attrs = { lang("en") }) {
        Head {
            Meta(attrs = { attr("charset", "utf-8") })
            Meta(attrs = {
                attr("name", "viewport")
                attr("content", "width=device-width, initial-scale=1")
            })
            Title { Text(if (list.activeCount == 0) "Todos" else "Todos (${list.activeCount})") }
            Style(TodoStyles)
        }
        Body {
            TodoPage(list)
        }
    }
}

@Composable
fun TodoPage(list: TodoList) {
    Main(attrs = { classes(TodoStyles.app) }) {
        Header(attrs = { classes(TodoStyles.header) }) {
            H1 { Text("Todos") }
            P(attrs = { classes(TodoStyles.progress) }) {
                Text(if (list.todos.isEmpty()) "Nothing planned yet" else "${list.completedCount} of ${list.todos.size} done")
            }
        }

        Section(attrs = { classes(TodoStyles.card) }) {
            Div(attrs = { classes(TodoStyles.entry) }) {
                if (list.todos.isNotEmpty()) ToggleAllForm(list) else Span()
                NewTodoForm(list)
            }

            if (list.todos.isNotEmpty()) {
                val visible = list.visible
                if (visible.isEmpty()) {
                    P(attrs = { classes(TodoStyles.emptyState) }) {
                        Text(if (list.filter == Filter.ACTIVE) "All done, well played!" else "Nothing completed yet.")
                    }
                } else {
                    Ul(attrs = { classes(TodoStyles.items) }) {
                        visible.forEach { todo ->
                            if (todo.id == list.editing) EditedTodoItem(todo, list.filter) else TodoItem(todo, list.filter)
                        }
                    }
                }
                TodoFooter(list)
            }
        }

        P(attrs = { classes(TodoStyles.hint) }) {
            Text("Click a todo to edit it, save it empty to delete it.")
        }
    }
}

@Composable
private fun NewTodoForm(list: TodoList) {
    Form("/todos", attrs = { classes(TodoStyles.newTodo); method(FormMethod.Post) }) {
        FilterInput(list.filter)
        TextInput {
            name(TodoFields.TITLE)
            classes(TodoStyles.textInput)
            placeholder("What needs to be done?")
            attr("aria-label", "New todo")
            maxLength(MAX_TITLE_LENGTH)
            required()
            // Keep the focus on the input after each addition, but not while editing another todo
            if (list.editing == null) autoFocus()
        }
        Button(attrs = { classes(TodoStyles.addButton); type(ButtonType.Submit) }) { Text("Add") }
    }
}

@Composable
private fun ToggleAllForm(list: TodoList) {
    Form("/todos/toggle-all", attrs = { method(FormMethod.Post) }) {
        FilterInput(list.filter)
        HiddenInput { name(TodoFields.COMPLETED); value((!list.allCompleted).toString()) }
        Button(attrs = {
            classes(TodoStyles.toggleAll)
            type(ButtonType.Submit)
            attr("aria-label", if (list.allCompleted) "Mark all as active" else "Mark all as completed")
            attr("aria-pressed", list.allCompleted.toString())
        }) { Text("❯") }
    }
}

@Composable
private fun TodoItem(todo: Todo, filter: Filter) {
    Li(attrs = {
        id("todo-${todo.id}")
        classes(TodoStyles.item)
        if (todo.completed) classes(TodoStyles.completed)
    }) {
        Form("/todos/${todo.id}/toggle", attrs = { method(FormMethod.Post) }) {
            FilterInput(filter)
            Button(attrs = {
                classes(TodoStyles.check)
                type(ButtonType.Submit)
                attr("aria-label", if (todo.completed) "Mark “${todo.title}” as active" else "Mark “${todo.title}” as completed")
                attr("aria-pressed", todo.completed.toString())
            }) { if (todo.completed) Text("✓") }
        }
        A("${filter.path}?edit=${todo.id}#todo-${todo.id}", attrs = {
            classes(TodoStyles.title)
            attr("title", "Edit")
        }) { Text(todo.title) }
        Form("/todos/${todo.id}/delete", attrs = { method(FormMethod.Post) }) {
            FilterInput(filter)
            Button(attrs = {
                classes(TodoStyles.destroy)
                type(ButtonType.Submit)
                attr("aria-label", "Delete “${todo.title}”")
            }) { Text("×") }
        }
    }
}

@Composable
private fun EditedTodoItem(todo: Todo, filter: Filter) {
    Li(attrs = {
        id("todo-${todo.id}")
        classes(TodoStyles.item, TodoStyles.editing)
    }) {
        Form("/todos/${todo.id}", attrs = { classes(TodoStyles.editForm); method(FormMethod.Post) }) {
            FilterInput(filter)
            TextInput(todo.title) {
                name(TodoFields.TITLE)
                classes(TodoStyles.textInput)
                attr("aria-label", "Edit “${todo.title}”")
                maxLength(MAX_TITLE_LENGTH)
                autoFocus()
            }
            Button(attrs = { classes(TodoStyles.addButton); type(ButtonType.Submit) }) { Text("Save") }
            A(filter.path, attrs = { classes(TodoStyles.linkButton) }) { Text("Cancel") }
        }
    }
}

@Composable
private fun TodoFooter(list: TodoList) {
    Footer(attrs = { classes(TodoStyles.footer) }) {
        Span(attrs = { classes(TodoStyles.count) }) {
            B { Text("${list.activeCount}") }
            Text(if (list.activeCount == 1) " item left" else " items left")
        }
        Nav(attrs = { classes(TodoStyles.filters); attr("aria-label", "Filter todos") }) {
            Filter.entries.forEach { filter ->
                A(filter.path, attrs = {
                    classes(TodoStyles.filter)
                    if (filter == list.filter) attr("aria-current", "page")
                }) { Text(filter.label) }
            }
        }
        if (list.completedCount > 0) {
            Form("/todos/clear-completed", attrs = { classes(TodoStyles.clear); method(FormMethod.Post) }) {
                FilterInput(list.filter)
                Button(attrs = { classes(TodoStyles.linkButton); type(ButtonType.Submit) }) {
                    Text("Clear completed (${list.completedCount})")
                }
            }
        } else {
            Span(attrs = { classes(TodoStyles.clear) })
        }
    }
}

/** Filter the form was submitted from, so that the server redirects back to the same page. */
@Composable
private fun FilterInput(filter: Filter) {
    HiddenInput { name(TodoFields.FILTER); value(filter.name) }
}
