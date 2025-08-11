package dev.movie.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import dev.library.domain.dictionary.country.dto.CountryResponse;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import dev.library.domain.movie.dto.MovieRequest;
import dev.library.domain.movie.dto.MovieSearchRequest;
import dev.library.domain.movie.dto.constant.AgeRating;
import dev.library.test.config.AbstractControllerTest;
import dev.library.test.dto.constant.GrantType;
import dev.library.test.util.AuthorizationUtils;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableWireMock({
        @ConfigureWireMock(name = "country-service", port = 8131),
        @ConfigureWireMock(name = "participant-service", port = 8132),
})
public class MovieControllerTest extends AbstractControllerTest {
    @InjectWireMock("country-service")
    WireMockServer mockCountryService;
    @InjectWireMock("participant-service")
    WireMockServer mockParticipantService;

    private List<CountryResponse> countriesForMovieOne;
    private List<CountryResponse> countriesForMovieTwo;

    private List<ParticipantResponse> participantsDirectorForMovieOne;
    private List<ParticipantResponse> participantsActorForMovieOne;
    private List<ParticipantResponse> participantsDirectorForMovieTwo;
    private List<ParticipantResponse> participantsActorForMovieTwo;

    @BeforeEach
    void setUp() {
        CountryResponse countryResponseUSA = new CountryResponse(1L, "840", "США");
        countriesForMovieOne = List.of(countryResponseUSA);
        CountryResponse countryResponseUnitedKingdom = new CountryResponse(5L, "826", "Великобритания");
        CountryResponse countryResponseFrance = new CountryResponse(2L, "250", "Франция");
        CountryResponse countryResponseJapan = new CountryResponse(12L, "392", "Япония");
        countriesForMovieTwo = List.of(countryResponseUnitedKingdom, countryResponseFrance,
                countryResponseJapan, countryResponseUSA);

        ParticipantResponse participantResponseMovieOneDirectorOne = new ParticipantResponse(16L, "Гудисон", "Брюс");
        participantsDirectorForMovieOne = List.of(participantResponseMovieOneDirectorOne);
        ParticipantResponse participantResponseMovieOneActorOne = new ParticipantResponse(17L, "Фрост", "Ник");
        ParticipantResponse participantResponseMovieOneActorTwo = new ParticipantResponse(18L, "Карлсен", "Синнёве");
        ParticipantResponse participantResponseMovieOneActorThree = new ParticipantResponse(19L, "Норрис", "Люк");
        participantsActorForMovieOne = List.of(participantResponseMovieOneActorOne, participantResponseMovieOneActorTwo, participantResponseMovieOneActorThree);
        ParticipantResponse participantResponseMovieTwoDirectorOne = new ParticipantResponse(7L, "Уилсон", "Дагал");
        participantsDirectorForMovieTwo = List.of(participantResponseMovieTwoDirectorOne);
        ParticipantResponse participantResponseMovieTwoActorOne = new ParticipantResponse(8L, "Уишоу", "Бен");
        ParticipantResponse participantResponseMovieTwoActorTwo = new ParticipantResponse(9L, "Бонневилль", "Хью");
        ParticipantResponse participantResponseMovieTwoActorThree = new ParticipantResponse(10L, "Коулман", "Оливия");
        ParticipantResponse participantResponseMovieTwoActorFour = new ParticipantResponse(11L, "Стонтон", "Имельда");
        ParticipantResponse participantResponseMovieTwoActorFive = new ParticipantResponse(12L, "Уолтерс", "Джули");
        ParticipantResponse participantResponseMovieTwoActorSix = new ParticipantResponse(13L, "Бродбент", "Джим");
        ParticipantResponse participantResponseMovieTwoActorSeven = new ParticipantResponse(14L, "Бандерас", "Антонио");
        ParticipantResponse participantResponseMovieTwoActorEight = new ParticipantResponse(15L, "Зеглер", "Рейчел");
        participantsActorForMovieTwo = List.of(participantResponseMovieTwoActorOne, participantResponseMovieTwoActorTwo,
                participantResponseMovieTwoActorThree, participantResponseMovieTwoActorFour,
                participantResponseMovieTwoActorFive, participantResponseMovieTwoActorSix,
                participantResponseMovieTwoActorSeven, participantResponseMovieTwoActorEight);
    }

