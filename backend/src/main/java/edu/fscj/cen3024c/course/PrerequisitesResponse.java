package edu.fscj.cen3024c.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PrerequisitesResponse {
    @JsonProperty("courseID")
    private String courseID;

    @JsonProperty("Prerequisites")
    private List<String> prerequisites;

    public PrerequisitesResponse(String courseID, List<String> prerequisites) {
        this.courseID = courseID;
        this.prerequisites = prerequisites;
    }

    public String getCourseID() {
        return courseID;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }
}
