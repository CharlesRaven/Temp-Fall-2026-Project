package edu.fscj.cen3024c.course;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer id;

    @Column(name = "course_code", nullable = false, unique = true, length = 20)
    private String courseCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "credit_hours", nullable = false)
    private Integer creditHours;

    @Column(columnDefinition = "text")
    private String description;

    // The join table stores each course/prerequisite pair; catalog courses are shared.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "course_prerequisite",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "prerequisite_course_id"))
    private Set<Course> prerequisites = new HashSet<>();

    protected Course() {
        // Required by JPA when loading a course from the database.
    }

    public String getCourseCode() {
        return courseCode;
    }

    public Set<Course> getPrerequisites() {
        return prerequisites;
    }

}
