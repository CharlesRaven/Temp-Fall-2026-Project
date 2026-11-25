package edu.fscj.cen3024c.course;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    @EntityGraph(attributePaths = "prerequisites")
    Optional<Course> findByCourseCode(String courseCode);

    @Query(value = """
        SELECT cp.prerequisite_group, p.course_code
        FROM course_prerequisite cp
        JOIN course c
            ON c.course_id = cp.course_id
        JOIN course p
            ON p.course_id = cp.prerequisite_course_id
        WHERE c.course_code = :courseCode
        ORDER BY cp.prerequisite_group, p.course_code
        """, nativeQuery = true)
    List<Object[]> findPrerequisiteGroups(
        @Param("courseCode") String courseCode
    );
}