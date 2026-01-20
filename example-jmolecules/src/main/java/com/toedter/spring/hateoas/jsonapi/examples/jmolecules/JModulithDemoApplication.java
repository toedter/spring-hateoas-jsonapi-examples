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

import com.toedter.spring.hateoas.jsonapi.JsonApiConfiguration;
import com.toedter.spring.hateoas.jsonapi.JsonApiObject;
import lombok.extern.slf4j.Slf4j;
import org.jmolecules.jackson3.JMoleculesModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Boot application demonstrating JModulith concepts with JSON:API.
 * <p>
 * This example showcases:
 * - Value object identifiers (MovieId, ImdbId, Rating)
 * - Aggregate roots (Movie)
 * - JSON:API integration with JMolecules annotations
 */
@SpringBootApplication
@Slf4j
public class JModulithDemoApplication implements WebMvcConfigurer {

    public static void main(String... args) {
        SpringApplication.run(JModulithDemoApplication.class, args);
    }

    @Bean
    public JsonApiConfiguration jsonApiConfiguration() {
        return new JsonApiConfiguration()
                .withJsonApiObject(
                        new JsonApiObject(true)
                )
                .withMapperCustomizer(builder ->
                        builder.addModule(new JMoleculesModule())
                );
    }
}
