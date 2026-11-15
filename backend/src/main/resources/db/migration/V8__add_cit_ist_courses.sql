-- FSCJ 2026-27 IST catalog: https://catalog.fscj.edu/programs/S301/curriculum-RXQzS
-- Prerequisite links are for required courses. Alternatives, minimum grades and corequisites are in course notes.

-- Add the IST degree program if it is not already present.
INSERT INTO degree_program (name, degree_type, required_credits)
SELECT 'Information Systems Technology', 'BAS', 120
WHERE NOT EXISTS (
    SELECT 1 FROM degree_program
    WHERE name = 'Information Systems Technology' AND degree_type = 'BAS'
);

-- Add IST courses and the needed supporting prerequisite. V3 supplies the starters.
INSERT INTO course (course_code, title, credit_hours) VALUES
    ('AMH2010', 'United States History to 1877', 3),
    ('AMH2020', 'United States History from 1877 to the Present', 3),
    ('BUL3130', 'Business Law and Ethics', 3),
    ('CEN2071C', 'Secure Software Testing', 3),
    ('CEN3024C', 'Software Development I', 3),
    ('CEN3083C', 'Introduction to Cloud Computing', 3),
    ('CEN4025C', 'Software Development II', 3),
    ('CEN4802C', 'Software Integration, Configuration and Testing', 3),
    ('CEN4940', 'Application Development Capstone', 3),
    ('CET2600C', 'Network Fundamentals (Cisco 1)', 3),
    ('CGS1100C', 'Microcomputer Applications for Business and Economics', 3),
    ('CIS2321C', 'Information Systems', 3),
    ('CNT2001C', 'Computer Networks and Telecommunications', 3),
    ('COP2360C', 'Introduction to C#', 3),
    ('COP2551C', 'Introduction to Object Oriented Programming with Java', 3),
    ('COP2822C', 'Web Technologies', 4),
    ('COP2842C', 'Internet Programming', 4),
    ('COP3330C', 'Object-Oriented Programming', 3),
    ('COP3813C', 'Web Application Development', 3),
    ('COP4655C', 'Application Development for Mobile Devices', 3),
    ('COP4847C', 'Advanced Web Application Development', 3),
    ('CTS1120C', 'Fundamentals of Information Security', 3),
    ('CTS1131C', 'Hardware Configuration', 3),
    ('CTS1133C', 'Software Configuration', 3),
    ('CTS2437C', 'SQL Server I - Fundamentals', 3),
    ('ENC1101', 'English Composition I', 3),
    ('ENC1101C', 'English Composition I Enhanced', 4),
    ('ENC1102', 'Writing About Texts', 3),
    ('FIN3400', 'Financial Management', 3),
    ('FIN3450', 'Foundations of FinTech', 3),
    ('FIN3740', 'Data Ethics and Regulatory Concepts in FinTech', 3),
    ('FIN4451', 'FinTech Capstone', 3),
    ('GEB3213', 'Business Writing', 3),
    ('GEB4891', 'Strategic Management and Decision Making', 3),
    ('IDC3021C', 'Technology in FinTech', 3),
    ('IDC4022C', 'Machine Learning for FinTech', 3),
    ('IDC4251C', 'Data Mining for FinTech', 3),
    ('IDC4252C', 'Data Analytics for FinTech', 3),
    ('ISM3013', 'Introduction to Information Technology Management', 3),
    ('ISM3113C', 'Systems Analysis and Design', 3),
    ('ISM3232C', 'FinTech Application Development', 3),
    ('ISM4117C', 'Business Intelligence', 3),
    ('ISM4212C', 'Database and Physical Design', 3),
    ('ISM4220C', 'Network Management for Information Professionals', 3),
    ('ISM4302', 'Information Technology Planning', 3),
    ('ISM4480', 'Electronic Commerce Systems and Strategies', 3),
    ('ISM4881', 'Capstone in Information Technology Management', 3),
    ('MAN2582', 'Introduction to Project Management', 3),
    ('MAN3240', 'Organizational Behavior', 3),
    ('POS2041', 'American Federal Government', 3),
    ('SPC2017', 'Introduction to Speech Communications', 3),
    ('SPC2065', 'Speech Communication for Business and the Professions', 3),
    ('SPC2608', 'Fundamentals of Public Speaking', 3),
    ('STA2023', 'Elementary Statistics', 3)
