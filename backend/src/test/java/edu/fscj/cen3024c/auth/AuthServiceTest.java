package edu.fscj.cen3024c.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verifyNoInteractions;

class AuthServiceTest {
    // A mock is a fake database connection used only by these tests.
    private final UserRepository users = mock(UserRepository.class);
    private final BCryptPasswordEncoder passwords = new BCryptPasswordEncoder(4);
    private final JwtEncoder encoder = mock(JwtEncoder.class);
    private final AuthService service = new AuthService(users, passwords, encoder);

    @Test
    void savedPasswordHashAuthenticatesAndIssuesToken() {
        AppUser user = mock(AppUser.class);
        when(user.getId()).thenReturn(7);
        when(user.getRole()).thenReturn("STUDENT");
        when(user.getPasswordHash()).thenReturn(passwords.encode("p@ssw0rd"));
        when(users.findByEmail("demo.student@example.com")).thenReturn(Optional.of(user));
        when(encoder.encode(any())).thenReturn(Jwt.withTokenValue("test-token")
            .header("alg", "HS256").subject("7").build());
        LoginResponse response = service.login(new LoginRequest(" DEMO.STUDENT@example.com ", "p@ssw0rd"));
        assertEquals("test-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("3600", response.getExpiresIn());
    }
    @Test
    void wrongPasswordDoesNotIssueToken() {
        AppUser user = new AppUser("Demo", "demo.student@example.com", passwords.encode("p@ssw0rd"));
        when(users.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertThrows(InvalidCredentialsException.class,
            () -> service.login(new LoginRequest(user.getEmail(), "wrong")));
        verifyNoInteractions(encoder);
    }
    @Test
    void unknownEmailDoesNotIssueToken() {
        when(users.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class,
            () -> service.login(new LoginRequest("missing@example.com", "p@ssw0rd")));
        verifyNoInteractions(encoder);
    }
    @Test
    void oversizedUnicodePasswordFailsWithoutServerError() {
        String longPassword = "";
        for (int i = 0; i < 40; i++) {
            longPassword = longPassword + "é";
        }
        LoginRequest request = new LoginRequest("demo.student@example.com", longPassword);
        assertThrows(InvalidCredentialsException.class, () -> service.login(request));
        verifyNoInteractions(encoder);
    }
}
