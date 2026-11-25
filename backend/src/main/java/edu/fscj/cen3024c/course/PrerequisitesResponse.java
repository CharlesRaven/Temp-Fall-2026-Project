package edu.fscj.cen3024c.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PrerequisitesResponse {

    @JsonProperty("courseID")
    private String courseID;

    @JsonProperty("Prerequisites")
    private List<String> prerequisites;

    @JsonProperty("prerequisiteGroups")
    private List<List<String>> prerequisiteGroups;

    public PrerequisitesResponse(
            String courseID,
            List<String> prerequisites) {

        this(courseID, prerequisites,
            prerequisites.stream()
                .map(code -> List.of(code))
                .toList());
    }

    public PrerequisitesResponse(
            String courseID,
            List<String> prerequisites,
            List<List<String>> prerequisiteGroups) {

        this.courseID = courseID;
        this.prerequisites = prerequisites;
        this.prerequisiteGroups = prerequisiteGroups;
    }

    public String getCourseID() {
        return courseID;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    public List<List<String>> getPrerequisiteGroups() {
        return prerequisiteGroups;
    }
}