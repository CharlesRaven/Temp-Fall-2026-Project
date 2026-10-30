package edu.fscj.cen3024c.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository repository;

    public CourseService(CourseRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PrerequisitesResponse getPrerequisites(String courseCode) {
        String normalizedCode = "";
        for (char letter : courseCode.toCharArray()) {
            if (!Character.isWhitespace(letter)) {
                normalizedCode = normalizedCode + letter;
            }
        }
        normalizedCode = normalizedCode.toUpperCase(Locale.ROOT);

        Optional<Course> result = repository.findByCourseCode(normalizedCode);
        if (result.isEmpty()) {
            throw new CourseNotFoundException();
        }
        Course course = result.get();

        List<String> prerequisiteCodes = new ArrayList<>();
        for (Course prerequisite : course.getPrerequisites()) {
            prerequisiteCodes.add(prerequisite.getCourseCode());
        }
        Collections.sort(prerequisiteCodes);

        return new PrerequisitesResponse(course.getCourseCode(), prerequisiteCodes);
    }
}
