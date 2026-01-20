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

package com.toedter.spring.hateoas.jsonapi.examples.jmolecules;

import com.toedter.spring.hateoas.jsonapi.examples.jmolecules.movie.ImdbId;
import com.toedter.spring.hateoas.jsonapi.examples.jmolecules.movie.Movie;
import com.toedter.spring.hateoas.jsonapi.examples.jmolecules.movie.MovieController;
import com.toedter.spring.hateoas.jsonapi.examples.jmolecules.movie.MovieId;
import com.toedter.spring.hateoas.jsonapi.examples.jmolecules.movie.MovieRepository;
import com.toedter.spring.hateoas.jsonapi.examples.jmolecules.movie.MovieTitle;
import com.toedter.spring.hateoas.jsonapi.examples.jmolecules.movie.Rating;
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

import java.util.Optional;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API;

/**
 * Integration test demonstrating JModulith value objects with JSON:API.
 */
@WebMvcTest(MovieController.class)
@ComponentScan("com.toedter.spring.hateoas.jsonapi")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("JModulith Integration Test")
class JModulithIntegrationTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private MovieRepository movieRepository;

    @Test
    void should_get_single_movie_with_value_object_id() {
        MovieId movieId = MovieId.of("test-movie-123");
        Movie movie = new Movie(
            MovieTitle.of("Inception"),
            2010,
            ImdbId.of("tt1375666"),
            Rating.of(8.8),
            13
        );
        movie.setId(movieId);

        Mockito.when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        var result = mockMvcTester.get()
            .uri("/api/movies/test-movie-123")
            .accept(JSON_API);

        result
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .hasPath("$.jsonapi")
            .extractingPath("$.jsonapi.version")
            .isEqualTo("1.1");

        // Verify value object ID is serialized correctly
        result.assertThat()
            .bodyJson()
            .extractingPath("$.data.id")
            .isEqualTo("test-movie-123");

        result
            .assertThat()
            .bodyJson()
            .extractingPath("$.data.type")
            .isEqualTo("movies");

        // Verify value objects are serialized as scalars, not nested objects
        result
            .assertThat()
            .bodyJson()
            .extractingPath("$.data.attributes.imdbId")
            .isEqualTo("tt1375666");

        result
            .assertThat()
            .bodyJson()
            .extractingPath("$.data.attributes.rating")
            .isEqualTo(8.8);

        result
            .assertThat()
            .bodyJson()
            .extractingPath("$.data.attributes.title")
            .isEqualTo("Inception");
    }

    @Test
    void should_get_movie_not_found_for_invalid_id() {
        MovieId movieId = MovieId.of("non-existent-id");
        Mockito.when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        var result = mockMvcTester.get()
            .uri("/api/movies/non-existent-id")
            .accept(JSON_API);

        result.assertThat().hasStatus(404);
    }
}
