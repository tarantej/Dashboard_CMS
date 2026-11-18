INSERT INTO public.technology
    (technology_name, technology_slug, technology_logo,
     technology_version, technology_category)

SELECT
    v.technology_name,
    v.technology_slug,
    NULL,
    NULL,
    v.technology_category
FROM (
    VALUES
    -- Programming Languages
    ('Kotlin', 'kotlin', 'Language'),
    ('Swift', 'swift', 'Language'),
    ('Rust', 'rust', 'Language'),
    ('Ruby', 'ruby', 'Language'),
    ('C', 'c', 'Language'),
    ('C++', 'cpp', 'Language'),
    ('Dart', 'dart', 'Language'),
    ('R', 'r', 'Language'),

    -- Java Ecosystem
    ('JDBC', 'jdbc', 'Java Technology'),
    ('Spring Framework', 'spring-framework', 'Framework'),
    ('Spring WebFlux', 'spring-webflux', 'Framework'),
    ('Spring Batch', 'spring-batch', 'Framework'),
    ('Spring Cloud', 'spring-cloud', 'Framework'),
    ('Jakarta EE', 'jakarta-ee', 'Framework'),

    -- JavaScript Ecosystem
    ('Node.js', 'nodejs', 'Runtime'),
    ('Express.js', 'expressjs', 'Framework'),
    ('Next.js', 'nextjs', 'Framework'),
    ('Nuxt', 'nuxt', 'Framework'),
    ('NestJS', 'nestjs', 'Framework'),
    ('Svelte', 'svelte', 'Framework'),
    ('Vite', 'vite', 'Build Tool'),

    -- Databases and Backend Services
    ('SQLite', 'sqlite', 'Database'),
    ('Elasticsearch', 'elasticsearch', 'Database'),
    ('Apache Cassandra', 'cassandra', 'Database'),
    ('Firebase', 'firebase', 'Backend Service'),
    ('Supabase', 'supabase', 'Backend Service'),

    -- DevOps and Version Control
    ('Git', 'git', 'Version Control'),
    ('GitHub', 'github', 'Version Control'),
    ('GitLab', 'gitlab', 'Version Control'),
    ('Jenkins', 'jenkins', 'DevOps'),
    ('GitHub Actions', 'github-actions', 'DevOps'),
    ('Terraform', 'terraform', 'DevOps'),
    ('Ansible', 'ansible', 'DevOps'),
    ('Helm', 'helm', 'DevOps'),

    -- Cloud and Hosting
    ('Google Cloud Platform', 'google-cloud-platform', 'Cloud'),
    ('DigitalOcean', 'digitalocean', 'Cloud'),
    ('Cloudflare', 'cloudflare', 'Cloud'),
    ('Vercel', 'vercel', 'Cloud'),
    ('Netlify', 'netlify', 'Cloud'),

    -- Design and Development Tools
    ('Figma', 'figma', 'Design Tool'),
    ('Adobe Photoshop', 'adobe-photoshop', 'Design Tool'),
    ('Adobe Illustrator', 'adobe-illustrator', 'Design Tool'),
    ('Postman', 'postman', 'API Tool'),
    ('Swagger / OpenAPI', 'swagger-openapi', 'API Tool'),
    ('IntelliJ IDEA', 'intellij-idea', 'IDE'),
    ('Visual Studio Code', 'visual-studio-code', 'IDE'),
    ('Strapi', 'strapi', 'CMS'),
    ('Sanity', 'sanity', 'CMS')

) AS v(technology_name, technology_slug, technology_category)

WHERE NOT EXISTS (
    SELECT 1
    FROM public.technology t
    WHERE LOWER(t.technology_name) = LOWER(v.technology_name)
       OR LOWER(t.technology_slug) = LOWER(v.technology_slug)
);



SELECT
    column_name,
    data_type,
    character_maximum_length,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'technology'
ORDER BY ordinal_position;
