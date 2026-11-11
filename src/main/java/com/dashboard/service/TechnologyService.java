package com.dashboard.service;


import com.dashboard.Technology;
import com.dashboard.repository.TechnologyRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
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

    // Retrieve all technologies alphabetically for DataTables
    public List<Technology> findAllTechnologies() {
        return technologyRepo.findAll(
                Sort.by(Sort.Direction.ASC, "technologyName")
        );
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


    public Technology saveTechnology(Technology technology) {

        // Validate technology name
        if (technology.getTechnologyName() == null ||
                technology.getTechnologyName().isBlank()) {
            throw new IllegalArgumentException("Technology name is required.");
        }

        String technologyName = technology.getTechnologyName().trim();
        technology.setTechnologyName(technologyName);

        // Generate slug if empty
        if (technology.getTechnologySlug() == null ||
                technology.getTechnologySlug().isBlank()) {
            technology.setTechnologySlug(generateSlug(technologyName));
        } else {
            technology.setTechnologySlug(
                    generateSlug(technology.getTechnologySlug())
            );
        }

        // Check duplicate name and slug
        Integer technologyId = technology.getTechnologyId();

        if (technologyId == null) {

            // New technology
            if (technologyRepo.existsByTechnologyNameIgnoreCase(technologyName)) {
                throw new IllegalArgumentException(
                        "A technology with this name already exists."
                );
            }

            if (technologyRepo.existsByTechnologySlugIgnoreCase(
                    technology.getTechnologySlug())) {
                throw new IllegalArgumentException(
                        "A technology with this slug already exists."
                );
            }

        } else {

            // Existing technology: exclude its own ID
            if (technologyRepo.existsByTechnologyNameIgnoreCaseAndTechnologyIdNot(
                    technologyName, technologyId)) {
                throw new IllegalArgumentException(
                        "A technology with this name already exists."
                );
            }

            if (technologyRepo.existsByTechnologySlugIgnoreCaseAndTechnologyIdNot(
                    technology.getTechnologySlug(), technologyId)) {
                throw new IllegalArgumentException(
                        "A technology with this slug already exists."
                );
            }
        }

        return technologyRepo.save(technology);
    }


    // Generate URL-friendly slug
    private String generateSlug(String value) {

        return value.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}