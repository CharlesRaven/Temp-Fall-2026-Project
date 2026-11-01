CREATE TABLE semester_plan (
                               semester_plan_id BIGSERIAL PRIMARY KEY,
                               user_id INTEGER NOT NULL,
                               term VARCHAR(20) NOT NULL,
                               year INTEGER NOT NULL,
                               notes TEXT,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_semester_plan_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES app_user(user_id),

                               CONSTRAINT uq_semester_plan
                                   UNIQUE (user_id, term, year)
);

CREATE TABLE semester_plan_course (
                                      semester_plan_course_id BIGSERIAL PRIMARY KEY,
                                      semester_plan_id BIGINT NOT NULL,
                                      course_id INTEGER NOT NULL,

                                      CONSTRAINT fk_semester_plan_course_plan
                                          FOREIGN KEY (semester_plan_id)
                                              REFERENCES semester_plan(semester_plan_id)
                                              ON DELETE CASCADE,

                                      CONSTRAINT fk_semester_plan_course_course
                                          FOREIGN KEY (course_id)
                                              REFERENCES course(course_id),

                                      CONSTRAINT uq_semester_plan_course
                                          UNIQUE (semester_plan_id, course_id)
);