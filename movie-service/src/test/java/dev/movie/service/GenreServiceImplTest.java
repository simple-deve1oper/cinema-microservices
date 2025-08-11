package dev.movie.service;

import dev.library.core.exception.BadRequestException;
import dev.library.domain.movie.dto.GenreResponse;
import dev.library.domain.movie.dto.constant.AgeRating;
import dev.movie.entity.Genre;
import dev.movie.entity.Movie;
import dev.movie.mapper.GenreMapper;
import dev.movie.repository.GenreRepository;
import dev.movie.service.impl.GenreServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class GenreServiceImplTest {
    final GenreRepository repository = Mockito.mock(GenreRepository.class);
    final GenreMapper mapper = new GenreMapper();
    final GenreService service = new GenreServiceImpl(repository, mapper);

    Genre entitySport;
    Genre entityComedy;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "errorGenreIdsNotFound", "Переданы несуществующие идентификаторы жанров: %s");

        entitySport = Genre.builder()
                .id(1L)
                .name("Спортивные")
                .movies(new ArrayList<>())
                .build();

        entityComedy = Genre.builder()
                .id(2L)
                .name("Комедии")
                .movies(new ArrayList<>())
                .build();
    }

    @Test
    void getAll_ok() {
        List<Genre> genres = List.of(entitySport, entityComedy);

        Mockito
                .when(repository.findAll())
                .thenReturn(genres);

        List<GenreResponse> responses = service.getAll();
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(2, responses.size());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll();
    }

    @Test
    void getAll_empty() {
        Mockito
                .when(repository.findAll())
                .thenReturn(Collections.emptyList());

        List<GenreResponse> responses = service.getAll();
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.isEmpty());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll();
    }

    @Test
    void create_ok() {
        Mockito
                .when(repository.findExistentIds(Mockito.anyIterable()))
                .thenReturn(List.of(1L, 2L));
        Mockito
                .when(repository.findAllById(Mockito.anyIterable()))
                .thenReturn(List.of(entitySport, entityComedy));
        Mockito
                .when(repository.saveAll(Mockito.anyIterable()))
                .thenReturn(List.of(entitySport, entityComedy));

        Set<Long> genreIds = Set.of(1L, 2L);
        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .build();
        List<Genre> genres = service.addForMovie(movie, genreIds);
        Assertions.assertNotNull(genres);
        Assertions.assertFalse(genres.isEmpty());
        Assertions.assertEquals(2, genres.size());
        Assertions.assertEquals(1, genres.get(0).getMovies().size());
        Assertions.assertEquals(movie, genres.get(0).getMovies().getFirst());
        Assertions.assertEquals(1, genres.get(1).getMovies().size());
        Assertions.assertEquals(movie, genres.get(1).getMovies().getFirst());

        Mockito
                .verify(repository, Mockito.times(1))
                .findExistentIds(Mockito.anyIterable());
        Mockito
                .verify(repository, Mockito.times(1))
                .findAllById(Mockito.anyIterable());
        Mockito
                .verify(repository, Mockito.times(1))
                .saveAll(Mockito.anyIterable());
    }

    @Test
    void create_badRequestException_checkNonExistentIds() {
        Mockito
                .when(repository.findExistentIds(Mockito.anyIterable()))
                .thenReturn(List.of(2L));

        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .build();
        Set<Long> genreIds = Set.of(1111L, 2L);
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.addForMovie(movie, genreIds)
                );
        var expectedMessage = "Переданы несуществующие идентификаторы жанров: [1111]";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findExistentIds(Mockito.anyIterable());
        Mockito
                .verify(repository, Mockito.times(0))
                .findAllById(Mockito.anyIterable());
        Mockito
                .verify(repository, Mockito.times(0))
                .saveAll(Mockito.anyIterable());
    }

    @Test
    void update_ok() {
        Mockito
                .when(repository.findExistentIds(Mockito.anyIterable()))
                .thenReturn(List.of(1L, 2L));

        List<Genre> oldGenres = new ArrayList<>();
        oldGenres.add(entitySport);
        oldGenres.add(entityComedy);
        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .genres(oldGenres)
                .build();
        Set<Long> genreIds = Set.of(1L, 2L);
        service.updateForMovie(movie, genreIds);

        Assertions.assertEquals(2, movie.getGenres().size());
        Assertions.assertEquals(1, movie.getGenres().get(0).getId());
        Assertions.assertEquals(2, movie.getGenres().get(1).getId());

        Mockito
                .verify(repository, Mockito.times(1))
                .findExistentIds(Mockito.anyIterable());
    }

    @Test
    void update_badRequestException_checkNonExistentIds() {
        Mockito
                .when(repository.findExistentIds(Mockito.anyIterable()))
                .thenReturn(List.of(2L));

        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .build();
        Set<Long> genreIds = Set.of(1111L, 2L);
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.updateForMovie(movie, genreIds)
                );
        var expectedMessage = "Переданы несуществующие идентификаторы жанров: [1111]";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findExistentIds(Mockito.anyIterable());
    }

    @Test
    void getGenreResponsesByMovie_ok() {
        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .genres(List.of(entitySport, entityComedy))
                .build();

        List<GenreResponse> responses = service.getGenreResponsesByMovie(movie);
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(2, responses.size());
        Assertions.assertEquals(1L, responses.get(0).id());
        Assertions.assertEquals("Спортивные", responses.get(0).name());
        Assertions.assertEquals(2L, responses.get(1).id());
        Assertions.assertEquals("Комедии", responses.get(1).name());
    }

    @Test
    void getGenreResponsesByMovie_empty() {
        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .genres(List.of())
                .build();

        List<GenreResponse> responses = service.getGenreResponsesByMovie(movie);
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.isEmpty());
    }
}
