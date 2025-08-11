package dev.dictionary.country.service;

import dev.dictionary.country.entity.Country;
import dev.dictionary.country.mapper.CountryMapper;
import dev.dictionary.country.repository.CountryRepository;
import dev.dictionary.country.service.impl.CountryServiceImpl;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.dictionary.country.dto.CountryResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class CountryServiceImplTest {
    final CountryRepository repository = Mockito.mock(CountryRepository.class);
    final CountryMapper mapper = new CountryMapper();
    final CountryService service = new CountryServiceImpl(repository, mapper);

    Country entityBelgium;
    Country entityCanada;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "errorCountryCodeNotFound", "Страна с кодом %s не найдена");

        entityBelgium = Country.builder()
                .id(1L)
                .code("056")
                .name("Бельгия")
                .build();

        entityCanada = Country.builder()
                .id(2L)
                .code("124")
                .name("Канада")
                .build();
    }

    @Test
    void getAll_ok() {
        List<Country> countries = List.of(entityBelgium, entityCanada);

        Mockito
                .when(repository.findAll())
                .thenReturn(countries);

        List<CountryResponse> responses = service.getAll();
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

        List<CountryResponse> responses = service.getAll();
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.isEmpty());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll();
    }

    @Test
    void getByCode_ok() {
        Mockito
                .when(repository.findByCode(Mockito.anyString()))
                .thenReturn(Optional.of(entityCanada));

        CountryResponse responseCanada = service.getByCode("124");
        Assertions.assertNotNull(responseCanada);
        Assertions.assertEquals(2L, responseCanada.id());
        Assertions.assertEquals("124", responseCanada.code());
        Assertions.assertEquals("Канада", responseCanada.name());

        Mockito
                .verify(repository, Mockito.times(1))
                .findByCode(Mockito.anyString());
    }

    @Test
    void getByCode_notFound() {
        Mockito
                .when(repository.findByCode(Mockito.anyString()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.getByCode("111")
                );
        String expectedMessage = "Страна с кодом 111 не найдена";
        String actualMessage = exception.getApiError().message();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

        Mockito
                .verify(repository, Mockito.times(1))
                .findByCode(Mockito.anyString());
    }

    @Test
    void getAllByCodes_all() {
        List<Country> countries = List.of(entityBelgium, entityCanada);

        Mockito
                .when(repository.findAllByCodeIn(Mockito.anySet()))
                .thenReturn(countries);

        Set<String> codes = Set.of("056", "124");
        List<CountryResponse> countryResponses = service.getAllByCodes(codes);
        Assertions.assertEquals(2, countryResponses.size());
        Assertions.assertEquals("Бельгия", countryResponses.get(0).name());
        Assertions.assertEquals("Канада", countryResponses.get(1).name());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAllByCodeIn(Mockito.anySet());
    }

    @Test
    void getAllByCodes_some() {
        List<Country> countries = List.of(entityBelgium);

        Mockito
                .when(repository.findAllByCodeIn(Mockito.anySet()))
                .thenReturn(countries);

        Set<String> codes = Set.of("056", "001");
        List<CountryResponse> countryResponses = service.getAllByCodes(codes);
        Assertions.assertEquals(1, countryResponses.size());
        Assertions.assertEquals("Бельгия", countryResponses.getFirst().name());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAllByCodeIn(Mockito.anySet());
    }

    @Test
    void getAllByCodes_empty() {
        Mockito
                .when(repository.findAllByCodeIn(Mockito.anySet()))
                .thenReturn(Collections.emptyList());

        Set<String> codes = Set.of("002", "001");
        List<CountryResponse> countryResponses = service.getAllByCodes(codes);
        Assertions.assertEquals(0, countryResponses.size());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAllByCodeIn(Mockito.anySet());
    }

    @Test
    void getNonExistentCodes_all() {
        Mockito
                .when(repository.findExistentCodes(Mockito.anyIterable()))
                .thenReturn(Collections.emptyList());

        Set<String> codes = Set.of("002", "001");
        List<String> countryCodes = service.getNonExistentCodes(codes);
        Assertions.assertEquals(2, countryCodes.size());
        Assertions.assertTrue(countryCodes.contains("002"));
        Assertions.assertTrue(countryCodes.contains("001"));

        Mockito
                .verify(repository, Mockito.times(1))
                .findExistentCodes(Mockito.anyIterable());
    }

    @Test
    void getNonExistentCodes_some() {
        Mockito
                .when(repository.findExistentCodes(Mockito.anyIterable()))
                .thenReturn(List.of("056"));

        Set<String> codes = Set.of("056", "001");
        List<String> countryCodes = service.getNonExistentCodes(codes);
        Assertions.assertEquals(1, countryCodes.size());
        Assertions.assertEquals("001", countryCodes.getFirst());

        Mockito
                .verify(repository, Mockito.times(1))
                .findExistentCodes(Mockito.anyIterable());
    }

    @Test
    void getNonExistentCodes_empty() {
        Mockito
                .when(repository.findExistentCodes(Mockito.anyIterable()))
                .thenReturn(List.of("056", "124"));

        Set<String> codes = Set.of("056", "124");
        List<String> countryCodes = service.getNonExistentCodes(codes);
        Assertions.assertEquals(0, countryCodes.size());

        Mockito
                .verify(repository, Mockito.times(1))
                .findExistentCodes(Mockito.anyIterable());
    }
}