    @Test
    @Order(1)
    void getAll_ok() throws JsonProcessingException {
        String countriesScenarioName = "getCountries";
        String countriesForMovieOneJson = mapper.writeValueAsString(countriesForMovieOne);
        mockCountryService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/countries/search/codes"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(countriesScenarioName)
                                .whenScenarioStateIs(Scenario.STARTED)
                                .willReturn(
                                        WireMock.ok(countriesForMovieOneJson).withHeader("Content-Type", "application/json")
                                )
                                .willSetStateTo("Second")
                );
        String countriesForMovieTwoJson = mapper.writeValueAsString(countriesForMovieTwo);
        mockCountryService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/countries/search/codes"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(countriesScenarioName)
                                .whenScenarioStateIs("Second")
                                .willReturn(
                                        WireMock.ok(countriesForMovieTwoJson).withHeader("Content-Type", "application/json")
                                )
                );

        String participantsScenarioName = "getParticipants";
        String participantsDirectorForMovieOneJson = mapper.writeValueAsString(participantsDirectorForMovieOne);
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(participantsScenarioName)
                                .whenScenarioStateIs(Scenario.STARTED)
                                .willReturn(
                                        WireMock.ok(participantsDirectorForMovieOneJson).withHeader("Content-Type", "application/json")
                                )
                                .willSetStateTo("Two")
                );
        String participantsActorForMovieOneJson = mapper.writeValueAsString(participantsActorForMovieOne);
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(participantsScenarioName)
                                .whenScenarioStateIs("Two")
                                .willReturn(
                                        WireMock.ok(participantsActorForMovieOneJson).withHeader("Content-Type", "application/json")
                                )
                                .willSetStateTo("Three")
                );
        String participantsDirectorForMovieTwoJson = mapper.writeValueAsString(participantsDirectorForMovieTwo);
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(participantsScenarioName)
                                .whenScenarioStateIs("Three")
                                .willReturn(
                                        WireMock.ok(participantsDirectorForMovieTwoJson).withHeader("Content-Type", "application/json")
                                )
                                .willSetStateTo("Four")
                );
        String participantsActorForMovieTwoJson = mapper.writeValueAsString(participantsActorForMovieTwo);
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(participantsScenarioName)
                                .whenScenarioStateIs("Four")
                                .willReturn(
                                        WireMock.ok(participantsActorForMovieTwoJson).withHeader("Content-Type", "application/json")
                                )
                );

        RestAssured
                .given()
                .when()
                    .get("/api/v1/movies")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.greaterThan(0));
    }

    @Test
    @Order(2)
    void getAll_some() throws JsonProcessingException {
        String countriesForMovieOneJson = mapper.writeValueAsString(countriesForMovieOne);
        mockCountryService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/countries/search/codes"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(countriesForMovieOneJson).withHeader("Content-Type", "application/json")
                                )
                );
        String participantsScenarioName = "getParticipants";
        String participantsDirectorForMovieOneJson = mapper.writeValueAsString(participantsDirectorForMovieOne);
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(participantsScenarioName)
                                .whenScenarioStateIs(Scenario.STARTED)
                                .willReturn(
                                        WireMock.ok(participantsDirectorForMovieOneJson).withHeader("Content-Type", "application/json")
                                )
                                .willSetStateTo("Two")
                );
        String participantsActorForMovieOneJson = mapper.writeValueAsString(participantsActorForMovieOne);
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(participantsScenarioName)
                                .whenScenarioStateIs("Two")
                                .willReturn(
                                        WireMock.ok(participantsActorForMovieOneJson).withHeader("Content-Type", "application/json")
                                )
                );

        MovieSearchRequest request = new MovieSearchRequest(
                "Перевозчик душ",
                2024,
                true
        );
        Map<String, String> params = new HashMap<>();
        params.put("name", request.getName());
        params.put("year", request.getYear().toString());
        params.put("rental", request.getRental().toString());

        RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .formParams(params)
                .when()
                .get("/api/v1/movies")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(1));
    }

    @Test
    @Order(2)
    void getAll_dictionaryService_unavailable() {
        RestAssured
                .given()
                .when()
                    .get("/api/v1/movies")
                .then()
                    .log().all()
                    .statusCode(500)
                    .body("code", Matchers.equalTo(500))
                    .body("message", Matchers.equalTo("Сервис справочника временно недоступен, повторите попытку позже!"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(3)
    void getById_ok() throws JsonProcessingException {
        String countriesForMovieOneJson = mapper.writeValueAsString(countriesForMovieOne);
        mockCountryService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/countries/search/codes"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(countriesForMovieOneJson).withHeader("Content-Type", "application/json")
                                )
                );

        String participantsScenarioName = "getParticipants";
        String participantsDirectorForMovieOneJson = mapper.writeValueAsString(participantsDirectorForMovieOne);
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(participantsScenarioName)
                                .whenScenarioStateIs(Scenario.STARTED)
                                .willReturn(
                                        WireMock.ok(participantsDirectorForMovieOneJson).withHeader("Content-Type", "application/json")
                                )
                                .willSetStateTo("Two")
                );
        String participantsActorForMovieOneJson = mapper.writeValueAsString(participantsActorForMovieOne);
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(participantsScenarioName)
                                .whenScenarioStateIs("Two")
                                .willReturn(
                                        WireMock.ok(participantsActorForMovieOneJson).withHeader("Content-Type", "application/json")
                                )
                );

        RestAssured
                .given()
                    .pathParam("id", 1)
                .when()
                    .get("/api/v1/movies/{id}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", Matchers.equalTo(1))
                    .body("name", Matchers.equalTo("Перевозчик душ"))
                    .body("description", Matchers.equalTo("Энн и Патрик после ночной прогулки в расстроенных чувствах садятся в черное такси, водителем которого оказывается добродушный и болтливый мужчина. Пара переживает не лучший этап в отношениях, поэтому неохотно поддерживает диалог. Неожиданно вместо дома они оказываются запертыми в машине на пустынном участке дороги, населенном привидениями. Чтобы выжить, паре необходимо выяснить личность загадочного таксиста и его мотивы"))
                    .body("duration", Matchers.equalTo(98))
                    .body("year", Matchers.equalTo(2024))
                    .body("ageRating", Matchers.equalTo("18+"))
                    .body("rental", Matchers.equalTo(true))
                    .body("genres", Matchers.hasSize(2))
                    .body("genres[0].id", Matchers.equalTo(3))
                    .body("genres[0].name", Matchers.equalTo("Ужасы"))
                    .body("genres[1].id", Matchers.equalTo(5))
                    .body("genres[1].name", Matchers.equalTo("Триллеры"))
                    .body("countries", Matchers.hasSize(1))
                    .body("countries[0].id", Matchers.equalTo(1))
                    .body("countries[0].code", Matchers.equalTo("840"))
                    .body("countries[0].name", Matchers.equalTo("США"))
                    .body("directors", Matchers.hasSize(1))
                    .body("directors[0].id", Matchers.equalTo(16))
                    .body("directors[0].lastName", Matchers.equalTo("Гудисон"))
                    .body("directors[0].firstName", Matchers.equalTo("Брюс"))
                    .body("directors[0].middleName", Matchers.nullValue())
                    .body("actors", Matchers.hasSize(3))
                    .body("actors[0].id", Matchers.equalTo(17))
                    .body("actors[0].lastName", Matchers.equalTo("Фрост"))
                    .body("actors[0].firstName", Matchers.equalTo("Ник"))
                    .body("actors[0].middleName", Matchers.nullValue())
                    .body("actors[1].id", Matchers.equalTo(18))
                    .body("actors[1].lastName", Matchers.equalTo("Карлсен"))
                    .body("actors[1].firstName", Matchers.equalTo("Синнёве"))
                    .body("actors[1].middleName", Matchers.nullValue())
                    .body("actors[2].id", Matchers.equalTo(19))
                    .body("actors[2].lastName", Matchers.equalTo("Норрис"))
                    .body("actors[2].firstName", Matchers.equalTo("Люк"))
                    .body("actors[2].middleName", Matchers.nullValue());
    }

    @Test
    @Order(4)
    void getById_dictionaryService_unavailable() {
        RestAssured
                .given()
                    .pathParam("id", 2)
                .when()
                    .get("/api/v1/movies/{id}")
                .then()
                    .log().all()
                    .statusCode(500)
                    .body("code", Matchers.equalTo(500))
                    .body("message", Matchers.equalTo("Сервис справочника временно недоступен, повторите попытку позже!"))
                    .body("dateTime", Matchers.notNullValue());

    }

    @Test
    @Order(5)
    void getById_notFound() {
        RestAssured
                .given()
                    .pathParam("id", 999)
                .when()
                    .get("/api/v1/movies/{id}")
                .then()
                    .log().all()
                    .statusCode(404)
                    .body("code", Matchers.equalTo(404))
                    .body("message", Matchers.equalTo("Фильм с идентификатором 999 не найден"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(6)
    void getDurationById_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        RestAssured
                .given()
                    .pathParam("id", 1)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/movies/{id}/duration")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$", Matchers.equalTo(98));
    }

    @Test
    @Order(7)
    void getDurationById_unauthorized() {
        RestAssured
                .given()
                    .pathParam("id", 1)
                .when()
                    .get("/api/v1/movies/{id}/duration")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    @Order(8)
    void existsById_true() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        RestAssured
                .given()
                    .pathParam("id", 2)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/movies/exists/{id}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$", Matchers.equalTo(true));
    }

    @Test
    @Order(9)
    void existsById_false() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        RestAssured
                .given()
                    .pathParam("id", 99)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/movies/exists/{id}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$", Matchers.equalTo(false));
    }

    @Test
    @Order(10)
    void existsById_unauthorized() {
        RestAssured
                .given()
                    .pathParam("id", 1)
                .when()
                    .get("/api/v1/movies/exists/{id}")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    @Order(11)
    void create_ok() throws JsonProcessingException {
        mockCountryService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/countries/search/not-exists/codes"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("[]").withHeader("Content-Type", "application/json")
                                )
                );
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/not-exists/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("[]").withHeader("Content-Type", "application/json")
                                )
                );

        CountryResponse countryResponseCanada = new CountryResponse(6L, "124", "Канада");
        String countriesForMovieJson = mapper.writeValueAsString(List.of(countryResponseCanada));
        mockCountryService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/countries/search/codes"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(countriesForMovieJson).withHeader("Content-Type", "application/json")
                                )
                );
        String participantsScenarioName = "getParticipants";
        ParticipantResponse participantResponseDirector = new ParticipantResponse(1L, "Ларраин", "Пабло");
        String participantsDirectorJson = mapper.writeValueAsString(List.of(participantResponseDirector));
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(participantsScenarioName)
                                .whenScenarioStateIs(Scenario.STARTED)
                                .willReturn(
                                        WireMock.ok(participantsDirectorJson).withHeader("Content-Type", "application/json")
                                )
                                .willSetStateTo("Two")
                );
        ParticipantResponse participantResponseActor = new ParticipantResponse(2L, "Джоли", "Анджелина");
        String participantsActorJson = mapper.writeValueAsString(List.of(participantResponseActor));
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(participantsScenarioName)
                                .whenScenarioStateIs("Two")
                                .willReturn(
                                        WireMock.ok(participantsActorJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        MovieRequest request = new MovieRequest(
                "Тест",
                "Тест",
                55,
                1012,
                AgeRating.ZERO,
                false,
                Set.of(1L),
                Set.of("124"),
                Set.of(1L),
                Set.of(2L)
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/movies")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", Matchers.equalTo(3))
                    .body("name", Matchers.equalTo("Тест"))
                    .body("description", Matchers.equalTo("Тест"))
                    .body("duration", Matchers.equalTo(55))
                    .body("year", Matchers.equalTo(1012))
                    .body("ageRating", Matchers.equalTo("0+"))
                    .body("rental", Matchers.equalTo(false))
                    .body("genres", Matchers.hasSize(1))
                    .body("genres[0].id", Matchers.equalTo(1))
                    .body("genres[0].name", Matchers.equalTo("Комедии"))
                    .body("countries", Matchers.hasSize(1))
                    .body("countries[0].id", Matchers.equalTo(6))
                    .body("countries[0].code", Matchers.equalTo("124"))
                    .body("countries[0].name", Matchers.equalTo("Канада"))
                    .body("directors", Matchers.hasSize(1))
                    .body("directors[0].id", Matchers.equalTo(1))
                    .body("directors[0].lastName", Matchers.equalTo("Ларраин"))
                    .body("directors[0].firstName", Matchers.equalTo("Пабло"))
                    .body("directors[0].middleName", Matchers.nullValue())
                    .body("actors", Matchers.hasSize(1))
                    .body("actors[0].id", Matchers.equalTo(2))
                    .body("actors[0].lastName", Matchers.equalTo("Джоли"))
                    .body("actors[0].firstName", Matchers.equalTo("Анджелина"))
                    .body("actors[0].middleName", Matchers.nullValue());
    }

    @Test
    @Order(12)
    void create_badRequestException_checkValidation() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        MovieRequest request = new MovieRequest(
                "",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/movies")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("Ошибка валидации"))
                    .body("fields.name", Matchers.equalTo("Наименование фильма не может быть пустым"))
                    .body("fields.description", Matchers.equalTo("Описание фильма не может быть пустым"))
                    .body("fields.duration", Matchers.equalTo("Продолжительность фильма не может быть пустым"))
                    .body("fields.year", Matchers.equalTo("Год выхода фильма должен не может быть пустым"))
                    .body("fields.ageRating", Matchers.equalTo("Возрастной рейтинг не может быть пустым"))
                    .body("fields.rental", Matchers.equalTo("Статус проката фильма не может быть пустым"))
                    .body("fields.genreIds", Matchers.equalTo("Список идентификаторов жанров должен содержать хотя бы один элемент"))
                    .body("fields.countryCodes", Matchers.equalTo("Список кодов стран должен содержать хотя бы один элемент"))
                    .body("fields.directorIds", Matchers.equalTo("Список идентификаторов режиссёров должен содержать хотя бы один элемент"))
                    .body("fields.actorIds", Matchers.equalTo("Список идентификаторов актёров должен содержать хотя бы один элемент"))
                    .body("dateTime", Matchers.notNullValue());

        request = new MovieRequest(
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123",
                "Тест",
                20,
                2001,
                AgeRating.SIX,
                false,
                Set.of(1L,2L),
                Set.of("056"),
                Set.of(4L,5L),
                Set.of(1L,2L)
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/movies")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("Ошибка валидации"))
                    .body("fields.name", Matchers.equalTo("Наименование фильма не может содержать более 100 символов"))
                    .body("fields.duration", Matchers.equalTo("Минимальное значение продолжительности фильма 25"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(13)
    void create_dictionaryService_unavailable() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        MovieRequest request = new MovieRequest(
                "Фильм 4",
                "Фильм 4",
                85,
                1995,
                AgeRating.TWELVE,
                true,
                Set.of(6L),
                Set.of("056"),
                Set.of(3L,5L),
                Set.of(2L,4L)
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/movies")
                .then()
                    .statusCode(500)
                    .body("code", Matchers.equalTo(500))
                    .body("message", Matchers.equalTo("Сервис справочника временно недоступен, повторите попытку позже!"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(14)
    void create_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        MovieRequest request = new MovieRequest(
                "Фильм 4",
                "Фильм 4",
                85,
                1995,
                AgeRating.TWELVE,
                true,
                Set.of(6L),
                Set.of("056"),
                Set.of(3L,5L),
                Set.of(2L,4L)
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/movies")
                .then()
                    .statusCode(403);
    }

    @Test
    @Order(15)
    void create_unauthorized() {
        MovieRequest request = new MovieRequest(
                "Фильм 4",
                "Фильм 4",
                85,
                1995,
                AgeRating.TWELVE,
                true,
                Set.of(6L),
                Set.of("056"),
                Set.of(3L,5L),
                Set.of(2L,4L)
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                .when()
                    .post("/api/v1/movies")
                .then()
                    .statusCode(401);
    }

    @Test
    @Order(16)
    void update_ok() throws JsonProcessingException {
        mockCountryService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/countries/search/not-exists/codes"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("[]").withHeader("Content-Type", "application/json")
                                )
                );
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/not-exists/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("[]").withHeader("Content-Type", "application/json")
                                )
                );

        List<CountryResponse> newCountryResponses = List.of(
                new CountryResponse(3L, "643", "Россия"),
                new CountryResponse(4L, "056", "Бельгия")
        );
        String countriesForMovieJson = mapper.writeValueAsString(newCountryResponses);
        mockCountryService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/countries/search/codes"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(countriesForMovieJson).withHeader("Content-Type", "application/json")
                                )
                );
        String participantsScenarioName = "getParticipants";
        ParticipantResponse participantResponseDirector = new ParticipantResponse(2L, "Джоли", "Анджелина");
        String participantsDirectorJson = mapper.writeValueAsString(List.of(participantResponseDirector));
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(participantsScenarioName)
                                .whenScenarioStateIs(Scenario.STARTED)
                                .willReturn(
                                        WireMock.ok(participantsDirectorJson).withHeader("Content-Type", "application/json")
                                )
                                .willSetStateTo("Two")
                );
        ParticipantResponse participantResponseActor = new ParticipantResponse(1L, "Ларраин", "Пабло");
        String participantsActorJson = mapper.writeValueAsString(List.of(participantResponseActor));
        mockParticipantService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/dictionary/participants/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .inScenario(participantsScenarioName)
                                .whenScenarioStateIs("Two")
                                .willReturn(
                                        WireMock.ok(participantsActorJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        MovieRequest request = new MovieRequest(
                "Крутой фильм с известным актёром",
                "Описание",
                78,
                2025,
                AgeRating.EIGHTEEN,
                true,
                Set.of(5L),
                Set.of("643", "056"),
                Set.of(2L),
                Set.of(1L)
        );
        RestAssured
                .given()
                    .pathParam("id", 2L)
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .put("/api/v1/movies/{id}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", Matchers.equalTo(2))
                    .body("name", Matchers.equalTo("Крутой фильм с известным актёром"))
                    .body("description", Matchers.equalTo("Описание"))
                    .body("duration", Matchers.equalTo(78))
                    .body("year", Matchers.equalTo(2025))
                    .body("ageRating", Matchers.equalTo("18+"))
                    .body("rental", Matchers.equalTo(true))
                    .body("genres", Matchers.hasSize(1))
                    .body("genres[0].id", Matchers.equalTo(5))
                    .body("genres[0].name", Matchers.equalTo("Триллеры"))
                    .body("countries", Matchers.hasSize(2))
                    .body("countries[0].id", Matchers.equalTo(3))
                    .body("countries[0].code", Matchers.equalTo("643"))
                    .body("countries[0].name", Matchers.equalTo("Россия"))
                    .body("countries[1].id", Matchers.equalTo(4))
                    .body("countries[1].code", Matchers.equalTo("056"))
                    .body("countries[1].name", Matchers.equalTo("Бельгия"))
                    .body("directors", Matchers.hasSize(1))
                    .body("directors[0].id", Matchers.equalTo(2))
                    .body("directors[0].lastName", Matchers.equalTo("Джоли"))
                    .body("directors[0].firstName", Matchers.equalTo("Анджелина"))
                    .body("directors[0].middleName", Matchers.nullValue())
                    .body("actors", Matchers.hasSize(1))
                    .body("actors[0].id", Matchers.equalTo(1))
                    .body("actors[0].lastName", Matchers.equalTo("Ларраин"))
                    .body("actors[0].firstName", Matchers.equalTo("Пабло"))
                    .body("actors[0].middleName", Matchers.nullValue());
    }

    @Test
    @Order(17)
    void update_badRequestException_checkValidation() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        MovieRequest request = new MovieRequest(
                "",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        RestAssured
                .given()
                    .pathParam("id", 3L)
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .put("/api/v1/movies/{id}")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("Ошибка валидации"))
                    .body("fields.name", Matchers.equalTo("Наименование фильма не может быть пустым"))
                    .body("fields.description", Matchers.equalTo("Описание фильма не может быть пустым"))
                    .body("fields.duration", Matchers.equalTo("Продолжительность фильма не может быть пустым"))
                    .body("fields.year", Matchers.equalTo("Год выхода фильма должен не может быть пустым"))
                    .body("fields.ageRating", Matchers.equalTo("Возрастной рейтинг не может быть пустым"))
                    .body("fields.rental", Matchers.equalTo("Статус проката фильма не может быть пустым"))
                    .body("fields.genreIds", Matchers.equalTo("Список идентификаторов жанров должен содержать хотя бы один элемент"))
                    .body("fields.countryCodes", Matchers.equalTo("Список кодов стран должен содержать хотя бы один элемент"))
                    .body("fields.directorIds", Matchers.equalTo("Список идентификаторов режиссёров должен содержать хотя бы один элемент"))
                    .body("fields.actorIds", Matchers.equalTo("Список идентификаторов актёров должен содержать хотя бы один элемент"))
                    .body("dateTime", Matchers.notNullValue());

        request = new MovieRequest(
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123",
                "Тест",
                20,
                2001,
                AgeRating.SIX,
                false,
                Set.of(1L,2L),
                Set.of("056"),
                Set.of(4L,5L),
                Set.of(1L,2L)
        );
        RestAssured
                .given()
                    .pathParam("id", 3L)
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .put("/api/v1/movies/{id}")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("Ошибка валидации"))
                    .body("fields.name", Matchers.equalTo("Наименование фильма не может содержать более 100 символов"))
                    .body("fields.duration", Matchers.equalTo("Минимальное значение продолжительности фильма 25"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(18)
    void update_notFoundException() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        MovieRequest request = new MovieRequest(
                "Test",
                "Description",
                67,
                2004,
                AgeRating.ZERO,
                false,
                Set.of(1L),
                Set.of("056"),
                Set.of(4L),
                Set.of(8L)
        );
        RestAssured
                .given()
                    .pathParam("id", 1123L)
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .put("/api/v1/movies/{id}")
                .then()
                    .log().all()
                    .statusCode(404)
                    .body("code", Matchers.equalTo(404))
                    .body("message", Matchers.equalTo("Фильм с идентификатором 1123 не найден"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(19)
    void update_dictionaryService_unavailable() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        MovieRequest request = new MovieRequest(
                "Test",
                "Description",
                67,
                2004,
                AgeRating.ZERO,
                false,
                Set.of(1L),
                Set.of("056"),
                Set.of(4L),
                Set.of(8L)
        );
        RestAssured
                .given()
                    .pathParam("id", 2L)
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .put("/api/v1/movies/{id}")
                .then()
                    .log().all()
                    .statusCode(500)
                    .body("code", Matchers.equalTo(500))
                    .body("message", Matchers.equalTo("Сервис справочника временно недоступен, повторите попытку позже!"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(20)
    void update_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        MovieRequest request = new MovieRequest(
                "Test",
                "Description",
                67,
                2004,
                AgeRating.ZERO,
                false,
                Set.of(1L),
                Set.of("056"),
                Set.of(4L),
                Set.of(8L)
        );
        RestAssured
                .given()
                    .pathParam("id", 2L)
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .put("/api/v1/movies/{id}")
                .then()
                    .log().all()
                    .statusCode(403);
    }

    @Test
    @Order(21)
    void update_unauthorized() {
        MovieRequest request = new MovieRequest(
                "Test",
                "Description",
                67,
                2004,
                AgeRating.ZERO,
                false,
                Set.of(1L),
                Set.of("056"),
                Set.of(4L),
                Set.of(8L)
        );
        RestAssured
                .given()
                    .pathParam("id", 2L)
                    .body(request)
                    .contentType("application/json")
                .when()
                    .put("/api/v1/movies/{id}")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    @Order(22)
    void deleteById_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                    .pathParam("id", 2L)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .delete("/api/v1/movies/{id}")
                .then()
                    .log().all()
                    .statusCode(204);
    }

    @Test
    @Order(23)
    void deleteById_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        RestAssured
                .given()
                    .pathParam("id", 2L)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .delete("/api/v1/movies/{id}")
                .then()
                    .log().all()
                    .statusCode(403);
    }

    @Test
    @Order(24)
    void deleteById_unauthorized() {
        RestAssured
                .given()
                    .pathParam("id", 2L)
                .when()
                    .delete("/api/v1/movies/{id}")
                .then()
                    .log().all()
                    .statusCode(401);
    }
}
