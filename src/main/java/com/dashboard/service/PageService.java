
package com.dashboard.service;

import com.dashboard.Page;
import com.dashboard.repository.PageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class PageService {

    private final PageRepository pageRepository;

    public PageService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    // Create a new page
    public Page createPage(Page page) {

        validatePage(page);

        String template = normalizeTemplate(page.getPageTemplate());
        String status = normalizeStatus(page.getPageStatus());

        String slug = generateUniqueSlug(
                page.getPageSlug(),
                page.getPageTitle(),
                template,
                null
        );

        String url = page.getPageUrl();

        if (url == null || url.isBlank()) {
            url = generatePageUrl(slug, template);
        } else {
            url = normalizePageUrl(url);
        }

        validateUniqueUrl(url, null);

        page.setPageTitle(page.getPageTitle().trim());
        page.setPageTemplate(template);
        page.setPageStatus(status);
        page.setPageSlug(slug);
        page.setPageUrl(url);

        return pageRepository.save(page);
    }

    // Update an existing page
    public Page updatePage(Integer pageId, Page updatedPage) {

        Page existingPage = getPageById(pageId);

        validatePage(updatedPage);

        String template = normalizeTemplate(
                updatedPage.getPageTemplate()
        );

        String status = normalizeStatus(
                updatedPage.getPageStatus()
        );

        String slug = generateUniqueSlug(
                updatedPage.getPageSlug(),
                updatedPage.getPageTitle(),
                template,
                pageId
        );

        String url = updatedPage.getPageUrl();

        if (url == null || url.isBlank()) {
            url = generatePageUrl(slug, template);
        } else {
            url = normalizePageUrl(url);
        }

        validateUniqueUrl(url, pageId);

        existingPage.setPageTitle(updatedPage.getPageTitle().trim());
        existingPage.setPageSlug(slug);
        existingPage.setPageUrl(url);
        existingPage.setPageTemplate(template);
        existingPage.setPageContent(updatedPage.getPageContent());
        existingPage.setPageStatus(status);
        existingPage.setMetaTitle(updatedPage.getMetaTitle());
        existingPage.setMetaDescription(
                updatedPage.getMetaDescription()
        );
        existingPage.setDisplayOrder(updatedPage.getDisplayOrder());

        return pageRepository.save(existingPage);
    }

    // Retrieve a page by ID
    public Page getPageById(Integer pageId) {

        return pageRepository.findById(pageId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Page not found with ID: " + pageId
                        )
                );
    }

    // Retrieve all pages
    public List<Page> getAllPages() {
        return pageRepository.findAll();
    }

    // Retrieve pages by template
    public List<Page> getPagesByTemplate(String template) {
        return pageRepository.findByPageTemplate(
                normalizeTemplate(template)
        );
    }

    // Retrieve pages by status
    public List<Page> getPagesByStatus(String status) {
        return pageRepository.findByPageStatus(
                normalizeStatus(status)
        );
    }

    // Move a page to Trash
    public Page moveToTrash(Integer pageId) {

        Page page = getPageById(pageId);
        page.setPageStatus("TRASH");

        return pageRepository.save(page);
    }

    // Restore a page from Trash
    public Page restorePage(Integer pageId) {

        Page page = getPageById(pageId);

        if (!"TRASH".equals(page.getPageStatus())) {
            throw new IllegalArgumentException(
                    "Only trashed pages can be restored."
            );
        }

        page.setPageStatus("DRAFT");

        return pageRepository.save(page);
    }

    // Permanently delete a page
    public void deletePage(Integer pageId) {

        Page page = getPageById(pageId);
        pageRepository.delete(page);
    }

    // Validate page information
    private void validatePage(Page page) {

        if (page.getPageTitle() == null ||
                page.getPageTitle().isBlank()) {
            throw new IllegalArgumentException(
                    "Page title cannot be empty."
            );
        }

        normalizeTemplate(page.getPageTemplate());
        normalizeStatus(page.getPageStatus());
    }

    // Validate and normalise the page template
    private String normalizeTemplate(String template) {

        if (template == null || template.isBlank()) {
            return "DEFAULT";
        }

        String value = template.trim().toUpperCase(Locale.ROOT);

        if (!List.of(
                "LANDING",
                "DEFAULT",
                "DASHBOARD",
                "LOGIN"
        ).contains(value)) {
            throw new IllegalArgumentException(
                    "Invalid page template: " + template
            );
        }

        return value;
    }

    // Validate and normalise page status
    private String normalizeStatus(String status) {

        if (status == null || status.isBlank()) {
            return "DRAFT";
        }

        String value = status.trim().toUpperCase(Locale.ROOT);

        if (!List.of("DRAFT", "PUBLISHED", "TRASH").contains(value)) {
            throw new IllegalArgumentException(
                    "Invalid page status: " + status
            );
        }

        return value;
    }

    // Generate a URL based on the page template
    private String generatePageUrl(String slug, String template) {

        return switch (template) {
            case "LANDING" -> "/";
            case "DASHBOARD" -> "/dashboard/" + slug;
            case "DEFAULT", "LOGIN" -> "/" + slug;
            default -> throw new IllegalArgumentException(
                    "Unsupported page template: " + template
            );
        };
    }

    // Normalise a manually supplied URL
    private String normalizePageUrl(String url) {

        String value = url.trim();

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    "Page URL cannot be empty."
            );
        }

        if (value.contains(" ") ||
                value.contains("?") ||
                value.contains("#") ||
                value.contains("\\") ||
                value.contains("://") ||
                value.startsWith("//")) {
            throw new IllegalArgumentException(
                    "Enter a valid internal page URL."
            );
        }

        if (!value.startsWith("/")) {
            value = "/" + value;
        }

        if (value.length() > 1 && value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }

        return value;
    }

    // Check whether a URL is already assigned to another page
    private void validateUniqueUrl(String url, Integer currentPageId) {

        Page existing = pageRepository.findByPageUrl(url)
                .orElse(null);

        if (existing != null &&
                !existing.getPageId().equals(currentPageId)) {
            throw new IllegalArgumentException(
                    "This page URL is already in use: " + url
            );
        }
    }

    // Generate a unique slug within the selected template
    private String generateUniqueSlug(
            String suppliedSlug,
            String title,
            String template,
            Integer currentPageId) {

        String source = (suppliedSlug == null || suppliedSlug.isBlank())
                ? title
                : suppliedSlug;

        String baseSlug = source.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        if (baseSlug.isBlank()) {
            throw new IllegalArgumentException(
                    "Unable to generate a valid page slug."
            );
        }

        String candidate = baseSlug;
        int counter = 2;

        while (true) {

            Page existing = pageRepository
                    .findByPageSlugAndPageTemplate(candidate, template)
                    .orElse(null);

            if (existing == null ||
                    existing.getPageId().equals(currentPageId)) {
                return candidate;
            }

            candidate = baseSlug + "-" + counter;
            counter++;
        }
    }
}