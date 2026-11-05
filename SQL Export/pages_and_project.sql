select * from pages


DROP TABLE IF EXISTS public.pages CASCADE;

CREATE TABLE public.pages (

    page_id SERIAL PRIMARY KEY,

    page_title VARCHAR(255) NOT NULL,

    page_slug VARCHAR(255) NOT NULL,

    page_url VARCHAR(500) NOT NULL,

    page_template VARCHAR(30) NOT NULL DEFAULT 'DEFAULT',

    page_content TEXT,

    page_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    meta_title VARCHAR(255),

    meta_description VARCHAR(500),

    display_order INTEGER DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_page_template
        CHECK (
            page_template IN (
                'LANDING',
                'DEFAULT',
                'DASHBOARD',
                'LOGIN'
            )
        ),

    CONSTRAINT chk_page_status
        CHECK (
            page_status IN (
                'DRAFT',
                'PUBLISHED',
                'TRASH'
            )
        ),

    CONSTRAINT uq_page_template_slug
        UNIQUE (page_template, page_slug),

    CONSTRAINT uq_page_url
        UNIQUE (page_url)

);

INSERT INTO public.pages
(
    page_title,
    page_slug,
    page_url,
    page_template,
    page_content,
    page_status,
    display_order
)
VALUES

-- Landing
('Home', 'index', '/', 'LANDING',
 NULL, 'PUBLISHED', 1),

-- Dashboard
('Dashboard', 'dashboard', '/dashboard', 'DASHBOARD',
 NULL, 'PUBLISHED', 2),

('Blank Page', 'blank', '/blank', 'DASHBOARD',
 NULL, 'PUBLISHED', 3),

('Profile', 'profile', '/profile', 'DASHBOARD',
 NULL, 'PUBLISHED', 4),

('Account Settings', 'account-settings', '/account-settings', 'DASHBOARD',
 NULL, 'PUBLISHED', 5),

('System Settings', 'system-settings', '/system-settings', 'DASHBOARD',
 NULL, 'PUBLISHED', 6),

('Activity Log', 'activity-log', '/activity-log', 'DASHBOARD',
 NULL, 'PUBLISHED', 7),

('Notifications', 'notifications', '/notifications', 'DASHBOARD',
 NULL, 'PUBLISHED', 8),

('Messages', 'messages', '/messages', 'DASHBOARD',
 NULL, 'PUBLISHED', 9),

-- Authentication
('Login', 'login', '/login', 'LOGIN',
 NULL, 'PUBLISHED', 10),

('Forgot Password', 'forgot-password', '/forgot-password', 'LOGIN',
 NULL, 'PUBLISHED', 11),

('Reset Password', 'reset-password', '/reset-password', 'LOGIN',
 NULL, 'PUBLISHED', 12),

('OAuth Signup', 'oauth-signup', '/oauth-signup', 'LOGIN',
 NULL, 'PUBLISHED', 13),

('OAuth Success', 'oauth-success', '/oauth-success', 'LOGIN',
 NULL, 'PUBLISHED', 14);


SELECT
    table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND (
      table_name ILIKE '%project%'
      OR table_name ILIKE '%technolog%'
      OR table_name ILIKE '%tag%'
      OR table_name ILIKE '%categor%'
  )
ORDER BY table_name;

SELECT
    table_name,
    column_name,
    data_type,
    character_maximum_length,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name IN (
      'projects',
      'project_status',
      'project_type',
      'technology',
      'project_technology'
  )
ORDER BY table_name, ordinal_position;


SELECT
    tc.table_name,
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints tc
LEFT JOIN information_schema.key_column_usage kcu
    ON tc.constraint_name = kcu.constraint_name
    AND tc.constraint_schema = kcu.constraint_schema
LEFT JOIN information_schema.constraint_column_usage ccu
    ON tc.constraint_name = ccu.constraint_name
    AND tc.constraint_schema = ccu.constraint_schema
WHERE tc.table_schema = 'public'
  AND tc.table_name IN (
      'projects',
      'project_status',
      'project_type',
      'technology',
      'project_technology'
  )
  AND tc.constraint_type IN ('PRIMARY KEY', 'FOREIGN KEY', 'UNIQUE')
ORDER BY tc.table_name, tc.constraint_type, tc.constraint_name;


SELECT
    table_name,
    column_name,
    data_type,
    is_nullable
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name IN ('project_status', 'project_type')
ORDER BY table_name, ordinal_position;

SELECT
    column_name,
    data_type,
    is_nullable
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'projects'
ORDER BY ordinal_position;

SELECT
    p.project_id,
    p.project_title,
    p.project_status,
    s.status_id,
    p.project_type,
    t.type_id
FROM projects p
LEFT JOIN project_status s
    ON LOWER(TRIM(p.project_status)) = LOWER(TRIM(s.status_name))
LEFT JOIN project_type t
    ON LOWER(TRIM(p.project_type)) = LOWER(TRIM(t.type_name))
ORDER BY p.project_id;


BEGIN;

ALTER TABLE projects
    ADD COLUMN status_id INTEGER,
    ADD COLUMN type_id INTEGER;

UPDATE projects
SET
    status_id = CASE project_id
        WHEN 1 THEN 2
        WHEN 2 THEN 5
        WHEN 3 THEN 5
        WHEN 4 THEN 5
    END,
    type_id = CASE project_id
        WHEN 1 THEN 2
        WHEN 2 THEN 3
        WHEN 3 THEN 3
        WHEN 4 THEN 1
    END
WHERE project_id IN (1, 2, 3, 4);

SELECT
    conname AS constraint_name,
    conrelid::regclass AS table_name,
    confrelid::regclass AS referenced_table
FROM pg_constraint
WHERE conrelid = 'projects'::regclass
  AND contype = 'f';

SELECT
    p.project_id,
    p.project_title,
    p.status_id,
    p.type_id
FROM projects p
LEFT JOIN project_status s
    ON p.status_id = s.status_id
LEFT JOIN project_type t
    ON p.type_id = t.type_id
ORDER BY p.project_id;


SELECT
    project_id,
    project_title,
    status_id,
    type_id
FROM projects
ORDER BY project_id;

ALTER TABLE projects
    ADD CONSTRAINT fk_projects_status
        FOREIGN KEY (status_id)
        REFERENCES project_status(status_id),
    ADD CONSTRAINT fk_projects_type
        FOREIGN KEY (type_id)
        REFERENCES project_type(type_id);

ALTER TABLE projects
    ALTER COLUMN status_id SET NOT NULL,
    ALTER COLUMN type_id SET NOT NULL;

-- ALTER TABLE projects
--     DROP COLUMN project_status,
--     DROP COLUMN project_type;

ROLLBACK;