package edu.fscj.cen3024c.course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @Operation(summary = "List a course's direct prerequisites", description = "Public endpoint for US-04. courseID is a course code, not the numeric database key.")
    @ApiResponse(responseCode = "200", description = "Course found, including an empty list if no prerequisites are stored")
    @ApiResponse(responseCode = "404", description = "Course ID not Found")
    @GetMapping("/{courseID}/prerequisites")
    public PrerequisitesResponse getPrerequisites(@PathVariable("courseID") String courseID) {
        return service.getPrerequisites(courseID);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Map<String, String>> courseNotFound() {
        return ResponseEntity.status(404).body(Map.of(
            "Error", "Not Found", "Message", "Course ID not Found"));
    }
}