ON CONFLICT (course_code) DO NOTHING;

-- Save the catalog rules as course notes so alternatives and grade rules are visible.
UPDATE course
SET description = CASE
    WHEN course.description IS NULL OR btrim(course.description) = '' THEN catalog.note
    ELSE course.description || E'\n' || catalog.note
END
FROM (VALUES
    ('AMH2010', 'Prerequisites: Qualify for enrollment in (ENC1101 or ENC1101C).'),
    ('AMH2020', 'Prerequisites: Qualify for enrollment in (ENC1101 or ENC1101C).'),
    ('BUL3130', 'Junior standing or higher.'),
    ('CEN2071C', 'Prerequisites: (COP2551C or COP2334C or COP2360C or COP2837C or COP2220C or COP2800C or COP2034C).'),
    ('CEN3024C', 'Prerequisites: COP3330C.'),
    ('CEN3083C', 'Prerequisites: (CNT2001C or CET2600C).'),
    ('CEN4025C', 'Prerequisites: CEN3024C. Corequisites: CEN4802C.'),
    ('CEN4802C', 'Prerequisites: (CEN3024C and CEN2071C). Corequisites: CEN4025C.'),
    ('CEN4940', 'Prerequisites: (CEN4025C and COP4655C and CEN4802C and COP4847C). Course grade: C or higher. Senior standing or higher.'),
    ('CET2600C', 'Prerequisites: (CTS1131C and CTS1133C). Course grade: C or higher.'),
    ('CIS2321C', 'Prerequisites: CGS1100C.'),
    ('CNT2001C', 'Prerequisites: (CGS1060C or CGS1100C).'),
    ('COP1000C', 'MAT1033 with C or higher or a qualifying math placement score is suggested.'),
    ('COP2334C', 'Prerequisites: COP1000C. Course grade: C or higher. COP2220C is recommended.'),
    ('COP2360C', 'Prerequisites: COP1000C.'),
    ('COP2551C', 'Prerequisites: COP1000C. Course grade: C or higher.'),
    ('COP2800C', 'Prerequisites: COP1000C. Course grade: C or higher.'),
    ('COP2822C', 'Prerequisites: COP1000C.'),
    ('COP2842C', 'Prerequisites: COP2822C.'),
    ('COP3330C', 'Prerequisites: (COP2551C or COP2800C or COP2360C).'),
    ('COP4655C', 'Prerequisites: (CTS2437C and COP3330C and CEN2071C and ISM3113C).'),
    ('COP4847C', 'Prerequisites: (COP3330C and COP3813C).'),
    ('CTS1120C', 'Prerequisites: ((CTS1131C and CTS1133C) or (CGS1060C and CNT1015C and CNT2001C)). Course grade: C or higher.'),
    ('CTS2437C', 'Prerequisites: COP1000C.'),
    ('ENC1101', 'Prerequisites: qualifying placement test score for non-exempt students.'),
    ('ENC1101C', 'Prerequisites: qualifying placement test score for non-exempt students.'),
    ('ENC1102', 'Prerequisites: (ENC1101 or ENC1101C). Course grade: C or higher.'),
    ('FIN3400', 'Prerequisites: GEB3213. Corequisites: GEB3213.'),
    ('FIN4451', 'Prerequisites: (FIN3740 and ISM3232C).'),
    ('GEB3213', 'Prerequisites: (ENC1101 or ENC1101C). Course grade: C or higher.'),
    ('GEB4891', 'Prerequisites: GEB3213. Corequisites: GEB3213.'),
    ('IDC4022C', 'Prerequisites: (FIN3450 and IDC3021C).'),
    ('IDC4251C', 'Prerequisites: (FIN3450 and IDC3021C).'),
    ('IDC4252C', 'Prerequisites: (FIN3450 and IDC3021C).'),
    ('ISM3013', 'Junior standing or higher.'),
    ('ISM3113C', 'Prerequisites: (CIS2321C and (COP2800C or COP2551C or COP2334C or COP2360C) and CTS2437C). Course grade: C or higher. Junior standing or higher.'),
    ('ISM3232C', 'Prerequisites: (FIN3450 and IDC3021C).'),
    ('ISM4117C', 'Prerequisites: (CTS2437C and ISM4212C).'),
    ('ISM4212C', 'Prerequisites: CTS2437C. Course grade: C or higher.'),
    ('ISM4220C', 'Prerequisites: ((CTS1131C and CTS1133C and CNT2001C) or CET2600C). Course grade: C or higher. Junior standing or higher.'),
    ('ISM4302', 'Senior standing or higher.'),
    ('ISM4480', 'Prerequisites: (COP2800C or COP2551C or COP2842C). Course grade: C or higher. Senior standing or higher.'),
    ('ISM4881', 'Prerequisites: (ISM4480 and ISM4212C and ISM3113C). Course grade: C or higher. Senior standing or higher.'),
    ('POS2041', 'Prerequisites: Qualify for enrollment in (ENC1101 or ENC1101C).'),
    ('SPC2017', 'Prerequisites: (ENC1101 or ENC1101C). Course grade: C or higher.'),
    ('SPC2065', 'Prerequisites: (ENC1101 or ENC1101C). Course grade: C or higher.'),
    ('SPC2608', 'Prerequisites: (ENC1101 or ENC1101C). Course grade: C or higher.'),
    ('STA2023', 'Prerequisites: (qualifying placement test score or MAT1033). Course grade: C or higher.')
) AS catalog(course_code, note)
WHERE course.course_code = catalog.course_code
  AND (course.description IS NULL OR strpos(course.description, catalog.note) = 0);

