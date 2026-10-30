-- Adds three starter courses checked in the FSCJ 2026-27 catalog on October 28, 2026.
-- COP1000C: https://catalog.fscj.edu/courses/1061401
-- COP2800C: https://catalog.fscj.edu/courses/1214611/requisites-zNdJt
-- COP2334C: https://catalog.fscj.edu/courses/1061701/requisites-zNdJt
-- These prerequisite links do not store the minimum grade of C.
-- COP2220C is recommended, so it is not added as a required prerequisite.
INSERT INTO course (course_code, title, credit_hours) VALUES
    ('COP1000C', 'Introduction to Computer Programming', 3),
    ('COP2800C', 'Java 1', 3),
    ('COP2334C', 'Object-Oriented Programming with C++', 3)
ON CONFLICT (course_code) DO NOTHING;

INSERT INTO course_prerequisite (course_id, prerequisite_course_id)
SELECT course.course_id, prerequisite.course_id
FROM course
CROSS JOIN course prerequisite
WHERE course.course_code IN ('COP2800C', 'COP2334C')
  AND prerequisite.course_code = 'COP1000C'
ON CONFLICT (course_id, prerequisite_course_id) DO NOTHING;
