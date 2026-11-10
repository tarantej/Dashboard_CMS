package com.dashboard.service;


import com.dashboard.Technology;
import com.dashboard.repository.TechnologyRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@Service
public class TechnologyService
{
    private final TechnologyRepo technologyRepo;

    public TechnologyService(TechnologyRepo technologyRepo) {
        this.technologyRepo = technologyRepo;
    }

    // Retrieve all technologies with pagination
    public Page<Technology> findAllTechnologies(Pageable pageable) {
        return technologyRepo.findAll(pageable);
    }

    // Search technologies by name, slug or category
    public Page<Technology> searchTechnologies(String keyword, Pageable pageable) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return technologyRepo.findAll(pageable);
        }

        String searchTerm = keyword.trim();

        return technologyRepo
                .findByTechnologyNameContainingIgnoreCaseOrTechnologySlugContainingIgnoreCaseOrTechnologyCategoryContainingIgnoreCase(
                        searchTerm,
                        searchTerm,
                        searchTerm,
                        pageable
                );
    }

    // Retrieve technology by ID
    public Optional<Technology> findTechnologyById(Integer technologyId) {
        return technologyRepo.findById(technologyId);
    }

    // Add or update technology
    public Technology saveTechnology(Technology technology) {
        return technologyRepo.save(technology);
    }

    public List<Technology> findAllTechnologies() {
        return technologyRepo.findAll(
                Sort.by(Sort.Direction.ASC, "technologyName")
        );
    }
}