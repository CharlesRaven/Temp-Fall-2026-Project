package edu.fscj.cen3024c.student;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping("/{studentID}/degreeProgress")
    public ResponseEntity<?> getDegreeProgress(
            @PathVariable("studentID") Integer studentID,
            @AuthenticationPrincipal Jwt jwt) {

        if (studentID < 1) {
            return invalidStudentID();
        }

        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }

        if (!studentID.toString().equals(jwt.getSubject())) {
            return ResponseEntity.status(403).body(
                Map.of("Message", "Access denied"));
        }

        return ResponseEntity.ok(
            service.getDegreeProgress(studentID));
    }

    @GetMapping("/{studentID}/completedCourses")
    public ResponseEntity<?> getCompletedCourses(
            @PathVariable("studentID") Integer studentID,
            @AuthenticationPrincipal Jwt jwt) {

        if (studentID < 1) {
            return invalidStudentID();
        }

        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }

        if (!studentID.toString().equals(jwt.getSubject())) {
            return ResponseEntity.status(403).body(
                Map.of("Message", "Access denied"));
        }

        return ResponseEntity.ok(Map.of(
            "studentID", studentID,
            "completedCourses",
            service.getCompletedCourses(studentID)
        ));
    }

    @ExceptionHandler(
        StudentService.StudentProgressNotFoundException.class)
    public ResponseEntity<Map<String, String>> progressNotFound(
            StudentService.StudentProgressNotFoundException error) {

        return ResponseEntity.status(404).body(Map.of(
            "Error", "Not Found",
            "Message", error.getMessage()
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> invalidStudentID() {

        return ResponseEntity.badRequest().body(Map.of(
            "Error", "Bad Request",
            "Message", "studentID must be a positive whole number"
        ));
    }
}