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

import com.toedter.spring.hateoas.jsonapi.JsonApiMediaTypeConfiguration;
import java.util.List;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.hateoas.server.mvc.TypeConstrainedJacksonJsonHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import tools.jackson.databind.json.JsonMapper;

class MovieRepositoryRestConfigurer implements RepositoryRestConfigurer {

  private final JsonApiMediaTypeConfiguration mappingInfo;
  private final JsonMapper mapper;

  MovieRepositoryRestConfigurer(
    JsonApiMediaTypeConfiguration mappingInfo,
    JsonMapper mapper
  ) {
    this.mappingInfo = mappingInfo;
    this.mapper = mapper;
  }

  @Override
  public void configureRepositoryRestConfiguration(
    RepositoryRestConfiguration config,
    CorsRegistry cors
  ) {
    config.exposeIdsFor(Movie.class);
    config.useHalAsDefaultJsonMediaType(false);
  }

  @Override
  public void configureHttpMessageConverters(
    List<HttpMessageConverter<?>> messageConverters
  ) {
    messageConverters.add(
      0,
      new TypeConstrainedJacksonJsonHttpMessageConverter(
        mappingInfo.getRootType(),
        mappingInfo.getMediaTypes(),
        mappingInfo.configureJsonMapper(mapper.rebuild()).build()
      )
    );
  }
}
