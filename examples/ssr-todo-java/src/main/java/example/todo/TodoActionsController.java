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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Actions submitted by the plain HTML forms of the todo list pages. Each action redirects to the
 * page of the filter it was submitted from (Post/Redirect/Get).
 */
@Controller
@RequestMapping("/todos")
public class TodoActionsController {

	private final TodoRepository repository;

	public TodoActionsController(TodoRepository repository) {
		this.repository = repository;
	}

	@PostMapping
	public String create(@RequestParam String title, @RequestParam @Nullable Filter filter) {
		String normalized = Todo.normalizeTitle(title);
		if (normalized != null) {
			this.repository.save(new Todo(normalized));
		}
		return redirect(filter);
	}

	/** Rename a todo, deleting it when the new title is blank. */
	@PostMapping("/{id}")
	public String update(@PathVariable long id, @RequestParam String title, @RequestParam @Nullable Filter filter) {
		this.repository.findById(id).ifPresent(todo -> {
			String normalized = Todo.normalizeTitle(title);
			if (normalized == null) {
				this.repository.delete(todo);
			}
			else {
				this.repository.save(todo.withTitle(normalized));
			}
		});
		return redirect(filter);
	}

	@PostMapping("/{id}/toggle")
	public String toggle(@PathVariable long id, @RequestParam @Nullable Filter filter) {
		this.repository.findById(id).ifPresent(todo -> this.repository.save(todo.withCompleted(!todo.completed())));
		return redirect(filter);
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable long id, @RequestParam @Nullable Filter filter) {
		this.repository.deleteById(id);
		return redirect(filter);
	}

	/** Mark all the todos as completed or active: the page submits active once they are all completed. */
	@PostMapping("/toggle-all")
	public String toggleAll(@RequestParam boolean completed, @RequestParam @Nullable Filter filter) {
		this.repository.updateAllCompleted(completed);
		return redirect(filter);
	}

	@PostMapping("/clear-completed")
	public String clearCompleted(@RequestParam @Nullable Filter filter) {
		this.repository.deleteAllCompleted();
		return redirect(filter);
	}

	private static String redirect(@Nullable Filter filter) {
		return "redirect:" + (filter != null ? filter : Filter.ALL).getPath();
	}
}
