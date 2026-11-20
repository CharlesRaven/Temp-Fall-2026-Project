
package edu.fscj.cen3024c.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    name = "app.demo-admin.enabled",
    havingValue = "true"
)
public class DemoAdminSetup implements CommandLineRunner {

    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final String password;

    public DemoAdminSetup(
            UserRepository users,
            PasswordEncoder passwords,
            @Value("${app.demo-admin.password}") String password) {
        this.users = users;
        this.passwords = passwords;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        String email = "demo.admin@example.com";

        if (users.findByEmail(email).isEmpty()) {
            String passwordHash = passwords.encode(password);

            AppUser admin = new AppUser(
                "Demo Administrator",
                email,
                passwordHash,
                "ADMIN"
            );

            users.save(admin);
        }
    }
}