-- Link courses to the IST program with their roadmap category.
INSERT INTO degree_program_course (program_id, course_id, requirement_type, required)
SELECT program.program_id, course.course_id, roadmap.requirement_type, roadmap.required
FROM (VALUES
    ('GEB3213', 'REQUIRED', TRUE),
    ('ISM3113C', 'REQUIRED', TRUE),
    ('ISM4117C', 'REQUIRED', TRUE),
    ('ISM4212C', 'REQUIRED', TRUE),
    ('CEN3024C', 'APP_DEVELOPMENT', FALSE),
    ('CEN3083C', 'APP_DEVELOPMENT', FALSE),
    ('CEN4025C', 'APP_DEVELOPMENT', FALSE),
    ('CEN4802C', 'APP_DEVELOPMENT', FALSE),
    ('CEN4940', 'APP_DEVELOPMENT', FALSE),
    ('COP3330C', 'APP_DEVELOPMENT', FALSE),
    ('COP3813C', 'APP_DEVELOPMENT', FALSE),
    ('COP4655C', 'APP_DEVELOPMENT', FALSE),
    ('COP4847C', 'APP_DEVELOPMENT', FALSE),
    ('FIN3450', 'FINTECH', FALSE),
    ('FIN3740', 'FINTECH', FALSE),
    ('FIN4451', 'FINTECH', FALSE),
    ('IDC3021C', 'FINTECH', FALSE),
    ('IDC4022C', 'FINTECH', FALSE),
    ('IDC4251C', 'FINTECH', FALSE),
    ('IDC4252C', 'FINTECH', FALSE),
    ('ISM3232C', 'FINTECH', FALSE),
    ('BUL3130', 'IT_MANAGEMENT', FALSE),
    ('GEB4891', 'IT_MANAGEMENT', FALSE),
    ('ISM3013', 'IT_MANAGEMENT', FALSE),
    ('ISM4220C', 'IT_MANAGEMENT', FALSE),
    ('ISM4302', 'IT_MANAGEMENT', FALSE),
    ('ISM4480', 'IT_MANAGEMENT', FALSE),
    ('ISM4881', 'IT_MANAGEMENT', FALSE),
    ('MAN3240', 'IT_MANAGEMENT', FALSE),
    ('FIN3400', 'FINTECH_OR_IT_MANAGEMENT', FALSE),
    ('CGS1100C', 'LOWER_DIVISION', TRUE),
    ('CIS2321C', 'LOWER_DIVISION', TRUE),
    ('COP1000C', 'LOWER_DIVISION', TRUE),
    ('CTS1120C', 'LOWER_DIVISION', TRUE),
    ('CTS1131C', 'LOWER_DIVISION', TRUE),
    ('CTS1133C', 'LOWER_DIVISION', TRUE),
    ('CTS2437C', 'LOWER_DIVISION', TRUE),
    ('CET2600C', 'NETWORKING_OPTION', FALSE),
    ('CNT2001C', 'NETWORKING_OPTION', FALSE),
    ('COP2360C', 'PROGRAMMING_OPTION', FALSE),
    ('COP2551C', 'PROGRAMMING_OPTION', FALSE),
    ('COP2800C', 'PROGRAMMING_OPTION', FALSE),
    ('COP2842C', 'PROGRAMMING_OPTION', FALSE),
    ('CEN2071C', 'LOWER_DIVISION_CONCENTRATION', FALSE),
    ('MAN2582', 'LOWER_DIVISION_CONCENTRATION', FALSE),
    ('ENC1102', 'GENERAL_EDUCATION', TRUE),
    ('STA2023', 'GENERAL_EDUCATION', TRUE),
    ('ENC1101', 'ENGLISH_OPTION', FALSE),
    ('ENC1101C', 'ENGLISH_OPTION', FALSE),
    ('SPC2017', 'SPEECH_OPTION', FALSE),
    ('SPC2065', 'SPEECH_OPTION', FALSE),
    ('SPC2608', 'SPEECH_OPTION', FALSE),
    ('AMH2010', 'CIVIC_LITERACY_OPTION', FALSE),
    ('AMH2020', 'CIVIC_LITERACY_OPTION', FALSE),
    ('POS2041', 'CIVIC_LITERACY_OPTION', FALSE)
) AS roadmap(course_code, requirement_type, required)
JOIN degree_program program
    ON program.degree_type = 'BAS' AND program.name = 'Information Systems Technology'
