package edu.fscj.cen3024c.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service;
    private final UserRepository users;

    public AuthController(AuthService service, UserRepository users) {
        this.service = service;
        this.users = users;
    }

    @Operation(summary = "Log in with a saved student account")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = service.login(request);
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(response);
    }

    @Operation(summary = "Show the account for the supplied Bearer token")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<Object> currentUser(@AuthenticationPrincipal Jwt token) {
        Integer userId = Integer.valueOf(token.getSubject());
        Optional<AppUser> result = users.findById(userId);

        if (result.isEmpty()) {
            Map<String, String> error = Map.of(
                "Error", "Unauthorized", "Message", "Account not Found");
            return ResponseEntity.status(401).body(error);
        }

        AppUser user = result.get();
        CurrentUserResponse response = new CurrentUserResponse(
            user.getId(), user.getName(), user.getEmail(), user.getRole());
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> invalidCredentials() {
        Map<String, String> error = Map.of(
            "Error", "Not Found", "Message", "Email or Password Invalid");
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, String>> invalidRequest() {
        Map<String, String> error = Map.of(
            "Error", "Bad Request", "Message", "Invalid Request or Missing Fields");
        return ResponseEntity.badRequest().body(error);
    }
}
