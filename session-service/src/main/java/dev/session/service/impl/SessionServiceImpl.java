package dev.session.service.impl;

import dev.library.core.exception.EntityAlreadyExistsException;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.core.specification.SpecificationBuilder;
import dev.library.core.util.DateUtil;
import dev.library.core.util.ReflectionUtils;
import dev.library.domain.movie.client.MovieClient;
import dev.library.domain.session.dto.SessionSearchRequest;
import dev.library.domain.session.dto.SessionRequest;
import dev.library.domain.session.dto.SessionResponse;
import dev.library.domain.session.dto.constant.MovieFormat;
import dev.session.entity.Session;
import dev.session.mapper.SessionMapper;
import dev.session.repository.SessionRepository;
import dev.session.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

/**
 * Сервис, реализующий интерфейс {@link SessionService}
 */
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository repository;
    private final SessionMapper mapper;
    private final SpecificationBuilder<Session> specificationBuilder;
    private final MovieClient movieClient;

    @Value("${errors.session.id.not-found}")
    private String errorSessionIdNotFound;
    @Value("${errors.session.hall-date-time.already-exists}")
    private String errorSessionHallAndDateTimeAlreadyExists;
    @Value("${errors.session.movie-id.not-found}")
    private String errorSessionMovieIdNotFound;

    @Override
    public List<SessionResponse> getAll(SessionSearchRequest searchRequest) {
        Specification<Session> specification = getSpecification(searchRequest);
        List<Session> sessions = repository.findAll(specification);

        return sessions.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public SessionResponse getById(Long id) {
        Session session = findById(id);

        return mapper.toResponse(session);
    }

    @Override
    @Transactional
    public SessionResponse create(SessionRequest request) {
        checkNotExistsByMovieId(request.movieId());
        checkExistsByHallAndDateTime(request.hall(), request.dateTime());
        Session session = mapper.toEntity(request);
        session = repository.save(session);

        return mapper.toResponse(session);
    }

    @Override
    @Transactional
    public SessionResponse update(Long id, SessionRequest request) {
        Session session = findById(id);
        replaceData(session, request);
        repository.save(session);

        return mapper.toResponse(session);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            String errorMessage = errorSessionIdNotFound.formatted(id);
            throw new EntityNotFoundException(errorMessage);
        }
        repository.deleteById(id);
    }

    @Override
    public Session findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorSessionIdNotFound.formatted(id)));
    }

    /**
     * Обновление данных записи о сеансе
     * @param session - объект типа {@link Session}
     * @param request - - объект типа {@link SessionRequest}
     */
    private void replaceData(Session session, SessionRequest request) {
        Long requestMovieId = request.movieId();
        if (!requestMovieId.equals(session.getMovieId())) {
            checkNotExistsByMovieId(requestMovieId);
            session.setMovieId(requestMovieId);
        }
        MovieFormat requestMovieFormat = request.movieFormat();
        if (!requestMovieFormat.equals(session.getMovieFormat())) {
            session.setMovieFormat(requestMovieFormat);
        }
        Integer requestHall = request.hall();
        OffsetDateTime requestDateTime = request.dateTime();
        if (!requestHall.equals(session.getHall()) || !requestDateTime.equals(session.getDateTime())) {
            checkExistsByHallAndDateTime(requestHall, requestDateTime);
            if (!requestHall.equals(session.getHall())) {
                session.setHall(requestHall);
            }
            if (!requestDateTime.equals(session.getDateTime())) {
                session.setDateTime(requestDateTime);
            }
        }
        Boolean requestAvailable = request.available();
        if (!requestAvailable.equals(session.getAvailable())) {
            session.setAvailable(requestAvailable);
        }
    }

    /**
     * Проверка на то, что не существует фильма по переданному идентификатору фильма
     * @param movieId - идентификатор фильма
     */
    private void checkNotExistsByMovieId(Long movieId) {
        boolean existsMovieById = movieClient.existsById(movieId);
        if (!existsMovieById) {
            String errorMessage = errorSessionMovieIdNotFound.formatted(movieId);
            throw new EntityNotFoundException(errorMessage);
        }
    }

    /**
     * Проверка, что существует запись о сеансе с таким же номером зала и датой с временем
     * @param hall - номер зала
     * @param dateTime - дата и время
     */
    private void checkExistsByHallAndDateTime(int hall, OffsetDateTime dateTime) {
        boolean result = repository.existsByHallAndDateTime(hall, dateTime);
        if (result) {
            String errorMessage = errorSessionHallAndDateTimeAlreadyExists
                    .formatted(hall, DateUtil.formatDate(dateTime));
            throw new EntityAlreadyExistsException(errorMessage);
        }
    }

    /**
     * Получение Specification для фильтрации данных при получении всех записей о сеансах
     * @param searchDto - объект типа {@link SessionSearchRequest}
     */
    private Specification<Session> getSpecification(SessionSearchRequest searchDto) {
        Specification<Session> specification = specificationBuilder.emptySpecification();
        if (ReflectionUtils.allFieldsIsNull(searchDto)) {
            return specification;
        }
        if (Objects.nonNull(searchDto.getMovieId())) {
            Long valueMovieId = searchDto.getMovieId();
            String fieldMovieId = ReflectionUtils.getFieldName(searchDto, valueMovieId).orElseThrow();
            specification = specification.and(
                    specificationBuilder.equal(fieldMovieId, valueMovieId)
            );
        }
        if (Objects.nonNull(searchDto.getDate())) {
            LocalDate date = searchDto.getDate();
            OffsetDateTime from = OffsetDateTime.of(date, LocalTime.MIN, ZoneOffset.UTC);
            OffsetDateTime to = OffsetDateTime.of(date, LocalTime.MAX, ZoneOffset.UTC);
            specification = specification.and(
                    specificationBuilder.between("dateTime", from, to)
            );
        }

        return specification;
    }
}
