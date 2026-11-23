package edu.fscj.cen3024c.course;

import edu.fscj.cen3024c.student.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService service;
    private final StudentService students;

    public CourseController(CourseService service, StudentService students) {
        this.service = service;
        this.students = students;
    }

    @GetMapping
    public List<Course> getAllCourses() {
    return service.getAllCourses();
    }

    @Operation(summary = "List a course's direct prerequisites", description = "Public endpoint for US-04. courseID is a course code, not the numeric database key.")
    @ApiResponse(responseCode = "200", description = "Course found, including an empty list if no prerequisites are stored")
    @ApiResponse(responseCode = "404", description = "Course ID not Found")
    @GetMapping("/{courseID}/prerequisites")
    public PrerequisitesResponse getPrerequisites(@PathVariable("courseID") String courseID) {
        return service.getPrerequisites(courseID);
    }

    @Operation(summary = "Check a student's completed courses against stored prerequisites")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/validate-prerequisites")
    public ResponseEntity<?> validatePrerequisites(
            @RequestBody PrerequisiteValidationRequest request) {
        if (!request.hasValidFields()) {
            return invalidPrerequisiteRequest();
        }

        return ResponseEntity.ok(students.validatePrerequisites(
            (Integer) request.studentID, (String) request.courseID));
    }

    @ExceptionHandler(StudentService.StudentProgressNotFoundException.class)
    public ResponseEntity<Map<String, String>> studentNotFound(
            StudentService.StudentProgressNotFoundException error) {
        return ResponseEntity.status(404).body(Map.of(
            "Error", "Not Found", "Message", error.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> invalidPrerequisiteRequest() {
        return ResponseEntity.badRequest().body(Map.of(
            "Error", "Bad Request",
            "Message", "Provide a positive whole-number studentID and a courseID of up to 20 characters"));
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Map<String, String>> courseNotFound() {
        return ResponseEntity.status(404).body(Map.of(
            "Error", "Not Found", "Message", "Course ID not Found"));
    }
}
