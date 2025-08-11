package dev.dictionary.country.repository;

import dev.dictionary.country.entity.Country;
import dev.library.test.config.AbstractRepositoryTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
public class CountryRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private CountryRepository countryRepository;

    @Test
    void findByCode_ok() {
        Optional<Country> optionalCountry = countryRepository.findByCode("056");
        Assertions.assertTrue(optionalCountry.isPresent());

        Country country = optionalCountry.get();
        Assertions.assertEquals(4L, country.getId());
        Assertions.assertEquals("056", country.getCode());
        Assertions.assertEquals("Бельгия", country.getName());
        Assertions.assertEquals("flyway", country.getCreatedBy());
        Assertions.assertNotNull(country.getCreatedDate());
        Assertions.assertEquals("flyway", country.getUpdatedBy());
        Assertions.assertNotNull(country.getUpdatedDate());
    }

    @Test
    void findByCode_empty() {
        Optional<Country> optionalCountry = countryRepository.findByCode("001");
        Assertions.assertTrue(optionalCountry.isEmpty());
    }

    @Test
    void findAllByCodeIn_all() {
        List<String> codes = List.of("643", "276", "152");
        List<Country> countries = countryRepository.findAllByCodeIn(codes);
        Assertions.assertEquals(3, countries.size());
        Assertions.assertEquals("Россия", countries.get(0).getName());
        Assertions.assertEquals("Германия", countries.get(1).getName());
        Assertions.assertEquals("Чили", countries.get(2).getName());
    }

    @Test
    void findAllByCodeIn_some() {
        List<String> codes = List.of("643", "002", "392");
        List<Country> countries = countryRepository.findAllByCodeIn(codes);
        Assertions.assertEquals(2, countries.size());
        Assertions.assertEquals("Россия", countries.get(0).getName());
        Assertions.assertEquals("Япония", countries.get(1).getName());
    }

    @Test
    void findAllByCodeIn_empty() {
        List<String> codes = List.of("999", "002", "001");
        List<Country> countries = countryRepository.findAllByCodeIn(codes);
        Assertions.assertEquals(0, countries.size());
    }

    @Test
    void findExistentCodes_all() {
        List<String> codes = List.of("643", "276", "152");
        List<String> countryCodes = countryRepository.findExistentCodes(codes);
        Assertions.assertEquals(3, countryCodes.size());
        Assertions.assertEquals("643", countryCodes.get(0));
        Assertions.assertEquals("276", countryCodes.get(1));
        Assertions.assertEquals("152", countryCodes.get(2));
    }

    @Test
    void findExistentCodes_some() {
        List<String> codes = List.of("643", "002", "392");
        List<String> countryCodes = countryRepository.findExistentCodes(codes);
        Assertions.assertEquals(2, countryCodes.size());
        Assertions.assertEquals("643", countryCodes.get(0));
        Assertions.assertEquals("392", countryCodes.get(1));
    }

    @Test
    void findExistentCodes_empty() {
        List<String> codes = List.of("999", "002", "001");
        List<String> countryCodes = countryRepository.findExistentCodes(codes);
        Assertions.assertEquals(0, countryCodes.size());
    }
}
