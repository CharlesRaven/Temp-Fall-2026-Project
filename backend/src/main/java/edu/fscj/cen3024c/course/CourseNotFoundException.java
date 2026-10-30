package edu.fscj.cen3024c.course;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException() {
        super("Course ID not Found");
    }
}
