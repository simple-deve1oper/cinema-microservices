package dev.dictionary.participant.service.impl;

import dev.dictionary.participant.entity.Participant;
import dev.dictionary.participant.mapper.ParticipantMapper;
import dev.dictionary.participant.repository.ParticipantRepository;
import dev.dictionary.participant.service.ParticipantService;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Сервис, реализующий интерфейс {@link ParticipantService}
 */
@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository repository;
    private final ParticipantMapper mapper;

    @Value("${errors.participant.id.not-found}")
    private String errorParticipantIdNotFound;

    @Override
    public List<ParticipantResponse> getAll() {
        List<Participant> participants = repository.findAll();

        return participants.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ParticipantResponse getById(Long id) {
        Participant participant = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorParticipantIdNotFound.formatted(id)));

        return mapper.toResponse(participant);
    }

    @Override
    public List<ParticipantResponse> getAllByIds(Set<Long> ids) {
        List<Participant> participants = repository.findAllById(ids);

        return participants.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<Long> getNonExistentIds(Set<Long> ids) {
        List<Long> existentIds = repository.findExistentIds(ids);

        return ids.stream()
                .filter(id -> !existentIds.contains(id))
                .toList();
    }
}
