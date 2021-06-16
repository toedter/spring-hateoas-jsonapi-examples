/*
 * Copyright 2020 the original author or authors.
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

package com.toedter.spring.hateoas.jsonapi.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toedter.spring.hateoas.jsonapi.MediaTypes;
import com.toedter.spring.hateoas.jsonapi.example.director.DirectorRepository;
import com.toedter.spring.hateoas.jsonapi.example.movie.Movie;
import com.toedter.spring.hateoas.jsonapi.example.movie.MovieRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Kai Toedter
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Spring Boot MockMvc Integration Test")
public class JsonApiSpringBootMockMvcIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieRepository movieRepository;

    @MockBean
    private DirectorRepository directorRepository;

    @Test
    void should_get_single_movie() throws Exception {

        Movie movie = new Movie("12345", "Test Movie", 2020, 9.3, 17, null);
        movie.setId(1L);

        Mockito.when(movieRepository.findById(1L))
                .thenReturn(Optional.of(movie));

        this.mockMvc
                .perform(get("/api/movies/1").accept(JSON_API))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jsonapi", is(not(empty()))))
                .andExpect(jsonPath("$.jsonapi.version", is("1.0")))
                .andExpect(jsonPath("$.data.id", is("1")))
                .andExpect(jsonPath("$.data.type", is("movies")))
                .andExpect(jsonPath("$.data.attributes.title", is("Test Movie")))
                .andExpect(jsonPath("$.data.attributes.year", is(2020)))
                .andExpect(jsonPath("$.data.attributes.rating", is(9.3)))
                .andExpect(jsonPath("$.links.self", is("http://localhost/api/movies/1")));
    }

    @Test
    @Disabled
    void should_not_create_movie() {

        Movie movie = new Movie("12345", "Test Movie", 2020, 9.3, 17, null);

        movie.setId(1L);
        Mockito.when(movieRepository.save(argThat(new MovieMatcher(movie))))
            .thenReturn(movie);

        assertThrows(NestedServletException.class,
            () -> this.mockMvc
                .perform(post("/api/movies")
                    .content(objectMapper.writeValueAsString(movie))
                    .contentType(MediaTypes.JSON_API_VALUE)
                    .accept(MediaTypes.JSON_API_VALUE)));
    }

    @Test
    @Disabled
    void should_create_movie() throws Exception {

        Movie movie = new Movie("12345", "Test Movie", 2020, 9.3, 17, null);
        Map<String, Object> data = new HashMap<>();
        data.put("type", "movies");
        data.put("attributes", objectMapper.convertValue(movie, new TypeReference<Map<String, Object>>() {
        }));
        Map<String, Object> request = new HashMap<>();
        request.put("data", data);

        movie.setId(1L);
        Mockito.when(movieRepository.save(argThat(new MovieMatcher(movie))))
            .thenReturn(movie);

        this.mockMvc
            .perform(post("/api/movies")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaTypes.JSON_API_VALUE)
                .accept(MediaTypes.JSON_API_VALUE))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.jsonapi", is(not(empty()))))
            .andExpect(jsonPath("$.jsonapi.version", is("1.0")))
            .andExpect(jsonPath("$.data.id", is("1")))
            .andExpect(jsonPath("$.data.type", is("movies")))
            .andExpect(jsonPath("$.data.attributes.title", is("Test Movie")))
            .andExpect(jsonPath("$.data.attributes.year", is(2020)))
            .andExpect(jsonPath("$.data.attributes.rating", is(9.3)))
            .andExpect(jsonPath("$.links.self", is(not(empty()))));
    }

    private static class MovieMatcher implements ArgumentMatcher<Movie> {

        private final Movie movie;

        public MovieMatcher(Movie movie) {
            this.movie = movie;
        }

        @Override
        public boolean matches(Movie argument) {
            if (argument == null) {
                return false;
            }
            return Objects.equals(movie.getTitle(), argument.getTitle()) &&
                Objects.equals(movie.getYear(), argument.getYear()) &&
                Objects.equals(movie.getImdbId(), argument.getImdbId()) &&
                Objects.equals(movie.getRank(), argument.getRank()) &&
                Objects.equals(movie.getThumb(), argument.getThumb());
        }
    }
}
