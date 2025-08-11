package dev.gateway.config;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

@Configuration
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> dictionaryServiceRoute() {
        return GatewayRouterFunctions.route("dictionary-service")
                .route(RequestPredicates.path("/api/v1/dictionary/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("dictionary-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("dictionaryServiceCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> dictionaryServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("dictionary-service-swagger")
                .route(RequestPredicates.path("/aggregate/dictionary-service/v3/api-docs"), HandlerFunctions.http())
                .filter(FilterFunctions.setPath("/v3/api-docs"))
                .filter(LoadBalancerFilterFunctions.lb("dictionary-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("dictionaryServiceSwaggerCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fileServiceRoute() {
        return GatewayRouterFunctions.route("file-service")
                .route(RequestPredicates.path("/api/v1/file/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("file-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("fileServiceCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fileServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("file-service-swagger")
                .route(RequestPredicates.path("/aggregate/file-service/v3/api-docs"), HandlerFunctions.http())
                .filter(FilterFunctions.setPath("/v3/api-docs"))
                .filter(LoadBalancerFilterFunctions.lb("file-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("fileServiceSwaggerCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> movieServiceRoute() {
        return GatewayRouterFunctions.route("movie-service")
                .route(RequestPredicates.path("/api/v1/genres/**").or(RequestPredicates.path("/api/v1/movies/**")), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("movie-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("movieServiceCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> movieServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("movie-service-swagger")
                .route(RequestPredicates.path("/aggregate/movie-service/v3/api-docs"), HandlerFunctions.http())
                .filter(FilterFunctions.setPath("/v3/api-docs"))
                .filter(LoadBalancerFilterFunctions.lb("movie-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("movieServiceSwaggerCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> sessionServiceRoute() {
        return GatewayRouterFunctions.route("session-service")
                .route(RequestPredicates.path("/api/v1/sessions/**").or(RequestPredicates.path("/api/v1/places/**")), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("session-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("sessionServiceCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> sessionServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("session-service-swagger")
                .route(RequestPredicates.path("/aggregate/session-service/v3/api-docs"), HandlerFunctions.http())
                .filter(FilterFunctions.setPath("/v3/api-docs"))
                .filter(LoadBalancerFilterFunctions.lb("session-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("sessionServiceSwaggerCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> bookingServiceRoute() {
        return GatewayRouterFunctions.route("booking-service")
                .route(RequestPredicates.path("/api/v1/bookings/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("booking-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("bookingServiceCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> bookingServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("booking-service-swagger")
                .route(RequestPredicates.path("/aggregate/booking-service/v3/api-docs"), HandlerFunctions.http())
                .filter(FilterFunctions.setPath("/v3/api-docs"))
                .filter(LoadBalancerFilterFunctions.lb("booking-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("bookingServiceSwaggerCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> receiptServiceRoute() {
        return GatewayRouterFunctions.route("receipt-service")
                .route(RequestPredicates.path("/api/v1/receipts/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("receipt-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("receiptServiceCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> receiptServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("receipt-service-swagger")
                .route(RequestPredicates.path("/aggregate/receipt-service/v3/api-docs"), HandlerFunctions.http())
                .filter(FilterFunctions.setPath("/v3/api-docs"))
                .filter(LoadBalancerFilterFunctions.lb("receipt-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("receiptServiceSwaggerCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return GatewayRouterFunctions.route("user-service")
                .route(RequestPredicates.path("/api/v1/users/**").or(RequestPredicates.path("/api/v1/roles/**")), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("user-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("userServiceCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("user-service-swagger")
                .route(RequestPredicates.path("/aggregate/user-service/v3/api-docs"), HandlerFunctions.http())
                .filter(FilterFunctions.setPath("/v3/api-docs"))
                .filter(LoadBalancerFilterFunctions.lb("user-service"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("userServiceSwaggerCircuitBreaker", URI.create("forward:/fallback")))
                .build();
    }
}
