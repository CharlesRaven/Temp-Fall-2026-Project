package edu.fscj.cen3024c.student;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class StudentProgressRepository {
    private final JdbcTemplate database;

    public StudentProgressRepository(JdbcTemplate database) {
        this.database = database;
    }

    public List<Map<String, Object>> findStudent(int studentID) {
        return database.queryForList("""
            SELECT student.program_id, program.name, program.required_credits
            FROM app_user student
            LEFT JOIN degree_program program ON program.program_id = student.program_id
            WHERE student.user_id = ? AND student.role = 'STUDENT'
            """, studentID);
    }

    public List<Integer> findCompletedCreditHours(int studentID, int programID) {
        return database.queryForList("""
            SELECT course.credit_hours
            FROM student_course_progress progress
            JOIN degree_program_course requirement ON requirement.course_id = progress.course_id
            JOIN course ON course.course_id = progress.course_id
            WHERE progress.user_id = ? AND requirement.program_id = ?
              AND progress.status = 'COMPLETED'
            """, Integer.class, studentID, programID);
    }

    public List<String> findCompletedCourseCodes(int studentID) {
        return database.queryForList("""
            SELECT course.course_code
            FROM student_course_progress progress
            JOIN course ON course.course_id = progress.course_id
            WHERE progress.user_id = ? AND progress.status = 'COMPLETED'
            """, String.class, studentID);
    }
}
