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

import com.toedter.spring.hateoas.jsonapi.examples.jmolecules.RootController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

/**
 * REST controller for Movie aggregate.
 * Demonstrates JSON:API with JModulith value object identifiers.
 */
@RestController
@RequestMapping(value = RootController.API_BASE_PATH, produces = JSON_API_VALUE)
public class MovieController {

    private final MovieRepository repository;
    private final MovieModelAssembler movieModelAssembler;

    MovieController(
        MovieRepository repository,
        MovieModelAssembler movieModelAssembler
    ) {
        this.repository = repository;
        this.movieModelAssembler = movieModelAssembler;
    }

    @GetMapping("/movies")
    public ResponseEntity<PagedModel<EntityModel<Movie>>> findAll(
        @RequestParam(
            value = "page[number]",
            defaultValue = "0",
            required = false
        ) int page,
        @RequestParam(
            value = "page[size]",
            defaultValue = "10",
            required = false
        ) int size
    ) {
        final PageRequest pageRequest = PageRequest.of(page, size);

        final Page<Movie> pagedResult = repository.findAll(pageRequest);

        List<EntityModel<Movie>> movieResources = StreamSupport.stream(
            pagedResult.spliterator(),
            false
        )
            .map(movieModelAssembler::toModel)
            .collect(Collectors.toList());

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
            pagedResult.getSize(),
            pagedResult.getNumber(),
            pagedResult.getTotalElements(),
            pagedResult.getTotalPages()
        );

        final PagedModel<EntityModel<Movie>> pagedModel = PagedModel.of(
            movieResources,
            pageMetadata
        );

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping("/movies")
    public ResponseEntity<?> newMovie(@RequestBody EntityModel<Movie> movieModel) {
        Movie movie = movieModel.getContent();
        if (movie.getId() == null) {
            movie.setId(MovieId.create());
        }
        repository.save(movie);
        final RepresentationModel<?> movieRepresentationModel =
            movieModelAssembler.toModel(movie);

        return movieRepresentationModel
            .getLink(IanaLinkRelations.SELF)
            .map(Link::getHref)
            .map(href -> {
                try {
                    return new URI(href);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            })
            .map(uri -> ResponseEntity.created(uri).build())
            .orElse(ResponseEntity.badRequest().body("Unable to create " + movie));
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<? extends RepresentationModel<?>> findOne(
        @PathVariable MovieId id
    ) {
        System.out.println("Finding movie with id: " + id);
        return repository
            .findById(id)
            .map(movieModelAssembler::toModel)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable MovieId id) {
        repository.findById(id).ifPresent(repository::delete);
        return ResponseEntity.noContent().build();
    }
}
