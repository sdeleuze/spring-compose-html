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

import org.springframework.data.annotation.Id;

public record Todo(String title, boolean completed, @Id @Nullable Long id) {

	public static final int MAX_TITLE_LENGTH = 200;

	public Todo(String title) {
		this(title, false);
	}

	public Todo(String title, boolean completed) {
		this(title, completed, null);
	}

	public Todo withTitle(String title) {
		return new Todo(title, this.completed, this.id);
	}

	public Todo withCompleted(boolean completed) {
		return new Todo(this.title, completed, this.id);
	}

	/** Trimmed title limited to {@link #MAX_TITLE_LENGTH}, {@code null} when blank. */
	public static @Nullable String normalizeTitle(String title) {
		String trimmed = title.trim();
		if (trimmed.isEmpty()) {
			return null;
		}
		return trimmed.substring(0, Math.min(trimmed.length(), MAX_TITLE_LENGTH));
	}
}
