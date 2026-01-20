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

/**
 * Catalog module containing the Movie aggregate and related value objects.
 *
 * This module demonstrates:
 * - Movie aggregate root with value object identifier (MovieId)
 * - Domain-specific value objects (ImdbId, Rating)
 * - Cross-module references to person module using DirectorId
 */
@org.springframework.modulith.ApplicationModule
package com.toedter.spring.hateoas.jsonapi.examples.jmolecules.movie;
