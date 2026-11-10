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
}