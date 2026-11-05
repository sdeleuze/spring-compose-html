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

import org.springframework.data.repository.findByIdOrNull
import org.springframework.experimental.compose.servlet.ComposeHtmlView
import org.springframework.experimental.compose.servlet.composeDocument
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * Todo list pages, one per [Filter], and the actions submitted by their plain HTML forms. Each action
 * redirects to the page of the filter it was submitted from (Post/Redirect/Get).
 */
@Controller
class TodoController(private val repository: TodoRepository) {

    /** Render the page of [Filter.ALL], with the todo of ID `edit` in edit mode if any (`/?edit=1`). */
    @GetMapping("/")
    fun all(@RequestParam edit: Long?) = page(Filter.ALL, edit)

    @GetMapping("/active")
    fun active(@RequestParam edit: Long?) = page(Filter.ACTIVE, edit)

    @GetMapping("/completed")
    fun completed(@RequestParam edit: Long?) = page(Filter.COMPLETED, edit)

    private fun page(filter: Filter, edit: Long?): ComposeHtmlView {
        val list = TodoList(repository.findAllByOrderByIdAsc(), filter, edit)
        return composeDocument(key = "todo-page") {
            TodoDocument(list)
        }
    }
}

@Controller
@RequestMapping("/todos")
class TodoActionsController(private val repository: TodoRepository) {

    @PostMapping
    fun create(@RequestParam title: String, @RequestParam filter: Filter?): String {
        normalizeTitle(title)?.let { repository.save(Todo(it)) }
        return redirect(filter)
    }

    /** Rename a todo, deleting it when the new title is blank. */
    @PostMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestParam title: String, @RequestParam filter: Filter?): String {
        repository.findByIdOrNull(id)?.let { todo ->
            val normalized = normalizeTitle(title)
            if (normalized == null) repository.delete(todo) else repository.save(todo.copy(title = normalized))
        }
        return redirect(filter)
    }

    @PostMapping("/{id}/toggle")
    fun toggle(@PathVariable id: Long, @RequestParam filter: Filter?): String {
        repository.findByIdOrNull(id)?.let { repository.save(it.copy(completed = !it.completed)) }
        return redirect(filter)
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long, @RequestParam filter: Filter?): String {
        repository.deleteById(id)
        return redirect(filter)
    }

    /** Mark all the todos as completed or active: the page submits active once they are all completed. */
    @PostMapping("/toggle-all")
    fun toggleAll(@RequestParam completed: Boolean, @RequestParam filter: Filter?): String {
        repository.updateAllCompleted(completed)
        return redirect(filter)
    }

    @PostMapping("/clear-completed")
    fun clearCompleted(@RequestParam filter: Filter?): String {
        repository.deleteAllCompleted()
        return redirect(filter)
    }

    private fun redirect(filter: Filter?) = "redirect:${(filter ?: Filter.ALL).path}"
}
