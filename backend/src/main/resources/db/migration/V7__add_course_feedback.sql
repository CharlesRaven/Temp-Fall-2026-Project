CREATE TABLE course_feedback (
                                 course_feedback_id BIGSERIAL PRIMARY KEY,
                                 user_id INTEGER NOT NULL,
                                 course_id INTEGER NOT NULL,
                                 difficulty_rating INTEGER,
                                 workload_rating INTEGER,
                                 cost_rating INTEGER,
                                 comment TEXT,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP,

                                 CONSTRAINT fk_course_feedback_user
                                     FOREIGN KEY (user_id)
                                         REFERENCES app_user(user_id),

                                 CONSTRAINT fk_course_feedback_course
                                     FOREIGN KEY (course_id)
                                         REFERENCES course(course_id),

                                 CONSTRAINT uq_course_feedback
                                     UNIQUE (user_id, course_id),

                                 CONSTRAINT chk_course_feedback_difficulty
                                     CHECK (difficulty_rating BETWEEN 1 AND 5),

                                 CONSTRAINT chk_course_feedback_workload
                                     CHECK (workload_rating BETWEEN 1 AND 5),

                                 CONSTRAINT chk_course_feedback_cost
                                     CHECK (cost_rating BETWEEN 1 AND 5)
);