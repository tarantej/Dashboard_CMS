ALTER TABLE project_type
ADD COLUMN type_color VARCHAR(50);

UPDATE project_type
SET type_color = CASE type_name
    WHEN 'Personal' THEN 'primary'
    WHEN 'Client' THEN 'success'
    WHEN 'Learning' THEN 'info'
    WHEN 'University' THEN 'warning'
    WHEN 'Open Source' THEN 'secondary'
END;

UPDATE public.projects
SET project_feature_image = '/img/portfolio/sherni-cover.jpg'
WHERE project_slug = 'sherni-ecommerce';

UPDATE public.projects
SET project_feature_image = '/img/portfolio/khl-cover.jpg'
WHERE project_slug = 'karen-harding-lawyer';


ALTER TABLE public.project_technology
RENAME TO project_tags;

ALTER TABLE public.project_tags
RENAME CONSTRAINT fk_project_technology_project
TO fk_project_tags_project;

ALTER TABLE public.project_tags
RENAME CONSTRAINT fk_project_technology_tech
TO fk_project_tags_tag;

ALTER TABLE public.project_tags
RENAME CONSTRAINT project_technology_pkey
TO project_tags_pkey;

ALTER TABLE public.projects
ADD COLUMN project_logo VARCHAR(255);


SELECT
    p.project_id,
    p.project_title,
    t.technology_name
FROM projects p
LEFT JOIN project_tags pt
    ON p.project_id = pt.project_id
LEFT JOIN technology t
    ON pt.technology_id = t.technology_id
ORDER BY p.updated_at DESC
LIMIT 15;