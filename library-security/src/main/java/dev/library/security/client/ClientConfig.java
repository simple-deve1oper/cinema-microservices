package dev.library.security.client;

import dev.library.domain.booking.client.BookingClient;
import dev.library.domain.dictionary.country.client.CountryClient;
import dev.library.domain.dictionary.participant.client.ParticipantClient;
import dev.library.domain.movie.client.MovieClient;
import dev.library.domain.session.client.PlaceClient;
import dev.library.domain.session.client.SessionClient;
import dev.library.domain.user.client.UserClient;
import io.micrometer.observation.ObservationRegistry;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.RestClientTimeoutProperties;
import org.springframework.cloud.netflix.eureka.http.EurekaClientHttpRequestFactorySupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.observation.DefaultClientRequestObservationConvention;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
@PropertySource("classpath:resilience4j.properties")
public class ClientConfig {
    private final ClientInterceptor clientInterceptor;

    @Value("${url.country:}")
    private String countryUrl;
    @Value("${url.participant:}")
    private String participantUrl;
    @Value("${url.place:}")
    private String placeUrl;
    @Value("${url.session:}")
    private String sessionUrl;
    @Value("${url.user:}")
    private String userUrl;
    @Value("${url.movie:}")
    private String movieUrl;
    @Value("${url.booking:}")
    private String bookingUrl;

    public ClientConfig(ClientInterceptor clientInterceptor) {
        this.clientInterceptor = clientInterceptor;
    }

    @Bean
    public CountryClient countryClient(RestClient.Builder loadBalancedRestClientBuilder, ObservationRegistry observationRegistry) {
        RestClient restClient = getRestClient(loadBalancedRestClientBuilder, observationRegistry, countryUrl);
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();

        return httpProxyFactory.createClient(CountryClient.class);
    }

    @Bean
    public ParticipantClient participantClient(RestClient.Builder loadBalancedRestClientBuilder, ObservationRegistry observationRegistry) {
        RestClient restClient = getRestClient(loadBalancedRestClientBuilder, observationRegistry, participantUrl);
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();

        return httpProxyFactory.createClient(ParticipantClient.class);
    }

    @Bean
    public SessionClient sessionClient(RestClient.Builder loadBalancedRestClientBuilder, ObservationRegistry observationRegistry) {
        RestClient restClient = getRestClient(loadBalancedRestClientBuilder, observationRegistry, sessionUrl);
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();

        return httpProxyFactory.createClient(SessionClient.class);
    }

    @Bean
    public PlaceClient placeClient(RestClient.Builder loadBalancedRestClientBuilder, ObservationRegistry observationRegistry) {
        RestClient restClient = getRestClient(loadBalancedRestClientBuilder, observationRegistry, placeUrl);
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();

        return httpProxyFactory.createClient(PlaceClient.class);
    }

    @Bean
    public UserClient userClient(RestClient.Builder loadBalancedRestClientBuilder, ObservationRegistry observationRegistry) {
        RestClient restClient = getRestClient(loadBalancedRestClientBuilder, observationRegistry, userUrl);
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();

        return httpProxyFactory.createClient(UserClient.class);
    }

    @Bean
    public MovieClient movieClient(RestClient.Builder loadBalancedRestClientBuilder, ObservationRegistry observationRegistry) {
        RestClient restClient = getRestClient(loadBalancedRestClientBuilder, observationRegistry, movieUrl);
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();

        return httpProxyFactory.createClient(MovieClient.class);
    }

    @Bean
    public BookingClient bookingClient(RestClient.Builder loadBalancedRestClientBuilder, ObservationRegistry observationRegistry) {
        RestClient restClient = getRestClient(loadBalancedRestClientBuilder, observationRegistry, bookingUrl);
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();

        return httpProxyFactory.createClient(BookingClient.class);
    }

    @Bean
    @Scope("prototype")
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    private ClientHttpRequestFactory getClientRequestFactory() {
        PoolingHttpClientConnectionManager connectionManager = getConnectionManager();
        CloseableHttpClient httpClient = getHttpClient(connectionManager);

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    public EurekaClientHttpRequestFactorySupplier eurekaClientHttpRequestFactorySupplier(
            RestClientTimeoutProperties timeoutProperties,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        return new KeycloakEurekaClientHttpRequestFactorySupplier(timeoutProperties, clientRegistrationRepository, authorizedClientService);
    }

    private PoolingHttpClientConnectionManager getConnectionManager() {
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.of(Duration.ofSeconds(3)))
                .setSocketTimeout(Timeout.of(Duration.ofSeconds(3)))
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultConnectionConfig(connectionConfig);

        return connectionManager;
    }

    private CloseableHttpClient getHttpClient(PoolingHttpClientConnectionManager connectionManager) {
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }

    private RestClient getRestClient(
            RestClient.Builder loadBalancedRestClientBuilder,
            ObservationRegistry observationRegistry,
            String baseUrl
    ) {
        return loadBalancedRestClientBuilder
                .observationRegistry(observationRegistry)
                .observationConvention(new DefaultClientRequestObservationConvention())
                .baseUrl(baseUrl)
                .requestFactory(getClientRequestFactory())
                .requestInterceptor(clientInterceptor)
                .build();
    }
}
