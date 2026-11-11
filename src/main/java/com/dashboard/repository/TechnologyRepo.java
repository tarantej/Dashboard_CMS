package com.dashboard.repository;

import com.dashboard.Technology;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnologyRepo extends JpaRepository<Technology, Integer>
{
    Page<Technology> findByTechnologyNameContainingIgnoreCaseOrTechnologySlugContainingIgnoreCaseOrTechnologyCategoryContainingIgnoreCase(
            String technologyName,
            String technologySlug,
            String technologyCategory,
            Pageable pageable
    );

    boolean existsByTechnologyNameIgnoreCase(String technologyName);

    boolean existsByTechnologySlugIgnoreCase(String technologySlug);

    // Check duplicate name, excluding the current technology
    boolean existsByTechnologyNameIgnoreCaseAndTechnologyIdNot(
            String technologyName,
            Integer technologyId
    );

    // Check duplicate slug, excluding the current technology
    boolean existsByTechnologySlugIgnoreCaseAndTechnologyIdNot(
            String technologySlug,
            Integer technologyId
    );
}