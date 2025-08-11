package dev.config.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> roles = extractAuthorities(jwt);

        return new JwtAuthenticationToken(jwt, roles);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (Objects.nonNull(jwt.getClaim("realm_access"))) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            ObjectMapper mapper = new ObjectMapper();
            List<String> keycloakRoles = mapper.convertValue(realmAccess.get("roles"), new TypeReference<>(){});
            keycloakRoles
                    .forEach(authority -> grantedAuthorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", authority))));
        }
        if (Objects.nonNull(jwt.getClaim("scope"))) {
            String scope = jwt.getClaim("scope");
            String[] scopes = scope.split("\\s");
            Arrays.asList(scopes)
                    .forEach(authority -> grantedAuthorities.add(new SimpleGrantedAuthority(String.format("SCOPE_%s", authority))));
        }

        return grantedAuthorities;
    }
}
