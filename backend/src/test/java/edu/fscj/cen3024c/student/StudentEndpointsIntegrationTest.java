package edu.fscj.cen3024c.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "CAPSTONE_TEST_DB_URL", matches = ".+")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=${CAPSTONE_TEST_DB_URL}",
    "app.demo-student.enabled=false",
    "app.jwt.secret=dGVzdC1vbmx5LXNpZ25pbmcta2V5LTMyLWJ5dGVzISE="
})
class StudentEndpointsIntegrationTest {
    private static final String VALIDATION_PATH = "/api/courses/validate-prerequisites";
    private static final String TEST_PASSWORD = "integration-test-password";

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate database;

    @Autowired
    private PasswordEncoder passwords;

    private final HttpClient client = HttpClient.newHttpClient();
    private final JsonMapper json = new JsonMapper();
    private Integer programID;
    private Integer studentID;
    private String email;

    @BeforeEach
    void addTestStudent() {
        email = "capstone-test-" + UUID.randomUUID() + "@example.test";
        programID = database.queryForObject("""
            INSERT INTO degree_program (name, degree_type, required_credits)
            VALUES ('Integration Test CIT', 'AS', 60)
            RETURNING program_id
            """, Integer.class);
        studentID = database.queryForObject("""
            INSERT INTO app_user (program_id, name, email, password_hash, role)
            VALUES (?, 'Test Student', ?, ?, 'STUDENT')
            RETURNING user_id
            """, Integer.class, programID, email, passwords.encode(TEST_PASSWORD));
        database.update("""
            INSERT INTO degree_program_course (program_id, course_id, requirement_type)
            SELECT ?, course_id, 'REQUIRED'
            FROM course WHERE course_code IN ('COP1000C', 'COP2800C')
            """, programID);
    }

    @AfterEach
    void deleteTestStudent() {
        if (studentID != null) {
            database.update("DELETE FROM student_course_progress WHERE user_id = ?", studentID);
            database.update("DELETE FROM app_user WHERE user_id = ?", studentID);
        }
        if (programID != null) {
            database.update("DELETE FROM degree_program_course WHERE program_id = ?", programID);
            database.update("DELETE FROM degree_program WHERE program_id = ?", programID);
        }
    }

       @Test
    void degreeProgressCountsOnlyCompletedCoursesInTheProgram() throws Exception {
        String path = "/api/students/" + studentID + "/degreeProgress";

        assertEquals(401, send(path, null, null).statusCode());

        String login = "{\"Email\":\"" + email + "\",\"Password\":\"" + TEST_PASSWORD + "\"}";
        HttpResponse<String> loginResponse = send("/api/v1/auth/login", login, null);
        assertEquals(200, loginResponse.statusCode());
        String token = json.readTree(loginResponse.body()).get("token").asString();

        HttpResponse<String> emptyProgress = send(path, null, token);
        assertEquals(200, emptyProgress.statusCode());
        assertEquals(0, json.readTree(emptyProgress.body()).get("totalCreditsCompleted").intValue());

        saveProgress("COP1000C", "COMPLETED");
        saveProgress("COP2800C", "IN_PROGRESS");
        saveProgress("COP2334C", "COMPLETED");

        HttpResponse<String> response = send(path, null, token);
        assertEquals(200, response.statusCode());
        JsonNode progress = json.readTree(response.body());
        assertEquals(studentID.intValue(), progress.get("studentID").intValue());
        assertEquals(60, progress.get("totalCreditsRequired").intValue());
        assertEquals(3, progress.get("totalCreditsCompleted").intValue());
        assertTrue(progress.get("GPA").isNull());

        database.update("UPDATE app_user SET program_id = NULL WHERE user_id = ?", studentID);
        HttpResponse<String> noProgram = send(path, null, token);
        assertEquals(404, noProgram.statusCode());
        assertEquals("No degree program assigned to this student",
            json.readTree(noProgram.body()).get("Message").asString());
        assertEquals(400, send("/api/students/invalid/degreeProgress", null, token).statusCode());
    }

    @Test
    void prerequisiteValidationReadsSavedProgressAndChecksInput() throws Exception {
        String request = "{\"studentID\":" + studentID + ",\"courseID\":\"cop 2800c\"}";
        assertEquals(401, send(VALIDATION_PATH, request, null).statusCode());

        String login = "{\"Email\":\"" + email + "\",\"Password\":\"" + TEST_PASSWORD + "\"}";
        HttpResponse<String> loginResponse = send("/api/v1/auth/login", login, null);
        assertEquals(200, loginResponse.statusCode());
        String token = json.readTree(loginResponse.body()).get("token").asString();

        HttpResponse<String> missing = send(VALIDATION_PATH, request, token);
        assertEquals(200, missing.statusCode());
        JsonNode result = json.readTree(missing.body());
        assertEquals("COP2800C", result.get("courseID").asString());
        assertFalse(result.get("meetsRequirements").asBoolean());
        assertEquals("COP1000C", result.get("missingPrerequisites").get(0).asString());

        saveProgress("COP1000C", "COMPLETED");
        HttpResponse<String> met = send(VALIDATION_PATH, request, token);
        assertEquals(200, met.statusCode());
        JsonNode updated = json.readTree(met.body());
        assertTrue(updated.get("meetsRequirements").asBoolean());
        assertTrue(updated.get("missingPrerequisites").isEmpty());

        assertEquals(400, send(VALIDATION_PATH, "{}", token).statusCode());
        assertEquals(400, send(VALIDATION_PATH,
            "{\"studentID\":1.5,\"courseID\":\"COP2800C\"}", token).statusCode());
        assertEquals(404, send(VALIDATION_PATH, request.replace("cop 2800c", "UNKNOWN"),
            token).statusCode());
    }

    private void saveProgress(String courseCode, String status) {
        database.update("""
            INSERT INTO student_course_progress (user_id, course_id, status)
            SELECT ?, course_id, ? FROM course WHERE course_code = ?
            """, studentID, status, courseCode);
    }

    private HttpResponse<String> send(String path, String body, String token)
            throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder(
            URI.create("http://localhost:" + port + path)).timeout(Duration.ofSeconds(5));
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        if (body != null) {
            request.header("Content-Type", "application/json");
            request.POST(HttpRequest.BodyPublishers.ofString(body));
        }
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
}
