
package edu.fscj.cen3024c.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @GetMapping("/{studentID}/degreeProgress")
    public Map<String, Object> getDegreeProgress(
            @PathVariable Long studentID) {

        return Map.of(
            "studentID", studentID,
            "degreeName", "Information Systems Technology",
            "totalCreditsRequired", 120,
            "totalCreditsCompleted", 75,
            "GPA", 3.6,
            "Status", "In Progress"
        );
    }
    @GetMapping("/{studentID}/completedCourses")
public Map<String, Object> getCompletedCourses(
        @PathVariable Long studentID) {

    return Map.of(
        "studentID", studentID,
        "completedCourses", java.util.List.of(
            "COP1000C",
            "COP2334C",
            "COP2800C"
        )
    ); 
}

}