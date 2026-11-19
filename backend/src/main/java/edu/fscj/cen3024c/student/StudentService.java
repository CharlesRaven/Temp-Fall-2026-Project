package edu.fscj.cen3024c.student;

import edu.fscj.cen3024c.course.CourseService;
import edu.fscj.cen3024c.course.PrerequisiteValidationResponse;
import edu.fscj.cen3024c.course.PrerequisitesResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {
    private final StudentProgressRepository students;
    private final CourseService courses;

    public StudentService(StudentProgressRepository students, CourseService courses) {
        this.students = students;
        this.courses = courses;
    }

    public DegreeProgressResponse getDegreeProgress(int studentID) {
        Map<String, Object> student = findStudent(studentID);
        Integer programID = (Integer) student.get("program_id");
        if (programID == null) {
            throw new StudentProgressNotFoundException("No degree program assigned to this student");
        }

        // The repository returns credits only for completed courses linked to this program.
        long completedCredits = 0;
        for (int creditHours : students.findCompletedCreditHours(studentID, programID)) {
            completedCredits += creditHours;
        }

        return new DegreeProgressResponse(studentID, (String) student.get("name"),
            (int) student.get("required_credits"), completedCredits);
    }

    public PrerequisiteValidationResponse validatePrerequisites(int studentID, String courseCode) {
        findStudent(studentID);
        PrerequisitesResponse prerequisites = courses.getPrerequisites(courseCode);
        List<String> completedCourses = students.findCompletedCourseCodes(studentID);
        List<String> missingPrerequisites = new ArrayList<>();

        for (String requiredCourse : prerequisites.getPrerequisites()) {
            if (!completedCourses.contains(requiredCourse)) {
                missingPrerequisites.add(requiredCourse);
            }
        }

        return new PrerequisiteValidationResponse(studentID, prerequisites.getCourseID(),
            missingPrerequisites);
    }

    private Map<String, Object> findStudent(int studentID) {
        List<Map<String, Object>> results = students.findStudent(studentID);
        if (results.isEmpty()) {
            throw new StudentProgressNotFoundException("Student not found");
        }
        return results.get(0);
    }

    public static class StudentProgressNotFoundException extends RuntimeException {
        public StudentProgressNotFoundException(String message) {
            super(message);
        }
    }
}
