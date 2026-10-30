package edu.fscj.cen3024c.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final JwtEncoder tokens;
    private final String dummyHash;

    public AuthService(UserRepository users, PasswordEncoder passwords, JwtEncoder tokens) {
        this.users = users;
        this.passwords = passwords;
        this.tokens = tokens;
        this.dummyHash = passwords.encode(UUID.randomUUID().toString());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String password = request.getPassword();

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new InvalidCredentialsException();
        }

        Optional<AppUser> result = users.findByEmail(email);
        if (result.isEmpty()) {
            passwords.matches(password, dummyHash);
            throw new InvalidCredentialsException();
        }
        AppUser user = result.get();

        boolean passwordIsCorrect = passwords.matches(password, user.getPasswordHash());
        if (!passwordIsCorrect) {
            throw new InvalidCredentialsException();
        }

        // Spring Security builds and signs the token. It is valid for one hour.
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(3600);
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("fscj-degree-planner")
            .subject(user.getId().toString())
            .issuedAt(now)
            .expiresAt(expiresAt)
            .claim("scope", user.getRole())
            .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        JwtEncoderParameters parameters = JwtEncoderParameters.from(header, claims);
        Jwt token = tokens.encode(parameters);

        return new LoginResponse(token.getTokenValue(), "Bearer", "3600");
    }
}
