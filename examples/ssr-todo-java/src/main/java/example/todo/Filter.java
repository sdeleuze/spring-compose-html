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

import java.util.function.Predicate;

/** Todo list filters, each one with its own page. */
public enum Filter {

	ALL("All", "/", todo -> true),
	ACTIVE("Active", "/active", todo -> !todo.completed()),
	COMPLETED("Completed", "/completed", Todo::completed);

	private final String label;

	private final String path;

	private final Predicate<Todo> predicate;

	Filter(String label, String path, Predicate<Todo> predicate) {
		this.label = label;
		this.path = path;
		this.predicate = predicate;
	}

	public String getLabel() {
		return this.label;
	}

	public String getPath() {
		return this.path;
	}

	public boolean matches(Todo todo) {
		return this.predicate.test(todo);
	}
}
