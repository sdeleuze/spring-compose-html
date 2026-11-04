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

package example.counter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class CounterApplicationTests(@Autowired private val mockMvc: MockMvc) {

    @Test
    fun pageRendersCounterWithDefaultCount() {
        assertThat(render("/"))
            .startsWith("<!doctype html><html lang=\"en\">")
            .contains("<div data-compose-hydration-root=\"$COUNTER_ID\"><div class=\"card\"><output class=\"count\">0</output>")
            .contains(state("0"))
            .contains("<script type=\"module\" src=\"/app.js\"></script>")
    }

    @Test
    fun pageRendersCounterWithRequestCount() {
        assertThat(render("/?count=5"))
            .contains("<output class=\"count\">5</output>")
            .contains(state("5"))
    }

    @Test
    fun browserBundleIsServed() {
        this.mockMvc.get("/app.js").andExpect {
            status { isOk() }
        }
    }

    private fun render(uri: String): String = this.mockMvc.get(uri).andExpect {
        status { isOk() }
        content { contentTypeCompatibleWith(MediaType.TEXT_HTML) }
    }.andReturn().response.contentAsString

    private fun state(value: String) =
        "data-compose-hydration-for=\"$COUNTER_ID\" type=\"text/plain\">$value</script>"
}
