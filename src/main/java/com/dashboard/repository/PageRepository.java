package com.dashboard.repository;

import com.dashboard.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer>
{
    // Find pages by their template
    List<Page> findByPageTemplate(String pageTemplate);

    // Find pages by their status
    List<Page> findByPageStatus(String pageStatus);

    // Find a page using its slug and template
    Optional<Page> findByPageSlugAndPageTemplate(
            String pageSlug,
            String pageTemplate
    );

    //  Find page by URL

    Optional<Page> findByPageUrl(String pageUrl);

    // Search pages by title
    List<Page> findByPageTitleContainingIgnoreCase(String pageTitle);
}