package dev.movie.service.impl;

import dev.library.core.exception.EntityNotFoundException;
import dev.library.core.specification.SpecificationBuilder;
import dev.library.core.util.ReflectionUtils;
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
import dev.movie.service.GenreService;
import dev.movie.service.MovieCountryService;
import dev.movie.service.MovieParticipantService;
import dev.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Сервис, реализующий интерфейс {@link MovieService}
 */
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository repository;
    private final MovieMapper mapper;
    private final GenreService genreService;
    private final MovieCountryService movieCountryService;
    private final MovieParticipantService movieParticipantService;
    private final SpecificationBuilder<Movie> specificationBuilder;

    @Value("${errors.movie.id.not-found}")
    private String errorMovieIdNotFound;

    @Override
    public List<MovieResponse> getAll(MovieSearchRequest searchRequest) {
        Specification<Movie> specification = getSpecification(searchRequest);
        List<Movie> movies = repository.findAll(specification);

        return movies.stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public MovieResponse getById(Long id) {
        Movie movie = findById(id);

        return buildResponse(movie);
    }

    @Override
    public Integer getDurationById(Long id) {
        return repository.findDurationById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMovieIdNotFound.formatted(id)));
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional
    public MovieResponse create(MovieRequest request) {
        Movie movie = mapper.toEntity(request);
        movie = repository.save(movie);
        List<Genre> genres = genreService.addForMovie(movie, request.genreIds());
        movie.setGenres(genres);
        List<MovieCountry> countries = movieCountryService.create(movie, request.countryCodes());
        movie.setCountries(countries);
        List<MovieParticipant> participants = movieParticipantService
                .create(movie, Map.of(Position.ACTOR, request.actorIds(), Position.DIRECTOR, request.directorIds()));
        movie.setParticipants(participants);

        return buildResponse(movie);
    }

    @Override
    @Transactional
    public MovieResponse update(Long id, MovieRequest request) {
        Movie movie = findById(id);
        replaceData(movie, request);
        genreService.updateForMovie(movie, request.genreIds());
        movieCountryService.update(movie, request.countryCodes());
        movieParticipantService.update(movie, Map.of(Position.ACTOR, request.actorIds(), Position.DIRECTOR, request.directorIds()));
        movie = repository.save(movie);

        return buildResponse(movie);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            String errorMessage = errorMovieIdNotFound.formatted(id);
            throw new EntityNotFoundException(errorMessage);
        }
        repository.deleteById(id);
    }

    @Override
    public MovieResponse buildResponse(Movie movie) {
        List<GenreResponse> genreResponses = genreService.getGenreResponsesByMovie(movie);
        List<CountryResponse> countryResponses = movieCountryService.getCountryResponsesByMovieId(movie.getCountries());
        List<MovieParticipant> participants = movie.getParticipants();
        List<MovieParticipant> directors = participants.stream()
                .filter(participant -> participant.getPosition() == Position.DIRECTOR)
                .toList();
        List<ParticipantResponse> directorResponses = movieParticipantService
                .getParticipantResponseByMovieIdAndPosition(directors);
        List<MovieParticipant> actors = participants.stream()
                .filter(participant -> participant.getPosition() == Position.ACTOR)
                .toList();
        List<ParticipantResponse> actorResponses = movieParticipantService
                .getParticipantResponseByMovieIdAndPosition(actors);

        return mapper.toResponse(movie, genreResponses, countryResponses, directorResponses, actorResponses);
    }

    private Movie findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMovieIdNotFound.formatted(id)));
    }

    /**
     * Замена данных записи о фильме
     * @param movie - объект типа {@link Movie}
     * @param request - объект типа {@link MovieRequest}
     */
    private void replaceData(Movie movie, MovieRequest request) {
        String requestName = request.name();
        if (!requestName.equals(movie.getName())) {
            movie.setName(requestName);
        }
        String requestDescription = request.description();
        if (!requestDescription.equals(movie.getDescription())) {
            movie.setDescription(requestDescription);
        }
        Integer requestDuration = request.duration();
        if (!requestDuration.equals(movie.getDuration())) {
            movie.setDuration(requestDuration);
        }
        Integer requestYear = request.year();
        if (!requestYear.equals(movie.getYear())) {
            movie.setYear(requestYear);
        }
        AgeRating requestAgeRating = request.ageRating();
        if (!requestAgeRating.equals(movie.getAgeRating())) {
            movie.setAgeRating(requestAgeRating);
        }
        Boolean requestRental = request.rental();
        if (!requestRental.equals(movie.getRental())) {
            movie.setRental(requestRental);
        }
    }

    /**
     * Получение Specification для фильтрации данных при получении всех записей о фильмах
     * @param searchDto - объект типа {@link MovieSearchRequest}
     */
    private Specification<Movie> getSpecification(MovieSearchRequest searchDto) {
        Specification<Movie> specification = specificationBuilder.emptySpecification();
        if (ReflectionUtils.allFieldsIsNull(searchDto)) {
            return specification;
        }
        if (Objects.nonNull(searchDto.getName())) {
            String valueName = searchDto.getName();
            String fieldName = ReflectionUtils.getFieldName(searchDto, valueName).orElseThrow();
            specification = specification.and(
                    specificationBuilder.like(fieldName, valueName)
            );
        }
        if (Objects.nonNull(searchDto.getYear())) {
            Integer valueYear = searchDto.getYear();
            String fieldNameYear = ReflectionUtils.getFieldName(searchDto, valueYear).orElseThrow();
            specification = specification.and(
                    specificationBuilder.equal(fieldNameYear, valueYear)
            );
        }
        if (Objects.nonNull(searchDto.getRental())) {
            Boolean valueRental= searchDto.getRental();
            String fieldNameRental = ReflectionUtils.getFieldName(searchDto, valueRental).orElseThrow();
            specification = specification.and(
                    specificationBuilder.equal(fieldNameRental, valueRental)
            );
        }

        return specification;
    }
}
