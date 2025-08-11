package dev.admin.client;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

public class OAuthHttpHeadersProvider implements HttpHeadersProvider {
    private final OAuth2AuthorizedClientManager clientManager;

    public OAuthHttpHeadersProvider(OAuth2AuthorizedClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public HttpHeaders getHeaders(Instance instance) {
        OAuth2AuthorizedClient auth2AuthorizedClient = this.clientManager.authorize(
                OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
                        .principal("admin-server")
                        .build()
        );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(auth2AuthorizedClient.getAccessToken().getTokenValue());

        return httpHeaders;
    }
}
