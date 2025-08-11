package dev.dictionary.country.service.impl;

import dev.dictionary.country.entity.Country;
import dev.dictionary.country.mapper.CountryMapper;
import dev.dictionary.country.repository.CountryRepository;
import dev.dictionary.country.service.CountryService;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.dictionary.country.dto.CountryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Сервис, реализующий интерфейс {@link CountryService}
 */
@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository repository;
    private final CountryMapper mapper;

    @Value("${errors.country.code.not-found}")
    private String errorCountryCodeNotFound;

    @Override
    public List<CountryResponse> getAll() {
        List<Country> countries = repository.findAll();

        return countries.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CountryResponse getByCode(String code) {
        Country country = repository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException(errorCountryCodeNotFound.formatted(code)));

        return mapper.toResponse(country);
    }

    @Override
    public List<CountryResponse> getAllByCodes(Set<String> codes) {
        List<Country> countries = repository.findAllByCodeIn(codes);

        return countries.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<String> getNonExistentCodes(Set<String> codes) {
        List<String> existentCodes = repository.findExistentCodes(codes);

        return codes.stream()
                .filter(code -> !existentCodes.contains(code))
                .toList();
    }
}
