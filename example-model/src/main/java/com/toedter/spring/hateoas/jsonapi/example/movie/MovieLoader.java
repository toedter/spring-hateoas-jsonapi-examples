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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toedter.spring.hateoas.jsonapi.example.director.Director;
import com.toedter.spring.hateoas.jsonapi.example.director.DirectorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@Slf4j
public class MovieLoader {

    @Autowired
    public MovieLoader init(MovieRepository movieRepository, DirectorRepository directorRepository) throws Exception {
        String moviesJson;
        ObjectMapper mapper = new ObjectMapper();

        InputStream in = getClass().getResourceAsStream("/static/movie-data/movies-250.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        JsonNode rootNode = mapper.readValue(reader, JsonNode.class);

        JsonNode movies = rootNode.get("movies");
        int rating = 1;
        for (JsonNode movieNode : movies) {
            Movie movie = createMovie(rating++, movieNode);
            movieRepository.save(movie);

            String directors = movieNode.get("Director").asText();
            String[] directorList = directors.split(",");

            for (String directorName : directorList) {
                Director director = directorRepository.findByName(directorName.trim());
                if (director == null) {
                    director = new Director(directorName.trim());
                }
                director.addMovie(movie);
                directorRepository.save(director);
                movie.addDirector(director);
                movieRepository.save(movie);
            }
        }
        return this;
    }

    private Movie createMovie(int rank, JsonNode rootNode) {
        String title = rootNode.get("Title").asText();
        String imdbId = rootNode.get("imdbID").asText();

        long year = rootNode.get("Year").asLong();
        double imdbRating = rootNode.get("imdbRating").asDouble();

        String movieImage = "/static/movie-data/thumbs/" + imdbId + ".jpg";

        return new Movie(imdbId, title, year, imdbRating, rank, movieImage);
    }

    private String readFile(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

}
