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

package example.todo;

import java.util.List;

import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class TodoApplicationTests {

	@Autowired
	private MockMvcTester mockMvc;

	@Autowired
	private TodoRepository repository;

	@BeforeEach
	void clear() {
		this.repository.deleteAll();
	}

	@Test
	void emptyList() {
		assertThatPage("/")
				.startsWith("<!doctype html><html lang=\"en\">")
				.contains("<title>Todos</title>")
				.contains("Nothing planned yet")
				.contains("<form action=\"/todos\" method=\"post\" class=\"newTodo\">")
				.doesNotContain("<ul")
				.doesNotContain("<footer")
				.doesNotContain("<script");
	}

	@Test
	void createTodos() {
		post("/todos", TodoFields.TITLE, "  Buy milk  ");
		post("/todos", TodoFields.TITLE, "Write tests");

		List<Todo> todos = this.repository.findAllByOrderByIdAsc();
		Todo milk = todos.get(0);
		Todo tests = todos.get(1);
		assertThat(milk.title()).isEqualTo("Buy milk");
		assertThatPage("/")
				.contains("<title>Todos (2)</title>")
				.contains("0 of 2 done")
				.contains("<li id=\"todo-" + milk.id() + "\" class=\"item\">")
				.contains(">Buy milk</a>")
				.contains("<li id=\"todo-" + tests.id() + "\" class=\"item\">")
				.contains("<b>2</b> items left")
				.contains("<a href=\"/\" aria-current=\"page\" class=\"filter\">All</a>")
				.doesNotContain("Clear completed");
	}

	@Test
	void blankTitleIsIgnored() {
		post("/todos", TodoFields.TITLE, "   ");

		assertThat(this.repository.count()).isZero();
	}

	@Test
	void longTitleIsTruncated() {
		post("/todos", TodoFields.TITLE, "a".repeat(Todo.MAX_TITLE_LENGTH + 10));

		assertThat(this.repository.findAll()).singleElement()
				.extracting(Todo::title).asString().hasSize(Todo.MAX_TITLE_LENGTH);
	}

	@Test
	void toggleTodo() {
		Todo todo = this.repository.save(new Todo("Buy milk"));

		post("/todos/" + todo.id() + "/toggle");
		assertThatPage("/")
				.contains("<li id=\"todo-" + todo.id() + "\" class=\"item completed\">")
				.contains("1 of 1 done")
				.contains("Clear completed (1)");

		post("/todos/" + todo.id() + "/toggle");
		assertThat(this.repository.findAll()).singleElement().extracting(Todo::completed).isEqualTo(false);
	}

	@Test
	void filters() {
		this.repository.save(new Todo("Buy milk"));
		this.repository.save(new Todo("Write tests", true));

		assertThatPage("/active")
				.contains("Buy milk")
				.doesNotContain("Write tests")
				.contains("<a href=\"/active\" aria-current=\"page\" class=\"filter\">Active</a>")
				.contains("<b>1</b> item left");
		assertThatPage("/completed")
				.contains("Write tests")
				.doesNotContain("Buy milk");
	}

	@Test
	void emptyFilter() {
		this.repository.save(new Todo("Write tests", true));

		assertThatPage("/active").contains("All done, well played!");
	}

	@Test
	void actionsRedirectToTheirFilter() {
		Todo todo = this.repository.save(new Todo("Buy milk"));

		assertThat(this.mockMvc.post().uri("/todos/" + todo.id() + "/toggle").param(TodoFields.FILTER, Filter.ACTIVE.name()))
				.hasRedirectedUrl("/active");
		assertThat(this.mockMvc.post().uri("/todos/clear-completed").param(TodoFields.FILTER, Filter.COMPLETED.name()))
				.hasRedirectedUrl("/completed");
	}

	@Test
	void editTodo() {
		Todo todo = this.repository.save(new Todo("Buy milk"));

		assertThatPage("/?edit=" + todo.id())
				.contains("<li id=\"todo-" + todo.id() + "\" class=\"item editing\">")
				.contains("<form action=\"/todos/" + todo.id() + "\" method=\"post\" class=\"editForm\">")
				.contains("value=\"Buy milk\"");

		post("/todos/" + todo.id(), TodoFields.TITLE, "Buy oat milk");
		assertThat(this.repository.findAll()).singleElement().extracting(Todo::title).isEqualTo("Buy oat milk");
	}

	@Test
	void editWithBlankTitleDeletesTodo() {
		Todo todo = this.repository.save(new Todo("Buy milk"));

		post("/todos/" + todo.id(), TodoFields.TITLE, " ");

		assertThat(this.repository.count()).isZero();
	}

	@Test
	void deleteTodo() {
		Todo todo = this.repository.save(new Todo("Buy milk"));

		post("/todos/" + todo.id() + "/delete");

		assertThat(this.repository.count()).isZero();
	}

	@Test
	void unknownTodoIsIgnored() {
		post("/todos/42/toggle");
		post("/todos/42", TodoFields.TITLE, "Buy milk");
		post("/todos/42/delete");

		assertThat(this.repository.count()).isZero();
	}

	@Test
	void toggleAll() {
		this.repository.save(new Todo("Buy milk"));
		this.repository.save(new Todo("Write tests", true));
		assertThatPage("/").contains("<input type=\"hidden\" name=\"completed\" value=\"true\">");

		post("/todos/toggle-all", TodoFields.COMPLETED, "true");
		assertThat(this.repository.findAll()).allMatch(Todo::completed);
		assertThatPage("/")
				.contains("<input type=\"hidden\" name=\"completed\" value=\"false\">")
				.contains("aria-label=\"Mark all as active\"");

		post("/todos/toggle-all", TodoFields.COMPLETED, "false");
		assertThat(this.repository.findAll()).noneMatch(Todo::completed);
	}

	@Test
	void clearCompleted() {
		this.repository.save(new Todo("Buy milk"));
		this.repository.save(new Todo("Write tests", true));

		post("/todos/clear-completed");

		assertThat(this.repository.findAll()).extracting(Todo::title).containsExactly("Buy milk");
	}

	/** Post the given name/value parameter pairs to {@code uri} and expect a redirect to {@code /}. */
	private void post(String uri, String... params) {
		MockMvcRequestBuilder request = this.mockMvc.post().uri(uri);
		for (int i = 0; i < params.length; i += 2) {
			request.param(params[i], params[i + 1]);
		}
		assertThat(request).hasRedirectedUrl("/");
	}

	/** Render the page at {@code uri} and return assertions on its HTML. */
	private AbstractStringAssert<?> assertThatPage(String uri) {
		MvcTestResult result = this.mockMvc.get().uri(uri).exchange();
		assertThat(result).hasStatus(HttpStatus.OK).contentType().isCompatibleWith(MediaType.TEXT_HTML);
		return assertThat(result).bodyText();
	}
}
