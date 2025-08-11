package dev.movie.service;

import dev.library.core.exception.EntityNotFoundException;
import dev.library.core.specification.SpecificationBuilder;
import dev.library.domain.dictionary.country.dto.CountryResponse;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import dev.library.domain.movie.dto.GenreResponse;
import dev.library.domain.movie.dto.MovieRequest;
import dev.library.domain.movie.dto.MovieResponse;
import dev.library.domain.movie.dto.MovieSearchRequest;
import dev.library.domain.movie.dto.constant.AgeRating;
import dev.library.domain.movie.dto.constant.Position;
import dev.movie.entity.Genre;
import dev.movie.entity.Movie;
import dev.movie.entity.MovieCountry;
import dev.movie.entity.MovieParticipant;
import dev.movie.mapper.MovieMapper;
import dev.movie.repository.MovieRepository;
import dev.movie.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTest {
    final MovieRepository repository = Mockito.mock(MovieRepository.class);
    final MovieMapper mapper = new MovieMapper();
    final GenreService genreService = Mockito.mock(GenreService.class);
    final MovieCountryService movieCountryService = Mockito.mock(MovieCountryService.class);
    final MovieParticipantService movieParticipantService = Mockito.mock(MovieParticipantService.class);
    final SpecificationBuilder<Movie> specificationBuilder = new SpecificationBuilder<>();
    final MovieService service = new MovieServiceImpl(repository, mapper, genreService, movieCountryService,
            movieParticipantService, specificationBuilder);

    Movie entityMovieOne;
    Movie entityMovieTwo;

    Genre genreSport;
    Genre genreComedy;

    MovieCountry countryCanada;
    MovieCountry countryBelgium;

    MovieParticipant participantIvan;
    MovieParticipant participantAnton;

    GenreResponse genreSportResponse;
    GenreResponse genreComedyResponse;
    CountryResponse countryCanadaResponse;
    CountryResponse countryBelgiumResponse;
    ParticipantResponse participantDirectorResponse;
    ParticipantResponse participantActorResponse;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "errorMovieIdNotFound", "Фильм с идентификатором %d не найден");

        genreSport = Genre.builder()
                .id(1L)
                .name("Спортивные")
                .build();
        genreComedy = Genre.builder()
                .id(2L)
                .name("Комедии")
                .build();

        countryCanada = MovieCountry.builder()
                .id(1L)
                .countryCode("124")
                .build();
        countryBelgium = MovieCountry.builder()
                .id(2L)
                .countryCode("056")
                .build();

        participantIvan = MovieParticipant.builder()
                .id(13L)
                .participantId(12L)
                .position(Position.DIRECTOR)
                .build();
        participantAnton = MovieParticipant.builder()
                .id(14L)
                .participantId(13L)
                .position(Position.ACTOR)
                .build();

        entityMovieOne = Movie.builder()
                .id(1L)
                .name("Тест 1")
                .description("Тест 1")
                .duration(123)
                .year(2022)
                .ageRating(AgeRating.SIXTEEN)
                .rental(true)
                .genres(List.of(genreComedy))
                .countries(List.of(countryCanada))
                .participants(List.of(participantIvan, participantAnton))
                .build();

        entityMovieTwo = Movie.builder()
                .id(2L)
                .name("Тест 2")
                .description("Тест 2")
                .duration(56)
                .year(2024)
                .ageRating(AgeRating.ZERO)
                .rental(false)
                .genres(List.of(genreSport))
                .countries(List.of(countryBelgium))
                .participants(List.of(participantIvan, participantAnton))
                .build();

        genreSportResponse = new GenreResponse(2L, "Спортивные");
        genreComedyResponse = new GenreResponse(2L, "Комедии");
        countryCanadaResponse = new CountryResponse(5L, "124", "Канада");
        countryBelgiumResponse  = new CountryResponse(15L, "056", "Бельгия");
        participantDirectorResponse = new ParticipantResponse(44L, "Иванов", "Иван", "Иванович");
        participantActorResponse = new ParticipantResponse(48L, "Ложкин", "Антон", "Николаевич");
    }

    @Test
    void getAll_ok() {
        List<Movie> entities = List.of(entityMovieOne, entityMovieTwo);

        Mockito
                .when(repository.findAll(ArgumentMatchers.<Specification<Movie>>any()))
                .thenReturn(entities);

        List<MovieResponse> responses = service.getAll(new MovieSearchRequest());
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(2, responses.size());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll(ArgumentMatchers.<Specification<Movie>>any());
    }

    @Test
    void getAll_some() {
        List<Movie> entities = List.of(entityMovieTwo);

        Mockito
                .when(repository.findAll(ArgumentMatchers.<Specification<Movie>>any()))
                .thenReturn(entities);

        List<MovieResponse> responses = service.getAll(
                new MovieSearchRequest(
                        "Тест", 2024, false
                )
        );
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(1, responses.size());
        Assertions.assertEquals("Тест 2", responses.getFirst().name());
        Assertions.assertEquals(2024, responses.getFirst().year());
        Assertions.assertEquals(false, responses.getFirst().rental());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll(ArgumentMatchers.<Specification<Movie>>any());
    }

    @Test
    void getAll_empty() {
        Mockito
                .when(repository.findAll(ArgumentMatchers.<Specification<Movie>>any()))
                .thenReturn(Collections.emptyList());

        List<MovieResponse> responses = service.getAll(
                new MovieSearchRequest(
                        "Тест 999", 2067, true
                )
        );
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.isEmpty());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll(ArgumentMatchers.<Specification<Movie>>any());
    }

    @Test
    void getById_ok() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entityMovieOne));
        Mockito
                .when(genreService.getGenreResponsesByMovie(Mockito.any(Movie.class)))
                .thenReturn(List.of(genreComedyResponse));
        Mockito
                .when(movieCountryService.getCountryResponsesByMovieId(Mockito.anyList()))
                .thenReturn(List.of(countryCanadaResponse));
        Mockito
                .when(movieParticipantService.getParticipantResponseByMovieIdAndPosition(Mockito.anyList()))
                .thenReturn(List.of(participantDirectorResponse))
                .thenReturn(List.of(participantActorResponse));

        MovieResponse response = service.getById(1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.id());
        Assertions.assertEquals("Тест 1", response.name());
        Assertions.assertEquals("Тест 1", response.description());
        Assertions.assertEquals(123, response.duration());
        Assertions.assertEquals(2022, response.year());
        Assertions.assertEquals(AgeRating.SIXTEEN.getValue(), response.ageRating());
        Assertions.assertEquals(true, response.rental());
        Assertions.assertNotNull(response.genres());
        Assertions.assertEquals(1, response.genres().size());
        Assertions.assertNotNull(response.countries());
        Assertions.assertEquals(1, response.countries().size());
        Assertions.assertNotNull(response.directors());
        Assertions.assertEquals(1, response.directors().size());
        Assertions.assertNotNull(response.actors());
        Assertions.assertEquals(1, response.actors().size());

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(genreService, Mockito.times(1))
                .getGenreResponsesByMovie(Mockito.any(Movie.class));
        Mockito
                .verify(movieCountryService, Mockito.times(1))
                .getCountryResponsesByMovieId(Mockito.anyList());
        Mockito
                .verify(movieParticipantService, Mockito.times(2))
                .getParticipantResponseByMovieIdAndPosition(Mockito.anyList());
    }

    @Test
    void getById_entityNotFoundException() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.getById(123456L)
                );
        var expectedMessage = "Фильм с идентификатором 123456 не найден";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
    }

    @Test
    void getDurationById_ok() {
        Mockito
                .when(repository.findDurationById(Mockito.anyLong()))
                .thenReturn(Optional.of(123));

        Integer duration = service.getDurationById(1L);
        Assertions.assertNotNull(duration);
        Assertions.assertEquals(123, duration);

        Mockito
                .verify(repository, Mockito.times(1))
                .findDurationById(Mockito.anyLong());
    }

    @Test
    void getDurationById_entityNotFoundException() {
        Mockito
                .when(repository.findDurationById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.getDurationById(123456L)
                );
        var expectedMessage = "Фильм с идентификатором 123456 не найден";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findDurationById(Mockito.anyLong());
    }

    @Test
    void existsById_true() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        boolean exists = service.existsById(1L);
        Assertions.assertTrue(exists);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
    }

    @Test
    void existsById_false() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        boolean exists = service.existsById(123456L);
        Assertions.assertFalse(exists);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
    }

    @Test
    void create() {
        Movie entity = Movie.builder()
                .id(1L)
                .name("Тест 1")
                .description("Тест 1")
                .duration(123)
                .year(2022)
                .ageRating(AgeRating.SIXTEEN)
                .rental(true)
                .genres(List.of(genreComedy))
                .countries(List.of(countryCanada))
                .participants(List.of(participantIvan, participantAnton))
                .build();

        Mockito
                .when(repository.save(Mockito.any(Movie.class)))
                .thenReturn(entity);
        Mockito
                .when(genreService.addForMovie(Mockito.any(Movie.class), Mockito.anySet()))
                .thenReturn(List.of(genreComedy));
        Mockito
                .when(movieCountryService.create(Mockito.any(Movie.class), Mockito.anySet()))
                .thenReturn(List.of(countryCanada));
        Mockito
                .when(movieParticipantService.create(Mockito.any(Movie.class), Mockito.anyMap()))
                .thenReturn(List.of(participantIvan, participantAnton));
        Mockito
                .when(genreService.getGenreResponsesByMovie(Mockito.any(Movie.class)))
                .thenReturn(List.of(genreComedyResponse));
        Mockito
                .when(movieCountryService.getCountryResponsesByMovieId(Mockito.anyList()))
                .thenReturn(List.of(countryCanadaResponse));
        Mockito
                .when(movieParticipantService.getParticipantResponseByMovieIdAndPosition(Mockito.anyList()))
                .thenReturn(List.of(participantDirectorResponse))
                .thenReturn(List.of(participantActorResponse));

        MovieRequest request = new MovieRequest(
                "Тест 1",
                "Тест 1",
                123,
                2022,
                AgeRating.SIXTEEN,
                true,
                Set.of(2L),
                Set.of("124"),
                Set.of(13L),
                Set.of(14L)
        );
        MovieResponse response = service.create(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.id());
        Assertions.assertEquals("Тест 1", response.name());
        Assertions.assertEquals("Тест 1", response.description());
        Assertions.assertEquals(123, response.duration());
        Assertions.assertEquals(2022, response.year());
        Assertions.assertEquals(AgeRating.SIXTEEN.getValue(), response.ageRating());
        Assertions.assertEquals(true, response.rental());
        Assertions.assertNotNull(response.genres());
        Assertions.assertEquals(1, response.genres().size());
        Assertions.assertNotNull(response.countries());
        Assertions.assertEquals(1, response.countries().size());
        Assertions.assertNotNull(response.directors());
        Assertions.assertEquals(1, response.directors().size());
        Assertions.assertNotNull(response.actors());
        Assertions.assertEquals(1, response.actors().size());

        Mockito
                .verify(repository, Mockito.times(1))
                .save(Mockito.any(Movie.class));
        Mockito
                .verify(genreService, Mockito.times(1))
                .addForMovie(Mockito.any(Movie.class), Mockito.anySet());
        Mockito
                .verify(movieCountryService, Mockito.times(1))
                .create(Mockito.any(Movie.class), Mockito.anySet());
        Mockito
                .verify(movieParticipantService, Mockito.times(1))
                .create(Mockito.any(Movie.class), Mockito.anyMap());
        Mockito
                .verify(genreService, Mockito.times(1))
                .getGenreResponsesByMovie(Mockito.any(Movie.class));
        Mockito
                .verify(movieCountryService, Mockito.times(1))
                .getCountryResponsesByMovieId(Mockito.anyList());
        Mockito
                .verify(movieParticipantService, Mockito.times(2))
                .getParticipantResponseByMovieIdAndPosition(Mockito.anyList());
    }

    @Test
    void update_ok() {
        Movie entity = Movie.builder()
                .id(2L)
                .name("Тест 2")
                .description("Тест 2")
                .duration(101)
                .year(2221)
                .ageRating(AgeRating.EIGHTEEN)
                .rental(true)
                .genres(List.of(genreComedy))
                .countries(List.of(countryCanada))
                .participants(List.of(participantIvan, participantAnton))
                .build();

        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entityMovieTwo));
        Mockito
                .doNothing()
                .when(genreService)
                .updateForMovie(Mockito.any(Movie.class), Mockito.anySet());
        Mockito
                .doNothing()
                .when(movieCountryService)
                .update(Mockito.any(Movie.class), Mockito.anySet());
        Mockito
                .doNothing()
                .when(movieParticipantService)
                .update(Mockito.any(Movie.class), Mockito.anyMap());
        Mockito
                .when(repository.save(Mockito.any(Movie.class)))
                .thenReturn(entity);
        Mockito
                .when(genreService.getGenreResponsesByMovie(Mockito.any(Movie.class)))
                .thenReturn(List.of(genreComedyResponse));
        Mockito
                .when(movieCountryService.getCountryResponsesByMovieId(Mockito.anyList()))
                .thenReturn(List.of(countryCanadaResponse));
        Mockito
                .when(movieParticipantService.getParticipantResponseByMovieIdAndPosition(Mockito.anyList()))
                .thenReturn(List.of(participantDirectorResponse))
                .thenReturn(List.of(participantActorResponse));

        MovieRequest request = new MovieRequest(
                "Тест 2",
                "Тест 2",
                101,
                2221,
                AgeRating.EIGHTEEN,
                true,
                Set.of(2L),
                Set.of("124"),
                Set.of(),
                Set.of()
        );
        MovieResponse response = service.update(2L, request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.id());
        Assertions.assertEquals("Тест 2", response.name());
        Assertions.assertEquals("Тест 2", response.description());
        Assertions.assertEquals(101, response.duration());
        Assertions.assertEquals(2221, response.year());
        Assertions.assertEquals(AgeRating.EIGHTEEN.getValue(), response.ageRating());
        Assertions.assertEquals(true, response.rental());
        Assertions.assertNotNull(response.genres());
        Assertions.assertEquals(1, response.genres().size());
        Assertions.assertEquals("Комедии", response.genres().getFirst().name());
        Assertions.assertNotNull(response.countries());
        Assertions.assertEquals(1, response.countries().size());
        Assertions.assertNotNull(response.directors());
        Assertions.assertEquals(1, response.directors().size());
        Assertions.assertNotNull(response.actors());
        Assertions.assertEquals(1, response.actors().size());

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(genreService, Mockito.times(1))
                .updateForMovie(Mockito.any(Movie.class), Mockito.anySet());
        Mockito
                .verify(movieCountryService, Mockito.times(1))
                .update(Mockito.any(Movie.class), Mockito.anySet());
        Mockito
                .verify(movieParticipantService, Mockito.times(1))
                .update(Mockito.any(Movie.class), Mockito.anyMap());
        Mockito
                .verify(repository, Mockito.times(1))
                .save(Mockito.any(Movie.class));
        Mockito
                .verify(genreService, Mockito.times(1))
                .getGenreResponsesByMovie(Mockito.any(Movie.class));
        Mockito
                .verify(movieCountryService, Mockito.times(1))
                .getCountryResponsesByMovieId(Mockito.anyList());
        Mockito
                .verify(movieParticipantService, Mockito.times(2))
                .getParticipantResponseByMovieIdAndPosition(Mockito.anyList());
    }

    @Test
    void update_entityNotFoundException() {
        MovieRequest request = new MovieRequest(
                "Тест 2",
                "Тест 2",
                101,
                2221,
                AgeRating.EIGHTEEN,
                true,
                Set.of(2L),
                Set.of("124"),
                Set.of(),
                Set.of()
        );

        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.update(123456L, request)
                );
        var expectedMessage = "Фильм с идентификатором 123456 не найден";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(genreService, Mockito.times(0))
                .updateForMovie(Mockito.any(Movie.class), Mockito.anySet());
        Mockito
                .verify(movieCountryService, Mockito.times(0))
                .update(Mockito.any(Movie.class), Mockito.anySet());
        Mockito
                .verify(movieParticipantService, Mockito.times(0))
                .update(Mockito.any(Movie.class), Mockito.anyMap());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Movie.class));
    }

    @Test
    void deleteById_ok() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .doNothing()
                .when(repository)
                .deleteById(Mockito.anyLong());

        service.deleteById(1L);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .deleteById(Mockito.anyLong());
    }

    @Test
    void deleteById_entityNotFoundException() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.deleteById(123456L)
                );
        var expectedMessage = "Фильм с идентификатором 123456 не найден";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(0))
                .deleteById(Mockito.anyLong());
    }

    @Test
    void buildResponse() {
        Mockito
                .when(genreService.getGenreResponsesByMovie(Mockito.any(Movie.class)))
                .thenReturn(List.of(genreComedyResponse));
        Mockito
                .when(movieCountryService.getCountryResponsesByMovieId(Mockito.anyList()))
                .thenReturn(List.of(countryCanadaResponse));
        Mockito
                .when(movieParticipantService.getParticipantResponseByMovieIdAndPosition(Mockito.anyList()))
                .thenReturn(List.of(participantDirectorResponse))
                .thenReturn(List.of(participantActorResponse));


        MovieResponse response = service.buildResponse(entityMovieOne);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.id());
        Assertions.assertEquals("Тест 1", response.name());
        Assertions.assertEquals("Тест 1", response.description());
        Assertions.assertEquals(123, response.duration());
        Assertions.assertEquals(2022, response.year());
        Assertions.assertEquals("16+", response.ageRating());
        Assertions.assertEquals(true, response.rental());
        Assertions.assertNotNull(response.genres());
        Assertions.assertEquals(1, response.genres().size());
        Assertions.assertNotNull(response.countries());
        Assertions.assertEquals(1, response.countries().size());
        Assertions.assertNotNull(response.directors());
        Assertions.assertEquals(1, response.directors().size());
        Assertions.assertNotNull(response.actors());
        Assertions.assertEquals(1, response.actors().size());

        Mockito
                .verify(genreService, Mockito.times(1))
                .getGenreResponsesByMovie(Mockito.any(Movie.class));
        Mockito
                .verify(movieCountryService, Mockito.times(1))
                .getCountryResponsesByMovieId(Mockito.anyList());
        Mockito
                .verify(movieParticipantService, Mockito.times(2))
                .getParticipantResponseByMovieIdAndPosition(Mockito.anyList());
    }
}
