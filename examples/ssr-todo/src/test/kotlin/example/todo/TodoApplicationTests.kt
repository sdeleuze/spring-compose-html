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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class TodoApplicationTests(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val repository: TodoRepository,
) {

    @BeforeEach
    fun clear() {
        repository.deleteAll()
    }

    @Test
    fun emptyList() {
        assertThat(render("/"))
            .startsWith("<!doctype html><html lang=\"en\">")
            .contains("<title>Todos</title>")
            .contains("Nothing planned yet")
            .contains("<form action=\"/todos\" method=\"post\" class=\"newTodo\">")
            .doesNotContain("<ul")
            .doesNotContain("<footer")
            .doesNotContain("<script")
    }

    @Test
    fun createTodos() {
        post("/todos", TodoFields.TITLE to "  Buy milk  ")
        post("/todos", TodoFields.TITLE to "Write tests")

        val (milk, tests) = repository.findAllByOrderByIdAsc()
        assertThat(milk.title).isEqualTo("Buy milk")
        assertThat(render("/"))
            .contains("<title>Todos (2)</title>")
            .contains("0 of 2 done")
            .contains("<li id=\"todo-${milk.id}\" class=\"item\">")
            .contains(">Buy milk</a>")
            .contains("<li id=\"todo-${tests.id}\" class=\"item\">")
            .contains("<b>2</b> items left")
            .contains("<a href=\"/\" aria-current=\"page\" class=\"filter\">All</a>")
            .doesNotContain("Clear completed")
    }

    @Test
    fun blankTitleIsIgnored() {
        post("/todos", TodoFields.TITLE to "   ")

        assertThat(repository.count()).isZero()
    }

    @Test
    fun longTitleIsTruncated() {
        post("/todos", TodoFields.TITLE to "a".repeat(MAX_TITLE_LENGTH + 10))

        assertThat(repository.findAll().single().title).hasSize(MAX_TITLE_LENGTH)
    }

    @Test
    fun toggleTodo() {
        val todo = repository.save(Todo("Buy milk"))

        post("/todos/${todo.id}/toggle")
        assertThat(render("/"))
            .contains("<li id=\"todo-${todo.id}\" class=\"item completed\">")
            .contains("1 of 1 done")
            .contains("Clear completed (1)")

        post("/todos/${todo.id}/toggle")
        assertThat(repository.findAll().single().completed).isFalse()
    }

    @Test
    fun filters() {
        repository.save(Todo("Buy milk"))
        repository.save(Todo("Write tests", completed = true))

        assertThat(render("/active"))
            .contains("Buy milk")
            .doesNotContain("Write tests")
            .contains("<a href=\"/active\" aria-current=\"page\" class=\"filter\">Active</a>")
            .contains("<b>1</b> item left")
        assertThat(render("/completed"))
            .contains("Write tests")
            .doesNotContain("Buy milk")
    }

    @Test
    fun emptyFilter() {
        repository.save(Todo("Write tests", completed = true))

        assertThat(render("/active")).contains("All done, well played!")
    }

    @Test
    fun actionsRedirectToTheirFilter() {
        val todo = repository.save(Todo("Buy milk"))

        post("/todos/${todo.id}/toggle", TodoFields.FILTER to Filter.ACTIVE.name, redirectedUrl = "/active")
        post("/todos/clear-completed", TodoFields.FILTER to Filter.COMPLETED.name, redirectedUrl = "/completed")
    }

    @Test
    fun editTodo() {
        val todo = repository.save(Todo("Buy milk"))

        assertThat(render("/?edit=${todo.id}"))
            .contains("<li id=\"todo-${todo.id}\" class=\"item editing\">")
            .contains("<form action=\"/todos/${todo.id}\" method=\"post\" class=\"editForm\">")
            .contains("value=\"Buy milk\"")

        post("/todos/${todo.id}", TodoFields.TITLE to "Buy oat milk")
        assertThat(repository.findAll().single().title).isEqualTo("Buy oat milk")
    }

    @Test
    fun editWithBlankTitleDeletesTodo() {
        val todo = repository.save(Todo("Buy milk"))

        post("/todos/${todo.id}", TodoFields.TITLE to " ")

        assertThat(repository.count()).isZero()
    }

    @Test
    fun deleteTodo() {
        val todo = repository.save(Todo("Buy milk"))

        post("/todos/${todo.id}/delete")

        assertThat(repository.count()).isZero()
    }

    @Test
    fun unknownTodoIsIgnored() {
        post("/todos/42/toggle")
        post("/todos/42", TodoFields.TITLE to "Buy milk")
        post("/todos/42/delete")

        assertThat(repository.count()).isZero()
    }

    @Test
    fun toggleAll() {
        repository.save(Todo("Buy milk"))
        repository.save(Todo("Write tests", completed = true))
        assertThat(render("/")).contains("<input type=\"hidden\" name=\"completed\" value=\"true\">")

        post("/todos/toggle-all", TodoFields.COMPLETED to "true")
        assertThat(repository.findAll()).allMatch { it.completed }
        assertThat(render("/"))
            .contains("<input type=\"hidden\" name=\"completed\" value=\"false\">")
            .contains("aria-label=\"Mark all as active\"")

        post("/todos/toggle-all", TodoFields.COMPLETED to "false")
        assertThat(repository.findAll()).noneMatch { it.completed }
    }

    @Test
    fun clearCompleted() {
        repository.save(Todo("Buy milk"))
        repository.save(Todo("Write tests", completed = true))

        post("/todos/clear-completed")

        assertThat(repository.findAll().map { it.title }).containsExactly("Buy milk")
    }

    private fun post(uri: String, vararg params: Pair<String, String>, redirectedUrl: String = "/") {
        this.mockMvc.post(uri) {
            params.forEach { (name, value) -> param(name, value) }
        }.andExpect {
            redirectedUrl(redirectedUrl)
        }
    }

    private fun render(uri: String): String = this.mockMvc.get(uri).andExpect {
        status { isOk() }
        content { contentTypeCompatibleWith(MediaType.TEXT_HTML) }
    }.andReturn().response.contentAsString
}
