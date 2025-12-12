/*
 * Copyright 2021 the original author or authors.
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

package com.toedter.spring.hateoas.jsonapi.examples;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API;
import static org.assertj.core.api.Assertions.assertThat;

import com.toedter.spring.hateoas.jsonapi.examples.director.DirectorRepository;
import com.toedter.spring.hateoas.jsonapi.examples.movie.Movie;
import com.toedter.spring.hateoas.jsonapi.examples.movie.MovieController;
import com.toedter.spring.hateoas.jsonapi.examples.movie.MovieRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * @author Kai Toedter
 */
@WebMvcTest(MovieController.class)
@ComponentScan("com.toedter.spring.hateoas.jsonapi")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("WebMvc Integration Test")
public class JsonApiWebMvcIntegrationTest {

  @Autowired
  private MockMvcTester mockMvcTester;

  @MockitoBean
  private MovieRepository movieRepository;

  @MockitoBean
  private DirectorRepository directorRepository;

  @Test
  void should_get_single_movie() {
    Movie movie = new Movie("12345", "Test Movie", 2020, 9.3, 17, null);
    movie.setId(1L);

    Mockito.when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

    var result = this.mockMvcTester.get()
      .uri("/api/movies/1")
      .accept(JSON_API)
      .exchange();

    assertThat(result).hasStatusOk();
    assertThat(result).bodyJson().extractingPath("$.jsonapi").isNotNull();
    assertThat(result).bodyJson().extractingPath("$.jsonapi.version").isEqualTo("1.1");
    assertThat(result).bodyJson().extractingPath("$.data.id").isEqualTo("1");
    assertThat(result).bodyJson().extractingPath("$.data.type").isEqualTo("movies");
    assertThat(result).bodyJson().extractingPath("$.data.attributes.title").isEqualTo("Test Movie");
    assertThat(result).bodyJson().extractingPath("$.data.attributes.year").isEqualTo(2020);
    assertThat(result).bodyJson().extractingPath("$.data.attributes.rating").isEqualTo(9.3);
    assertThat(result).bodyJson().extractingPath("$.links.self").isEqualTo("http://localhost/api/movies/1");
  }
}