JOIN course ON course.course_code = roadmap.course_code
ON CONFLICT (program_id, course_id) DO NOTHING;

-- Add verified direct prerequisite links. V3 already added the starter links.
INSERT INTO course_prerequisite (course_id, prerequisite_course_id)
SELECT course.course_id, prerequisite.course_id
FROM (VALUES
    ('CEN3024C', 'COP3330C'),
    ('CEN4025C', 'CEN3024C'),
    ('CEN4802C', 'CEN2071C'),
    ('CEN4802C', 'CEN3024C'),
    ('CEN4940', 'CEN4025C'),
    ('CEN4940', 'CEN4802C'),
    ('CEN4940', 'COP4655C'),
    ('CEN4940', 'COP4847C'),
    ('CET2600C', 'CTS1131C'),
    ('CET2600C', 'CTS1133C'),
    ('CIS2321C', 'CGS1100C'),
    ('COP2360C', 'COP1000C'),
    ('COP2551C', 'COP1000C'),
    ('COP2822C', 'COP1000C'),
    ('COP2842C', 'COP2822C'),
    ('COP4655C', 'CEN2071C'),
    ('COP4655C', 'COP3330C'),
    ('COP4655C', 'CTS2437C'),
    ('COP4655C', 'ISM3113C'),
    ('COP4847C', 'COP3330C'),
    ('COP4847C', 'COP3813C'),
    ('CTS2437C', 'COP1000C'),
    ('FIN4451', 'FIN3740'),
    ('FIN4451', 'ISM3232C'),
    ('IDC4022C', 'FIN3450'),
    ('IDC4022C', 'IDC3021C'),
    ('IDC4251C', 'FIN3450'),
    ('IDC4251C', 'IDC3021C'),
    ('IDC4252C', 'FIN3450'),
    ('IDC4252C', 'IDC3021C'),
    ('ISM3113C', 'CIS2321C'),
    ('ISM3113C', 'CTS2437C'),
    ('ISM3232C', 'FIN3450'),
    ('ISM3232C', 'IDC3021C'),
    ('ISM4117C', 'CTS2437C'),
    ('ISM4117C', 'ISM4212C'),
    ('ISM4212C', 'CTS2437C'),
    ('ISM4881', 'ISM3113C'),
    ('ISM4881', 'ISM4212C'),
    ('ISM4881', 'ISM4480')
) AS catalog(course_code, prerequisite_code)
JOIN course ON course.course_code = catalog.course_code
JOIN course prerequisite ON prerequisite.course_code = catalog.prerequisite_code
ON CONFLICT (course_id, prerequisite_course_id) DO NOTHING;
