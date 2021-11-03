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

package com.toedter.spring.hateoas.jsonapi.example.movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toedter.spring.hateoas.jsonapi.JsonApiMediaTypeConfiguration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.hateoas.server.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.List;

class MovieRepositoryRestConfigurer implements RepositoryRestConfigurer {

    private final JsonApiMediaTypeConfiguration mappingInfo;
    private final ObjectMapper mapper;

    MovieRepositoryRestConfigurer(JsonApiMediaTypeConfiguration mappingInfo, ObjectMapper mapper) {
        this.mappingInfo = mappingInfo;
        this.mapper = mapper;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(Movie.class);
        config.useHalAsDefaultJsonMediaType(false);
    }

    @Override
    public void configureHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        messageConverters.add(0, new TypeConstrainedMappingJackson2HttpMessageConverter(
            mappingInfo.getRootType(), mappingInfo.getMediaTypes(), mappingInfo.configureObjectMapper(mapper)));
    }
}
