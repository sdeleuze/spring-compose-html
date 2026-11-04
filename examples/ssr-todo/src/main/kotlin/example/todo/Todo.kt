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

import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.ListCrudRepository

const val MAX_TITLE_LENGTH = 200

data class Todo(
    val title: String,
    val completed: Boolean = false,
    @Id val id: Long? = null,
)

interface TodoRepository : ListCrudRepository<Todo, Long> {

    fun findAllByOrderByIdAsc(): List<Todo>

    @Modifying
    @Query("UPDATE todo SET completed = :completed")
    fun updateAllCompleted(completed: Boolean)

    @Modifying
    @Query("DELETE FROM todo WHERE completed")
    fun deleteAllCompleted()
}

/** Todo list filters, each one with its own page. */
enum class Filter(val label: String, val path: String, private val predicate: (Todo) -> Boolean) {
    ALL("All", "/", { true }),
    ACTIVE("Active", "/active", { !it.completed }),
    COMPLETED("Completed", "/completed", { it.completed });

    fun matches(todo: Todo): Boolean = predicate(todo)
}

/** Immutable state of the todo list page, safe to capture in a composition. */
data class TodoList(
    val todos: List<Todo>,
    val filter: Filter = Filter.ALL,
    /** ID of the todo being edited, if any. */
    val editing: Long? = null,
) {
    val visible: List<Todo> get() = todos.filter(filter::matches)
    val activeCount: Int get() = todos.count { !it.completed }
    val completedCount: Int get() = todos.size - activeCount
    val allCompleted: Boolean get() = todos.isNotEmpty() && activeCount == 0
}

/** Trimmed title limited to [MAX_TITLE_LENGTH], `null` when blank. */
fun normalizeTitle(title: String): String? = title.trim().take(MAX_TITLE_LENGTH).ifEmpty { null }
