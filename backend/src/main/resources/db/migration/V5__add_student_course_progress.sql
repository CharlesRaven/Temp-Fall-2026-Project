CREATE TABLE student_course_progress (
                                         student_course_progress_id BIGSERIAL PRIMARY KEY,
                                         user_id INTEGER NOT NULL,
                                         course_id INTEGER NOT NULL,
                                         status VARCHAR(30) NOT NULL,
                                         grade VARCHAR(5),
                                         semester VARCHAR(20),
                                         year INTEGER,
                                         completed_at DATE,

                                         CONSTRAINT uq_student_course_progress
                                             UNIQUE (user_id, course_id),

                                         CONSTRAINT fk_student_course_progress_user
                                             FOREIGN KEY (user_id)
                                                 REFERENCES app_user(user_id),

                                         CONSTRAINT fk_student_course_progress_course
                                             FOREIGN KEY (course_id)
                                                 REFERENCES course(course_id),

                                         CONSTRAINT chk_student_course_progress_status
                                             CHECK (status IN (
                                                               'NOT_STARTED',
                                                               'PLANNED',
                                                               'IN_PROGRESS',
                                                               'COMPLETED',
                                                               'WITHDRAWN'
                                                 ))
);