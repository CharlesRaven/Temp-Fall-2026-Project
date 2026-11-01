CREATE TABLE degree_program_course (
                                       degree_program_course_id BIGSERIAL PRIMARY KEY,
                                       program_id INTEGER NOT NULL,
                                       course_id INTEGER NOT NULL,
                                       requirement_type VARCHAR(30) NOT NULL,
                                       recommended_semester INTEGER,
                                       required BOOLEAN NOT NULL DEFAULT TRUE,

                                       CONSTRAINT uq_degree_program_course
                                           UNIQUE (program_id, course_id),

                                       CONSTRAINT fk_degree_program_course_program
                                           FOREIGN KEY (program_id)
                                               REFERENCES degree_program(program_id),

                                       CONSTRAINT fk_degree_program_course_course
                                           FOREIGN KEY (course_id)
                                               REFERENCES course(course_id)
);