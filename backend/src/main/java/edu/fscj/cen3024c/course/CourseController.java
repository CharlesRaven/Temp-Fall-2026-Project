package edu.fscj.cen3024c.course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    // CREATE
    @Operation(summary = "Create a new course")
    @ApiResponse(responseCode = "200", description = "Course created successfully")
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return service.createCourse(course);
    }

    // READ ALL
    @Operation(summary = "Get all courses")
    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    @GetMapping
    public List<Course> getAllCourses() {
        return service.getAllCourses();
    }

    // UPDATE
    @Operation(summary = "Update an existing course")
    @ApiResponse(responseCode = "200", description = "Course updated successfully")
    @ApiResponse(responseCode = "404", description = "Course not found")
    @PutMapping("/{id}")
    public Course updateCourse(
            @PathVariable Integer id,
            @RequestBody Course course) {

        return service.updateCourse(id, course);
    }

    // DELETE
    @Operation(summary = "Delete a course")
    @ApiResponse(responseCode = "204", description = "Course deleted successfully")
    @ApiResponse(responseCode = "404", description = "Course not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer id) {
        service.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    // EXISTING PREREQUISITES ENDPOINT
    @Operation(
            summary = "List a course's direct prerequisites",
            description = "Public endpoint for US-04. courseID is a course code, not the numeric database key."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Course found, including an empty list if no prerequisites are stored"
    )
    @ApiResponse(responseCode = "404", description = "Course ID not Found")
    @GetMapping("/{courseID}/prerequisites")
    public PrerequisitesResponse getPrerequisites(
            @PathVariable("courseID") String courseID) {

        return service.getPrerequisites(courseID);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Map<String, String>> courseNotFound() {
        return ResponseEntity.status(404).body(Map.of(
                "Error", "Not Found",
                "Message", "Course ID not Found"
        ));
    }
}