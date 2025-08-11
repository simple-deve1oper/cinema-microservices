package dev.file.image.controller;

import dev.file.image.entity.Image;
import dev.file.image.repository.ImageRepository;
import dev.file.image.util.FileUtils;
import dev.library.domain.file.dto.ImageRequest;
import dev.library.test.config.AbstractControllerTest;
import dev.library.test.dto.constant.GrantType;
import dev.library.test.util.AuthorizationUtils;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageControllerTest extends AbstractControllerTest {
    @Autowired
    private ImageRepository imageRepository;

    @TempDir(cleanup = CleanupMode.ALWAYS)
    static Path path;

    @DynamicPropertySource
    static void directoryProperties(DynamicPropertyRegistry registry) {
        registry.add("directory.images", path::toString);
    }

    @Test
    @Order(1)
    void getAll_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/file/images")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.greaterThan(0));
    }

    @Test
    @Order(2)
    void getAll_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/file/images")
                .then()
                    .log().all()
                    .statusCode(403);
    }

    @Test
    @Order(3)
    void getAll_unauthorized() {
        RestAssured
                .given()
                .when()
                    .get("/api/v1/file/images")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    @Order(4)
    void getAllByMovieId_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        RestAssured
                .given()
                    .pathParam("movie-id", 2)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/file/images/movie/{movie-id}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.greaterThan(0));
    }

    @Test
    @Order(5)
    void getAllByMovieId_unauthorized() {
        RestAssured
                .given()
                    .pathParam("movie-id", 2)
                .when()
                    .get("/api/v1/file/images/movie/{movie-id}")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    @Order(6)
    void getResourceByMovieIdAndNumber_ok() throws IOException {
        Resource resource = new ClassPathResource("paddington-in-peru-poster.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                resource.getFilename(),
                MediaType.IMAGE_JPEG_VALUE,
                resource.getInputStream()
        );
        FileUtils.saveFile(multipartFile, path);

        RestAssured
                .given()
                    .pathParams(Map.of("movie-id", 2, "number", 1))
                .when()
                    .get("/api/v1/file/images/resource/{movie-id}/{number}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType("image/jpeg")
                    .extract().asByteArray();
    }

    @Test
    @Order(7)
    void getResourceByMovieIdAndNumber_notFound() {
        RestAssured
                .given()
                    .pathParams(Map.of("movie-id", 10, "number", 4))
                .when()
                    .get("/api/v1/file/images/resource/{movie-id}/{number}")
                .then()
                    .log().all()
                    .statusCode(404)
                    .body("code", Matchers.equalTo(404))
                    .body("message", Matchers.equalTo("Запись об изображении с идентификатором фильма 10 и порядковым номером 4 не найдена"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(8)
    void create_ok() throws IOException {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        File image = new ClassPathResource("/test-two.png").getFile();
        RestAssured
                .given()
                    .pathParam("movie-id", 1L)
                    .multiPart("image", image)
                    .header("Authorization", "Bearer " + token)
                    .contentType("multipart/form-data")
                .when()
                    .post("/api/v1/file/images/movie/{movie-id}/image")
                .then()
                    .log().all()
                    .statusCode(201);
    }

    @Test
    @Order(9)
    void create_badRequest() throws IOException {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        File image = new ClassPathResource("/test_text.txt").getFile();
        RestAssured
                .given()
                    .pathParam("movie-id", 1L)
                    .multiPart("image", image)
                    .header("Authorization", "Bearer " + token)
                    .contentType("multipart/form-data")
                .when()
                    .post("/api/v1/file/images/movie/{movie-id}/image")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("Загружаемый файл должен быть изображением в формате jpeg, jpg или png"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(9)
    void create_entityAlreadyExistsException() throws IOException {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        File image = new ClassPathResource("/paddington-in-peru-poster.jpg").getFile();
        RestAssured
                .given()
                    .pathParam("movie-id", 1L)
                    .multiPart("image", image)
                    .header("Authorization", "Bearer " + token)
                    .contentType("multipart/form-data")
                .when()
                    .post("/api/v1/file/images/movie/{movie-id}/image")
                .then()
                    .log().all()
                    .statusCode(409)
                    .body("code", Matchers.equalTo(409))
                    .body("message", Matchers.equalTo("Файл c именем paddington-in-peru-poster.jpg уже существует"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(10)
    void create_forbidden() throws IOException {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        File image = new ClassPathResource("/paddington-in-peru-poster.jpg").getFile();
        RestAssured
                .given()
                    .pathParam("movie-id", 1L)
                    .multiPart("image", image)
                    .header("Authorization", "Bearer " + token)
                    .contentType("multipart/form-data")
                .when()
                    .post("/api/v1/file/images/movie/{movie-id}/image")
                .then()
                    .log().all()
                    .statusCode(403);
    }

    @Test
    @Order(11)
    void create_unauthorized() throws IOException {
        File image = new ClassPathResource("/paddington-in-peru-poster.jpg").getFile();
        RestAssured
                .given()
                    .pathParam("movie-id", 1L)
                    .multiPart("image", image)
                    .contentType("multipart/form-data")
                .when()
                    .post("/api/v1/file/images/movie/{movie-id}/image")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    @Order(12)
    void updateImageNumbers_ok() throws IOException {
        Resource resource = new ClassPathResource("test.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                resource.getFilename(),
                MediaType.IMAGE_JPEG_VALUE,
                resource.getInputStream()
        );
        FileUtils.saveFile(multipartFile, path);

        Image image = Image.builder()
                .fileName(resource.getFilename())
                .movieId(2L)
                .number(2)
                .build();
        imageRepository.save(image);

        List<Image> images = imageRepository.findAllByMovieId(2L);
        Assertions.assertEquals(2, images.size());
        Assertions.assertEquals(1, images.get(0).getNumber());
        Assertions.assertEquals("paddington-in-peru-poster.jpg", images.get(0).getFileName());
        Assertions.assertEquals(2, images.get(1).getNumber());
        Assertions.assertEquals("test.jpg", images.get(1).getFileName());

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        List<ImageRequest> requests = List.of(
                new ImageRequest(2L, "test.jpg", 1),
                new ImageRequest(2L, "paddington-in-peru-poster.jpg", 2)
        );
        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json")
                    .body(requests)
                .when()
                    .put("/api/v1/file/images/numbers")
                .then()
                    .log().all()
                    .statusCode(200);

        images = imageRepository.findAllByMovieId(2L);
        Assertions.assertEquals(2, images.size());
        Assertions.assertEquals(1, images.get(0).getNumber());
        Assertions.assertEquals("test.jpg", images.get(0).getFileName());
        Assertions.assertEquals(2, images.get(1).getNumber());
        Assertions.assertEquals("paddington-in-peru-poster.jpg", images.get(1).getFileName());
    }

    @Test
    @Order(13)
    void updateImageNumbers_badRequestException_checkValidation() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        List<ImageRequest> requests = List.of(
                new ImageRequest(null, "", null)
        );
        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json")
                    .body(requests)
                .when()
                    .put("/api/v1/file/images/numbers")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("Ошибка валидации"))
                    .body("fields.movieId", Matchers.equalTo("Идентификатор фильма не может быть пустым"))
                    .body("fields.fileName", Matchers.equalTo("Наименование файла не может быть пустым"))
                    .body("fields.number", Matchers.equalTo("Порядковый номер не может быть пустым"))
                    .body("dateTime", Matchers.notNullValue());

        requests = List.of(
                new ImageRequest(0L, "1234567890".repeat(30), -1)
        );
        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json")
                    .body(requests)
                .when()
                    .put("/api/v1/file/images/numbers")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("Ошибка валидации"))
                    .body("fields.movieId", Matchers.equalTo("Минимальное значение идентификатора фильма 1"))
                    .body("fields.fileName", Matchers.equalTo("Наименование файла не может содержать более 255 символов"))
                    .body("fields.number", Matchers.equalTo("Минимальное значение порядкового номера 1"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(14)
    void updateImageNumbers_badRequestException_differentMovies() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        List<ImageRequest> requests = List.of(
                new ImageRequest(1L, "test.jpg", 1),
                new ImageRequest(2L, "paddington-in-peru-poster.jpg", 2)
        );
        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json")
                    .body(requests)
                .when()
                    .put("/api/v1/file/images/numbers")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("Обновление номеров изображений может происходить только по одному фильму за один запрос"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(15)
    void updateImageNumbers_badRequestException_sameNumbers() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        List<ImageRequest> requests = List.of(
                new ImageRequest(2L, "test.jpg", 1),
                new ImageRequest(2L, "paddington-in-peru-poster.jpg", 1)
        );
        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json")
                    .body(requests)
                .when()
                    .put("/api/v1/file/images/numbers")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("В переданных объектах запроса номера не должны повторяться"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(16)
    void updateImageNumbers_badRequestException_nonOrdinalNumbers() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        List<ImageRequest> requests = List.of(
                new ImageRequest(2L, "test.jpg", 5),
                new ImageRequest(2L, "paddington-in-peru-poster.jpg", 13)
        );
        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json")
                    .body(requests)
                .when()
                    .put("/api/v1/file/images/numbers")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("В переданных объектах запроса все номера должны быть порядковыми"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(17)
    void updateImageNumbers_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        List<ImageRequest> requests = List.of(
                new ImageRequest(2L, "test.jpg", 1),
                new ImageRequest(2L, "black-cab-poster.jpeg", 2)
        );
        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json")
                    .body(requests)
                .when()
                    .put("/api/v1/file/images/numbers")
                .then()
                    .log().all()
                    .statusCode(403);
    }

    @Test
    @Order(18)
    void updateImageNumbers_unauthorized() {
        List<ImageRequest> requests = List.of(
                new ImageRequest(2L, "test.jpg", 1),
                new ImageRequest(2L, "black-cab-poster.jpeg", 2)
        );
        RestAssured
                .given()
                    .contentType("application/json")
                    .body(requests)
                .when()
                    .put("/api/v1/file/images/numbers")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    @Order(19)
    void deleteById_ok() throws IOException {
        Resource resource = new ClassPathResource("black-cab-poster.jpeg");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                resource.getFilename(),
                MediaType.IMAGE_JPEG_VALUE,
                resource.getInputStream()
        );
        FileUtils.saveFile(multipartFile, path);

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        UUID id = imageRepository.findByMovieIdAndNumber(1L, 1).get().getId();
        RestAssured
                .given()
                    .pathParam("id", id)
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json")
                .when()
                    .delete("/api/v1/file/images/{id}")
                .then()
                    .log().all()
                    .statusCode(204);
    }

    @Test
    @Order(20)
    void deleteById_notFound() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        UUID id = UUID.randomUUID();
        RestAssured
                .given()
                    .pathParam("id", id)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .delete("/api/v1/file/images/{id}")
                .then()
                    .log().all()
                    .statusCode(404)
                    .body("code", Matchers.equalTo(404))
                    .body("message", Matchers.equalTo("Запись об изображении с идентификатором %s не найдена".formatted(id)))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(21)
    void deleteById_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        UUID id = UUID.randomUUID();
        RestAssured
                .given()
                    .pathParam("id", id)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .delete("/api/v1/file/images/{id}")
                .then()
                    .log().all()
                    .statusCode(403);
    }

    @Test
    @Order(22)
    void deleteById_unauthorized() {
        UUID id = UUID.randomUUID();
        RestAssured
                .given()
                    .pathParam("id", id)
                .when()
                    .delete("/api/v1/file/images/{id}")
                .then()
                    .log().all()
                    .statusCode(401);
    }
}
