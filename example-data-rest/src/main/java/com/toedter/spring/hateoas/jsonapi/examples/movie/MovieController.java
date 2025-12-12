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

package com.toedter.spring.hateoas.jsonapi.examples.movie;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import java.net.URI;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@BasePathAwareController
public class MovieController {

  private final MovieRepository movieRepository;
  private final RepositoryEntityLinks entityLinks;

  public MovieController(
    MovieRepository movieRepository,
    RepositoryEntityLinks entityLinks
  ) {
    this.movieRepository = movieRepository;
    this.entityLinks = entityLinks;
  }

  @PostMapping(value = "/movies", consumes = JSON_API_VALUE, produces = JSON_API_VALUE)
  public ResponseEntity<EntityModel<Movie>> createMovie(
    @RequestBody EntityModel<Movie> movieModel
  ) {
    Movie movie = movieModel.getContent();

    Movie savedMovie = movieRepository.save(movie);

    URI location = entityLinks.linkForItemResource(Movie.class, savedMovie.getId())
      .toUri();

    EntityModel<Movie> movieEntityModel = EntityModel.of(savedMovie);
    movieEntityModel.add(entityLinks.linkForItemResource(Movie.class, savedMovie.getId()).withSelfRel());

    return ResponseEntity.created(location).body(movieEntityModel);
  }
}

