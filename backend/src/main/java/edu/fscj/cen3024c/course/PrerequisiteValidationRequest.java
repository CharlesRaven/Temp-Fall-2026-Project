package edu.fscj.cen3024c.course;

import io.swagger.v3.oas.annotations.media.Schema;

public class PrerequisiteValidationRequest {
    // Check the JSON types before using these values. A decimal or quoted ID is invalid.
    @Schema(implementation = Integer.class, example = "1")
    public Object studentID;

    @Schema(implementation = String.class, example = "COP2800C")
    public Object courseID;

    public boolean hasValidFields() {
        return studentID instanceof Integer id && id > 0
            && courseID instanceof String code && !code.isBlank() && code.trim().length() <= 20;
    }
}
