package dev.session.mapper;

import dev.library.domain.session.dto.PlaceRequest;
import dev.library.domain.session.dto.PlaceResponse;
import dev.session.entity.Place;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Класс для преобразования данных типа {@link Place}
 */
@Component
public class PlaceMapper {
    private final DecimalFormat decimalFormat;

    public PlaceMapper() {
        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormat = new DecimalFormat("#.00", decimalFormatSymbols);
    }

    /**
     * Преобразование данных в {@link PlaceResponse}
     * @param place - объект типа {@link Place}
     */
    public PlaceResponse toResponse(Place place) {
        return new PlaceResponse(
                place.getId(),
                place.getSession().getId(),
                place.getRow(),
                place.getNumber(),
                decimalFormat.format(place.getPrice()),
                place.getAvailable()
        );
    }

    /**
     * Преобразование данных в {@link Place}
     * @param request - объект типа {@link PlaceRequest}
     */
    public Place toEntity(PlaceRequest request) {
        return Place.builder()
                .row(request.row())
                .number(request.number())
                .price(request.price())
                .available(request.available())
                .build();
    }
}
