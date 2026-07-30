CREATE TABLE IF NOT EXISTS staff_courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id UUID NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE(staff_id, course_id)
);

CREATE INDEX IF NOT EXISTS idx_staff_courses_staff_id ON staff_courses(staff_id);
CREATE INDEX IF NOT EXISTS idx_staff_courses_course_id ON staff_courses(course_id);
