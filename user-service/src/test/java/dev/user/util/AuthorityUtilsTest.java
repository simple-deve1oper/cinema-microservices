package dev.user.util;

import dev.library.core.exception.ServerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.List;

public class AuthorityUtilsTest {
    @Test
    void getRoleAfterFilterByAuthority_ok() {
        RoleRepresentation roleRepresentationManager = new RoleRepresentation(
                "manager",
                "Role for manager user",
                true
        );
        RoleRepresentation roleRepresentationDefault = new RoleRepresentation(
                "default",
                "default",
                false
        );

        RoleRepresentation response = AuthorityUtils.getRoleAfterFilterByAuthority(
                List.of(roleRepresentationManager, roleRepresentationDefault)
        );
        Assertions.assertEquals("manager", response.getName());
    }

    @Test
    void getRoleAfterFilterByAuthority_serverException() {
        RoleRepresentation roleRepresentationDefault = new RoleRepresentation(
                "default",
                "default",
                false
        );
        RoleRepresentation roleRepresentationSimple = new RoleRepresentation(
                "simple",
                "",
                true
        );

        ServerException exception = Assertions
                .assertThrows(
                        ServerException.class,
                        () -> AuthorityUtils.getRoleAfterFilterByAuthority(
                                List.of(roleRepresentationDefault, roleRepresentationSimple)
                        )
                );
        var expectedMessage = "Произошла ошибка фильтрации ролей пользователя";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);
    }
}
