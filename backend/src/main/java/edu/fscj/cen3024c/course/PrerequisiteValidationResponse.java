package edu.fscj.cen3024c.course;

import java.util.List;

public class PrerequisiteValidationResponse {
    public final int studentID;
    public final String courseID;
    public final boolean meetsRequirements;
    public final List<String> missingPrerequisites;

    public PrerequisiteValidationResponse(int studentID, String courseID,
                                          List<String> missingPrerequisites) {
        this.studentID = studentID;
        this.courseID = courseID;
        this.missingPrerequisites = missingPrerequisites;
        this.meetsRequirements = missingPrerequisites.isEmpty();
    }
}
