CREATE TABLE IF NOT EXISTS notification_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    body TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

ALTER TABLE announcements ADD COLUMN IF NOT EXISTS priority VARCHAR(20) DEFAULT 'NORMAL';

INSERT INTO notification_templates (name, subject, body) VALUES
    ('WELCOME', 'Welcome to {{institution}}', 'Dear {{name}},\n\nYour account has been created. Your student number is {{studentNo}}.\n\nWelcome aboard!'),
    ('ACCOUNT_ACTIVATED', 'Account Activated', 'Dear {{name}},\n\nYour account has been activated. You can now log in using your credentials.'),
    ('FEE_REMINDER', 'Fee Payment Reminder', 'Dear {{name}},\n\nThis is a reminder that your fee payment of {{amount}} is due on {{dueDate}}.\n\nPlease pay before the deadline to avoid penalties.'),
    ('EXAM_SCHEDULE', 'Exam Schedule Published', 'Dear {{name}},\n\nThe exam schedule for {{semester}} has been published. Please check your timetable for details.'),
    ('RESULT_PUBLISHED', 'Results Published', 'Dear {{name}},\n\nYour results for {{semester}} have been published. Please log in to view your grades.');
