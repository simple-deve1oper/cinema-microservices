package dev.receipt.service.impl;

import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.movie.dto.MovieResponse;
import dev.library.domain.session.dto.PlaceResponse;
import dev.library.domain.user.dto.UserResponse;
import dev.receipt.service.TemplateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.math.BigDecimal;

/**
 * Сервис, реализующий интерфейс {@link TemplateService}
 */
@Service
public class TemplateServiceImpl implements TemplateService {
    @Value("${currency.sign}")
    private String currencySign;

    @Override
    public String createContent(BookingResponse bookingResponse, MovieResponse movieResponse, UserResponse userResponse) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("bookingResponse", bookingResponse);
        context.setVariable("userResponse", userResponse);
        context.setVariable("movieResponse", movieResponse);
        context.setVariable("totalPrice", bookingResponse.places().stream()
                .map(PlaceResponse::price)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        context.setVariable("currencySign", currencySign);

        return templateEngine.process("templates/booking", context);
    }
}
