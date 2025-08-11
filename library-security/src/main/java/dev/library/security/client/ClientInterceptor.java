package dev.library.security.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class ClientInterceptor implements ClientHttpRequestInterceptor {
    private static final String REGISTRATION_ID = "keycloak";
    private static final Logger log = LoggerFactory.getLogger(ClientInterceptor.class);

    private OAuth2AccessToken accessToken;

    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    public ClientInterceptor(OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
    }

    @Override
    public ClientHttpResponse intercept(
            @NonNull HttpRequest request,
            @NonNull byte[] body,
            @NonNull ClientHttpRequestExecution execution
    ) throws IOException {
        log.debug("Request URI: {}", request.getURI());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return execution.execute(request, body);
        } else if (authentication.getPrincipal() instanceof String) {
            final OAuth2AccessToken accessToken = getAccessToken();
            if (accessToken == null) {
                return execution.execute(request, body);
            }
            request.getHeaders().setBearerAuth(accessToken.getTokenValue());
        } else {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            if (jwt == null) {
                return execution.execute(request, body);
            }
            request.getHeaders().setBearerAuth(jwt.getTokenValue());
        }

        return execution.execute(request, body);
    }

    private OAuth2AccessToken getAccessToken() {
        if (!isTokenValid(accessToken)) {
            setAccessToken();
        }
        return accessToken;
    }

    private void setAccessToken() {
        accessToken = null;
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            OAuth2AuthorizeRequest req = OAuth2AuthorizeRequest
                    .withClientRegistrationId(REGISTRATION_ID)
                    .principal(authentication.getName())
                    .build();
            OAuth2AuthorizedClient auth2AuthorizedClient = oAuth2AuthorizedClientManager.authorize(req);
            if (auth2AuthorizedClient != null) {
                accessToken = auth2AuthorizedClient.getAccessToken();
            }
        }
    }

    private boolean isTokenValid(OAuth2AccessToken token) {
        return token != null && token.getExpiresAt() != null
                && token.getExpiresAt().isAfter(Instant.now());
    }
}