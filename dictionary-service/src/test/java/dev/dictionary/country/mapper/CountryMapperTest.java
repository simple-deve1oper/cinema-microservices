package dev.dictionary.country.mapper;

import dev.dictionary.country.entity.Country;
import dev.library.domain.dictionary.country.dto.CountryResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CountryMapperTest {
    final CountryMapper mapper = new CountryMapper();

    @Test
    void toResponse() {
        Country entity = Country.builder()
                .id(12L)
                .code("999")
                .name("Test")
                .build();

        CountryResponse response = mapper.toResponse(entity);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(entity.getId(), response.id());
        Assertions.assertEquals(entity.getCode(), response.code());
        Assertions.assertEquals(entity.getName(), response.name());
    }
}
