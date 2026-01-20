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

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jmolecules.ddd.types.AggregateRoot;

/**
 * Movie aggregate root demonstrating JModulith/JMolecules patterns.
 * Uses value object identifiers and demonstrates modular boundaries.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie implements AggregateRoot<Movie, MovieId> {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id"))
    private MovieId id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "title"))
    private MovieTitle title;

    private int year;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "imdb_id"))
    private ImdbId imdbId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "rating"))
    private Rating rating;

    private int rank;

    public Movie(MovieTitle title, int year, ImdbId imdbId, Rating rating, int rank) {
        this.id = MovieId.create();
        this.title = title;
        this.year = year;
        this.imdbId = imdbId;
        this.rating = rating;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "Movie: " + this.title;
    }
}
