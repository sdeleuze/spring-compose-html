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

import org.jspecify.annotations.Nullable;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

/**
 * Todo list pages, one per {@link Filter}, rendered by the Kotlin {@link TodoPage#todoView} function.
 * The actions submitted by their plain HTML forms are handled by {@link TodoActionsController}.
 */
@Controller
public class TodoController {

	private final TodoRepository repository;

	public TodoController(TodoRepository repository) {
		this.repository = repository;
	}

	/** Render the page of {@link Filter#ALL}, with the todo of ID {@code edit} in edit mode if any ({@code /?edit=1}). */
	@GetMapping("/")
	public View all(@RequestParam @Nullable Long edit) {
		return page(Filter.ALL, edit);
	}

	@GetMapping("/active")
	public View active(@RequestParam @Nullable Long edit) {
		return page(Filter.ACTIVE, edit);
	}

	@GetMapping("/completed")
	public View completed(@RequestParam @Nullable Long edit) {
		return page(Filter.COMPLETED, edit);
	}

	private View page(Filter filter, @Nullable Long edit) {
		return TodoPage.todoView(new TodoList(this.repository.findAllByOrderByIdAsc(), filter, edit));
	}
}
