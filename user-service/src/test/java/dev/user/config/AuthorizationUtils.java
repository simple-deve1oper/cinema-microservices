package dev.user.config;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class AuthorizationUtils {
    public static String getToken(RestClient restClient, GrantType grantType, String clientId, String clientSecret, String... authorizationParams) {
        MultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
        requestData.put("grant_type", Collections.singletonList(grantType.getValue()));
        requestData.put("client_id", Collections.singletonList(clientId));
        requestData.put("client_secret", Collections.singletonList(clientSecret));
        if (grantType == GrantType.PASSWORD) {
            if (authorizationParams == null) {
                throw new RuntimeException("Не введен логин и пароль для авторизации");
            }
            String username = authorizationParams[0];
            String password = authorizationParams[1];
            requestData.put("username", Collections.singletonList(username));
            requestData.put("password", Collections.singletonList(password));
        }

        ResponseEntity<Map<String, Object>> responseEntity = restClient
                .post()
                .uri("/protocol/openid-connect/token")
                .body(requestData)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});
        Map<String, Object> responseBody = responseEntity.getBody();
        Object accessToken = Objects.requireNonNull(responseBody).get("access_token");

        return String.valueOf(accessToken);
    }
}
