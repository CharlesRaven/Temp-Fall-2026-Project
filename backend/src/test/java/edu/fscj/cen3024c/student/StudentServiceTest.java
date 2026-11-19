package edu.fscj.cen3024c.student;

import edu.fscj.cen3024c.course.CourseService;
import edu.fscj.cen3024c.course.PrerequisiteValidationResponse;
import edu.fscj.cen3024c.course.PrerequisitesResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StudentServiceTest {
    private final StudentProgressRepository students = mock(StudentProgressRepository.class);
    private final CourseService courses = mock(CourseService.class);
    private final StudentService service = new StudentService(students, courses);

    @Test
    void addsCreditsFromCompletedProgramCourses() {
        when(students.findStudent(1)).thenReturn(List.of(Map.of(
            "program_id", 2, "name", "Computer Information Technology", "required_credits", 60)));
        when(students.findCompletedCreditHours(1, 2)).thenReturn(List.of(3, 4));

        DegreeProgressResponse result = service.getDegreeProgress(1);

        assertEquals(60, result.totalCreditsRequired);
        assertEquals(7, result.totalCreditsCompleted);
    }

    @Test
    void explainsWhenStudentHasNoAssignedProgram() {
        when(students.findStudent(1)).thenReturn(List.of(Map.of()));
        var error = assertThrows(StudentService.StudentProgressNotFoundException.class,
            () -> service.getDegreeProgress(1));

        assertEquals("No degree program assigned to this student", error.getMessage());
    }

    @Test
    void listsOnlyPrerequisitesTheStudentStillNeeds() {
        when(students.findStudent(1)).thenReturn(List.of(Map.of("program_id", 2)));
        when(students.findCompletedCourseCodes(1)).thenReturn(List.of("COP1000C"));
        when(courses.getPrerequisites("COP2800C")).thenReturn(new PrerequisitesResponse(
            "COP2800C", List.of("COP1000C", "COP2220C")));

        PrerequisiteValidationResponse result = service.validatePrerequisites(1, "COP2800C");

        assertFalse(result.meetsRequirements);
        assertEquals(List.of("COP2220C"), result.missingPrerequisites);
    }

    @Test
    void acceptsCourseWithNoStoredPrerequisites() {
        when(students.findStudent(1)).thenReturn(List.of(Map.of("program_id", 2)));
        when(courses.getPrerequisites("COP1000C")).thenReturn(
            new PrerequisitesResponse("COP1000C", List.of()));

        PrerequisiteValidationResponse result = service.validatePrerequisites(1, "COP1000C");

        assertTrue(result.meetsRequirements);
        assertTrue(result.missingPrerequisites.isEmpty());
    }
}
