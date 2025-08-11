package dev.movie.service;

import dev.library.core.exception.BadRequestException;
import dev.library.domain.dictionary.country.client.CountryClient;
import dev.library.domain.dictionary.country.dto.CountryResponse;
import dev.library.domain.movie.dto.constant.AgeRating;
import dev.movie.entity.Movie;
import dev.movie.entity.MovieCountry;
import dev.movie.mapper.MovieCountryMapper;
import dev.movie.repository.MovieCountryRepository;
import dev.movie.service.impl.MovieCountryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class MovieCountryServiceImplTest {
    final MovieCountryRepository repository = Mockito.mock(MovieCountryRepository.class);
    final MovieCountryMapper mapper = new MovieCountryMapper();
    final CountryClient countryClient = Mockito.mock(CountryClient.class);
    final MovieCountryService service = new MovieCountryServiceImpl(repository, mapper, countryClient);

    MovieCountry entityRussia;
    MovieCountry entityBelgium;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "countryCodesNotFound", "Переданы несуществующие коды стран: %s");

        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .build();

        entityRussia = MovieCountry.builder()
                .id(1L)
                .movie(movie)
                .countryCode("643")
                .build();

        entityBelgium = MovieCountry.builder()
                .id(2L)
                .movie(movie)
                .countryCode("056")
                .build();
    }

    @Test
    void create_ok() {
        Mockito
                .when(countryClient.getNonExistentCodes(Mockito.anySet()))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(repository.saveAll(Mockito.anyList()))
                .thenReturn(List.of(entityRussia, entityBelgium));

        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .build();
        Set<String> codes = Set.of("643", "056");
        List<MovieCountry> movieCountries = service.create(movie, codes);
        Assertions.assertNotNull(movieCountries);
        Assertions.assertFalse(movieCountries.isEmpty());
        Assertions.assertEquals(2, movieCountries.size());
        Assertions.assertEquals(1L, movieCountries.get(0).getId());
        Assertions.assertEquals("643", movieCountries.get(0).getCountryCode());
        Assertions.assertEquals(2L, movieCountries.get(1).getId());
        Assertions.assertEquals("056", movieCountries.get(1).getCountryCode());

        Mockito
                .verify(countryClient, Mockito.times(1))
                .getNonExistentCodes(Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(1))
                .saveAll(Mockito.anyList());
    }

    @Test
    void create_badRequestException_checkNonExistentCodes() {
        Mockito
                .when(countryClient.getNonExistentCodes(Mockito.anySet()))
                .thenReturn(List.of("999"));

        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .build();
        Set<String> codes = Set.of("643", "999");
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.create(movie, codes)
                );
        var expectedMessage = "Переданы несуществующие коды стран: [999]";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(countryClient, Mockito.times(1))
                .getNonExistentCodes(Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(0))
                .saveAll(Mockito.anyList());
    }

    @Test
    void update_ok() {
        Mockito
                .when(countryClient.getNonExistentCodes(Mockito.anySet()))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(repository.saveAll(Mockito.anyList()))
                .thenReturn(List.of(entityRussia));

        List<MovieCountry> oldMovieCountries = new ArrayList<>();
        oldMovieCountries.add(MovieCountry.builder()
                .id(56L)
                .countryCode("102")
                .build());
        oldMovieCountries.add(MovieCountry.builder()
                .id(57L)
                .countryCode("177")
                .build());
        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .countries(oldMovieCountries)
                .build();
        for (MovieCountry oldMovieCountry : oldMovieCountries) {
            oldMovieCountry.setMovie(movie);
        }
        Set<String> codes = Set.of("643", "177");
        service.update(movie, codes);
        Assertions.assertEquals(2, movie.getCountries().size());
        Assertions.assertEquals("177", movie.getCountries().get(0).getCountryCode());
        Assertions.assertEquals(57, movie.getCountries().get(0).getId());
        Assertions.assertEquals("643", movie.getCountries().get(1).getCountryCode());
        Assertions.assertEquals(1, movie.getCountries().get(1).getId());

        Mockito
                .verify(countryClient, Mockito.times(1))
                .getNonExistentCodes(Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(1))
                .saveAll(Mockito.anyList());
    }

    @Test
    void update_badRequestException_checkNonExistentCodes() {
        Mockito
                .when(countryClient.getNonExistentCodes(Mockito.anySet()))
                .thenReturn(List.of("999"));

        List<MovieCountry> oldMovieCountries = new ArrayList<>();
        oldMovieCountries.add(MovieCountry.builder()
                .id(56L)
                .countryCode("102")
                .build());
        oldMovieCountries.add(MovieCountry.builder()
                .id(57L)
                .countryCode("177")
                .build());
        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .countries(oldMovieCountries)
                .build();
        for (MovieCountry oldMovieCountry : oldMovieCountries) {
            oldMovieCountry.setMovie(movie);
        }
        Set<String> codes = Set.of("643", "177");
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.update(movie, codes)
                );
        var expectedMessage = "Переданы несуществующие коды стран: [999]";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(countryClient, Mockito.times(1))
                .getNonExistentCodes(Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(0))
                .saveAll(Mockito.anyList());
    }

    @Test
    void getCountryResponsesByMovieId() {
        List<CountryResponse> responses = List.of(
                new CountryResponse(3L, "643", "Россия"),
                new CountryResponse(4L, "056", "Бельгия")
        );

        Mockito
                .when(countryClient.getAllByCodes(Mockito.anySet()))
                .thenReturn(responses);

        List<CountryResponse> countryResponses = service.getCountryResponsesByMovieId(List.of(entityRussia, entityBelgium));
        Assertions.assertNotNull(countryResponses);
        Assertions.assertFalse(countryResponses.isEmpty());
        Assertions.assertEquals(2, countryResponses.size());
        Assertions.assertEquals("643", countryResponses.get(0).code());
        Assertions.assertEquals("Россия", countryResponses.get(0).name());
        Assertions.assertEquals("056", countryResponses.get(1).code());
        Assertions.assertEquals("Бельгия", countryResponses.get(1).name());

        Mockito
                .verify(countryClient, Mockito.times(1))
                .getAllByCodes(Mockito.anySet());
    }
}
