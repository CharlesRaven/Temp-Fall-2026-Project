package edu.fscj.cen3024c.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class CourseService {

    private final CourseRepository repository;

    public CourseService(CourseRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PrerequisitesResponse getPrerequisites(String courseCode) {

        String normalizedCode = courseCode
            .replaceAll("\\s+", "")
            .toUpperCase(Locale.ROOT);

        Course course = repository.findByCourseCode(normalizedCode)
            .orElseThrow(CourseNotFoundException::new);

        List<String> prerequisiteCodes = new ArrayList<>();

        for (Course prerequisite : course.getPrerequisites()) {
            prerequisiteCodes.add(prerequisite.getCourseCode());
        }

        Collections.sort(prerequisiteCodes);

        Map<Integer, List<String>> groups = new LinkedHashMap<>();

        for (Object[] row : repository.findPrerequisiteGroups(normalizedCode)) {

            Integer groupID = ((Number) row[0]).intValue();
            String prerequisiteCode = (String) row[1];

            groups.computeIfAbsent(groupID, key -> new ArrayList<>())
                .add(prerequisiteCode);
        }

        List<List<String>> prerequisiteGroups =
            new ArrayList<>(groups.values());

        return new PrerequisitesResponse(
            course.getCourseCode(),
            prerequisiteCodes,
            prerequisiteGroups
        );
    }

    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return repository.findAll();
    }
}