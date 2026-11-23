package edu.fscj.cen3024c.student;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DegreeProgressResponse {
    public final int studentID;
    public final String degreeName;
    public final int totalCreditsRequired;
    public final long totalCreditsCompleted;

    // GPA is unavailable until the team defines how to calculate it.
    @JsonProperty("GPA")
    public final Double gpa = null;

    // Meeting a credit total alone does not mean the student can graduate.
    @JsonProperty("Status")
    public final String status = "In Progress";

    public DegreeProgressResponse(int studentID, String degreeName,
                                  int totalCreditsRequired, long totalCreditsCompleted) {
        this.studentID = studentID;
        this.degreeName = degreeName;
        this.totalCreditsRequired = totalCreditsRequired;
        this.totalCreditsCompleted = totalCreditsCompleted;
    }
}
