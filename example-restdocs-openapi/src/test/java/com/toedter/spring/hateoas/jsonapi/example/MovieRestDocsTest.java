package com.toedter.spring.hateoas.jsonapi.example;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.toedter.spring.hateoas.jsonapi.example.movie.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
@ExtendWith(SpringExtension.class)
class MovieRestDocsTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  MovieRepository movieRepository;

  ResultActions resultActions;

  @Test
  void should_get_movies() throws Exception {
    resultActions = mockMvc.perform(get("/api/movies").accept(JSON_API_VALUE));

    resultActions
      .andExpect(status().isOk())
      .andDo(
        document(
          "movies-get",
          resource(
            ResourceSnippetParameters.builder()
              .description("Get movies")
              .responseFields(
                fieldWithPath("jsonapi.version").description(
                  "the JSON:API version"
                ),
                subsectionWithPath("data").description("an array of movies"),
                subsectionWithPath("links").description(
                  "links to next, previous, ... pages"
                ),
                subsectionWithPath("meta").description("pagination information")
              )
              .build()
          )
        )
      );
  }

  @Test
  void should_get_movie() throws Exception {
    resultActions = mockMvc.perform(
      get("/api/movies/{id}", 1).accept(JSON_API_VALUE)
    );

    resultActions
      .andExpect(status().isOk())
      .andDo(
        document(
          "movie-get",
          resource(
            ResourceSnippetParameters.builder()
              .description("Get a movie by id")
              .pathParameters(
                parameterWithName("id").description("the movie id")
              )
              .responseFields(
                fieldWithPath("jsonapi.version").description(
                  "the JSON:API version"
                ),
                fieldWithPath("data.id").description("the JSON:API id"),
                fieldWithPath("data.type").description("the JSON:API type"),
                fieldWithPath("data.attributes.title").description(
                  "the movie's title"
                ),
                fieldWithPath("data.attributes.year").description(
                  "the movie's release year"
                ),
                fieldWithPath("data.attributes.imdbId").description(
                  "the movie's Internet Movie Database (IMDB) Id"
                ),
                fieldWithPath("data.attributes.rating").description(
                  "the movie's IMDB rating"
                ),
                fieldWithPath("data.attributes.rank").description(
                  "the movie's IMDB rank"
                ),
                subsectionWithPath("data.relationships.directors").description(
                  "the movie's directors"
                ),
                subsectionWithPath("links.self").description(
                  "the movie's self link"
                )
              )
              .build()
          )
        )
      );
  }

  @Test
  void should_create_movie() throws Exception {
    String body =
      """
      {
        "data": {
          "id": "1",
          "type": "movies",
          "attributes": {
            "title": "NEW MOVIE"
          }
        }
      }""";

    resultActions = mockMvc.perform(
      post("/api/movies").contentType(JSON_API_VALUE).content(body)
    );

    resultActions
      .andExpect(status().isCreated())
      .andDo(
        document(
          "movie-create",
          resource(
            ResourceSnippetParameters.builder()
              .description("Creates a movie")
              .build()
          )
        )
      );
  }

  @Test
  void should_update_movie() throws Exception {
    String body =
      """
      {
        "data": {
          "id": "1",
          "type": "movies",
          "attributes": {
            "title": "PATCHED TITLE"
          }
        }
      }""";

    resultActions = mockMvc.perform(
      patch("/api/movies/1").contentType(JSON_API_VALUE).content(body)
    );

    resultActions
      .andExpect(status().isNoContent())
      .andDo(
        document(
          "movie-update",
          resource(
            ResourceSnippetParameters.builder()
              .description("Updates a movie")
              .build()
          )
        )
      );
  }
}
