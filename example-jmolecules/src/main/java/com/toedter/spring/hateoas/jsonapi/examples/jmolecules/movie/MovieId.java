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

import org.jmolecules.ddd.types.Identifier;

import java.io.Serializable;
import java.util.UUID;

/**
 * Value object identifier for Movie aggregate.
 * Demonstrates JMolecules Identifier pattern with JSON:API.
 */
public record MovieId(String value) implements Identifier, Serializable {

    public static MovieId of(String value) {
        return new MovieId(value);
    }

    public static MovieId create() {
        return new MovieId(UUID.randomUUID().toString());
    }
}
