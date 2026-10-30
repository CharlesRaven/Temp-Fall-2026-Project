package edu.fscj.cen3024c.course;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @EntityGraph(attributePaths = "prerequisites")
    Optional<Course> findByCourseCode(String courseCode);
}
