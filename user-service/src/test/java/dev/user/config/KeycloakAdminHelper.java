package dev.user.config;

import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class KeycloakAdminHelper {
    private final String clientId="admin-cli";
    private final String clientSecret="wN3n1aFc0lrKrCKgfTnvEFsOO4TmYrwb";

    private final RestClient restClientKeycloakApi;
    private final RestClient restClientToken;

    public KeycloakAdminHelper(String keycloakServerUrl) {
        restClientToken = RestClient.builder()
                .baseUrl("%s/realms/cinema".formatted(keycloakServerUrl))
                .build();

        restClientKeycloakApi = RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    MultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
                    requestData.put("grant_type", Collections.singletonList("client_credentials"));
                    requestData.put("client_id", Collections.singletonList(clientId));
                    requestData.put("client_secret", Collections.singletonList(clientSecret));

                    ResponseEntity<Map<String, Object>> responseEntity = restClientToken
                            .post()
                            .uri("/protocol/openid-connect/token")
                            .body(requestData)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .retrieve()
                            .toEntity(new ParameterizedTypeReference<>() {});
                    Map<String, Object> responseBody = responseEntity.getBody();
                    String accessToken = (String) Objects.requireNonNull(responseBody).get("access_token");
                    request.getHeaders().setBearerAuth(accessToken);
                    return execution.execute(request, body);
                })
                .baseUrl("%s/admin/realms/cinema".formatted(keycloakServerUrl))
                .build();
    }

    public RealmRepresentation getSettingsRealm() {
        return restClientKeycloakApi
                .get()
                .retrieve()
                .body(RealmRepresentation.class);

    }

    public void editSettingsRealm(RealmRepresentation realmRepresentation) {
        restClientKeycloakApi
                .put()
                .body(realmRepresentation)
                .retrieve()
                .body(Void.class);
    }
}
