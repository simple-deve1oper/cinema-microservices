package dev.gateway.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.eureka.TimeoutProperties;
import org.springframework.cloud.netflix.eureka.http.EurekaClientHttpRequestFactorySupplier;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.util.concurrent.TimeUnit;

public class KeycloakEurekaClientHttpRequestFactorySupplier implements EurekaClientHttpRequestFactorySupplier {
    @Value("${spring.security.oauth2.client.registration.keycloak.provider}")
    private String provider;
    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    private final TimeoutProperties timeoutProperties;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public KeycloakEurekaClientHttpRequestFactorySupplier(TimeoutProperties timeoutProperties, ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService) {
        this.timeoutProperties = timeoutProperties;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizedClientService = authorizedClientService;
    }


    @Override
    public ClientHttpRequestFactory get(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        if (sslContext != null || hostnameVerifier != null || timeoutProperties != null) {
            httpClientBuilder
                    .setConnectionManager(buildConnectionManager(sslContext, hostnameVerifier, timeoutProperties));
        }
        httpClientBuilder.setDefaultRequestConfig(buildRequestConfig());

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository,
                        authorizedClientService);
        httpClientBuilder.addRequestInterceptorLast((request, entity, context) -> {
            if (!request.containsHeader(HttpHeaders.AUTHORIZATION)) {
                OAuth2AuthorizedClient authorizedClient = authorizedClientManager
                        .authorize(OAuth2AuthorizeRequest
                                .withClientRegistrationId(provider)
                                .principal(clientId)
                                .build());

                assert authorizedClient != null;
                request.setHeader(HttpHeaders.AUTHORIZATION,
                        "Bearer %s".formatted(authorizedClient.getAccessToken().getTokenValue()));
            }
        });

        CloseableHttpClient httpClient = httpClientBuilder.build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        return requestFactory;
    }

    private HttpClientConnectionManager buildConnectionManager(SSLContext sslContext, HostnameVerifier hostnameVerifier,
                                                               TimeoutProperties timeoutProperties) {
        PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder
                .create();
        SSLConnectionSocketFactoryBuilder sslConnectionSocketFactoryBuilder = SSLConnectionSocketFactoryBuilder
                .create();
        if (sslContext != null) {
            sslConnectionSocketFactoryBuilder.setSslContext(sslContext);
        }
        if (hostnameVerifier != null) {
            sslConnectionSocketFactoryBuilder.setHostnameVerifier(hostnameVerifier);
        }
        connectionManagerBuilder.setSSLSocketFactory(sslConnectionSocketFactoryBuilder.build());
        if (timeoutProperties != null) {
            connectionManagerBuilder.setDefaultSocketConfig(SocketConfig.custom()
                    .setSoTimeout(Timeout.of(timeoutProperties.getSocketTimeout(), TimeUnit.MILLISECONDS))
                    .build());
            connectionManagerBuilder.setDefaultConnectionConfig(ConnectionConfig.custom()
                    .setConnectTimeout(Timeout.of(timeoutProperties.getConnectTimeout(), TimeUnit.MILLISECONDS))
                    .build());
        }
        return connectionManagerBuilder.build();
    }

    private RequestConfig buildRequestConfig() {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        if (timeoutProperties != null) {
            requestConfigBuilder.setConnectionRequestTimeout(
                    Timeout.of(timeoutProperties.getConnectRequestTimeout(), TimeUnit.MILLISECONDS));
        }

        return requestConfigBuilder.build();
    }
}