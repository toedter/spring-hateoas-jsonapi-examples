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

package com.toedter.spring.hateoas.jsonapi.examples.director;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.toedter.spring.hateoas.jsonapi.examples.RootController;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = RootController.API_BASE_PATH, produces = JSON_API_VALUE)
public class DirectorController {

  private final DirectorRepository repository;
  private final DirectorModelAssembler directorModelAssembler;

  DirectorController(
    DirectorRepository repository,
    DirectorModelAssembler directorModelAssembler
  ) {
    this.repository = repository;
    this.directorModelAssembler = directorModelAssembler;
  }

  @GetMapping("/directors")
  public ResponseEntity<PagedModel<EntityModel<Director>>> findAll(
    @RequestParam(
      value = "page[number]",
      defaultValue = "0",
      required = false
    ) int pageNumber,
    @RequestParam(
      value = "page[size]",
      defaultValue = "10",
      required = false
    ) int pageSize
  ) {
    final PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

    final Page<Director> pagedResult = repository.findAll(pageRequest);

    List<EntityModel<Director>> directorResources = StreamSupport.stream(
      pagedResult.spliterator(),
      false
    )
      .map(directorModelAssembler::toModel)
      .collect(Collectors.toList());

    Link selfLink = linkTo(DirectorController.class)
      .slash(
        "directors?page[number]=" +
          pagedResult.getNumber() +
          "&page[size]=" +
          pagedResult.getSize()
      )
      .withSelfRel();

    PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
      pagedResult.getSize(),
      pagedResult.getNumber(),
      pagedResult.getTotalElements(),
      pagedResult.getTotalPages()
    );
    final PagedModel<EntityModel<Director>> pagedModel = PagedModel.of(
      directorResources,
      pageMetadata,
      selfLink
    );

    return ResponseEntity.ok(pagedModel);
  }

  @GetMapping("/directors/{id}")
  public ResponseEntity<EntityModel<Director>> findOne(
    @PathVariable Long id,
    @RequestParam(
      value = "fields[directors]",
      required = false
    ) String[] fieldsDirectors
  ) {
    return repository
      .findById(id)
      .map(directorModelAssembler::toModel)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }
}
