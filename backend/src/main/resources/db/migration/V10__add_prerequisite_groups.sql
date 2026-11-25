-- Support alternative prerequisite groups.
-- Courses within the same group use OR logic.
-- Different groups use AND logic.

ALTER TABLE course_prerequisite
ADD COLUMN prerequisite_group INTEGER;

-- Preserve existing prerequisites as individual requirements.
UPDATE course_prerequisite
SET prerequisite_group = prerequisite_course_id;

ALTER TABLE course_prerequisite
ALTER COLUMN prerequisite_group SET NOT NULL;

-- Add alternative prerequisites for COP3330C.
INSERT INTO course_prerequisite
    (course_id, prerequisite_course_id, prerequisite_group)
SELECT
    course.course_id,
    prerequisite.course_id,
    0
FROM course
CROSS JOIN course AS prerequisite
WHERE course.course_code = 'COP3330C'
AND prerequisite.course_code IN (
    'COP2551C',
    'COP2800C',
    'COP2360C'
)
ON CONFLICT (course_id, prerequisite_course_id)
DO UPDATE SET prerequisite_group = 0;