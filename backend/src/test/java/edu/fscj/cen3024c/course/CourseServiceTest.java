package edu.fscj.cen3024c.course;

import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourseServiceTest {
    // A mock is a fake database connection used only by these tests.
    private final CourseRepository repository = mock(CourseRepository.class);
    private final CourseService service = new CourseService(repository);

    @Test
    void normalizesCodesAndReturnsSortedDirectPrerequisites() {
        Course course = mock(Course.class);
        Course first = mock(Course.class);
        Course second = mock(Course.class);
        when(course.getCourseCode()).thenReturn("DEMO2000");
        when(first.getCourseCode()).thenReturn("DEMO1000");
        when(second.getCourseCode()).thenReturn("DEMO1001");
        when(course.getPrerequisites()).thenReturn(Set.of(second, first));
        when(repository.findByCourseCode("DEMO2000")).thenReturn(Optional.of(course));
        PrerequisitesResponse response = service.getPrerequisites(" demo 2000 ");
        assertEquals("DEMO2000", response.getCourseID());
        assertEquals(java.util.List.of("DEMO1000", "DEMO1001"), response.getPrerequisites());
    }

    @Test
    void existingCourseWithoutPrerequisitesReturnsEmptyList() {
        Course course = mock(Course.class);
        when(course.getCourseCode()).thenReturn("DEMO1000");
        when(course.getPrerequisites()).thenReturn(Set.of());
        when(repository.findByCourseCode("DEMO1000")).thenReturn(Optional.of(course));
        assertTrue(service.getPrerequisites("DEMO1000").getPrerequisites().isEmpty());
    }

    @Test
    void missingCourseIsDistinctFromCourseWithoutPrerequisites() {
        when(repository.findByCourseCode("UNKNOWN")).thenReturn(Optional.empty());
        assertThrows(CourseNotFoundException.class, () -> service.getPrerequisites("UNKNOWN"));
    }
}
