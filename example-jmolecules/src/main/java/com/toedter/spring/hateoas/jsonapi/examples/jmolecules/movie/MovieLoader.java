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

package com.toedter.spring.hateoas.jsonapi.examples.jmolecules.movie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
@Slf4j
public class MovieLoader {

  @Autowired
  public MovieLoader init(
    MovieRepository movieRepository
  ) {
    ObjectMapper mapper = new ObjectMapper();

    InputStream in = getClass().getResourceAsStream(
      "/static/movie-data/movies-250.json"
    );
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

    JsonNode rootNode = mapper.readValue(reader, JsonNode.class);

    JsonNode movies = rootNode.get("movies");
    int rating = 1;
    for (JsonNode movieNode : movies) {
      Movie movie = createMovie(rating++, movieNode);
      movieRepository.save(movie);
    }
    return this;
  }

  private Movie createMovie(int rank, JsonNode rootNode) {
    String title = rootNode.get("Title").asString();
    String imdbId = rootNode.get("imdbID").asString();

    int year = rootNode.get("Year").asInt();
    double imdbRating = rootNode.get("imdbRating").asDouble();

    String movieImage = "/static/movie-data/thumbs/" + imdbId + ".jpg";

    return new Movie(MovieTitle.of(title), year, ImdbId.of(imdbId), Rating.of(imdbRating), rank);
  }
}
