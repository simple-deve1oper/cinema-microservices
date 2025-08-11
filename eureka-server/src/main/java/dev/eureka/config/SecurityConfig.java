package dev.eureka.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eureka.dto.constant.Authority;
import jakarta.annotation.Priority;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthConverter jwtAuthConverter;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    @Priority(0)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatchers(customizer -> customizer
                        .requestMatchers("/eureka/apps", "/eureka/apps/**")
                        .requestMatchers("/actuator/**")
                )
                .oauth2ResourceServer(customizer -> customizer.jwt(configure -> configure.jwtAuthenticationConverter(jwtAuthConverter)))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/actuator/**").hasAuthority("SCOPE_metrics")
                        .anyRequest().hasAuthority("SCOPE_eureka")
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(CsrfConfigurer::disable)
                .build();
    }

    @Bean
    @Priority(1)
    public SecurityFilterChain uiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .oauth2Login(Customizer.withDefaults())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .anyRequest()
                        .hasRole("admin"))
                .build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        OidcUserService oidcUserService = new OidcUserService();
        return userRequest -> {
            OidcUser oidcUser = oidcUserService.loadUser(userRequest);
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            if (Objects.nonNull(oidcUser.getUserInfo().getClaim("realm_access"))) {
                Map<String, Object> realmAccess = oidcUser.getUserInfo().getClaim("realm_access");
                ObjectMapper mapper = new ObjectMapper();
                List<String> keycloakRoles = mapper.convertValue(realmAccess.get("roles"), new TypeReference<>(){});

                List<String> authorities = Arrays.stream(Authority.values()).map(Authority::getValue).toList();
                keycloakRoles.stream()
                        .filter(authorities::contains)
                        .forEach(authority -> grantedAuthorities.add(
                                new SimpleGrantedAuthority(String.format("ROLE_%s", authority)))
                        );
            }
            if (Objects.nonNull(oidcUser.getAuthorities())) {
                Collection<? extends GrantedAuthority> authorities = oidcUser.getAuthorities();
                if (!authorities.isEmpty()) {
                    authorities
                            .forEach(authority ->  {
                                        if (authority instanceof SimpleGrantedAuthority simpleGrantedAuthority) {
                                            grantedAuthorities.add(simpleGrantedAuthority);
                                        }
                                    }
                            );
                }
            }

            return new DefaultOidcUser(grantedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        };
    }
}
