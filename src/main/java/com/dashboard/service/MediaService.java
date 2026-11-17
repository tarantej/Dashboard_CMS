package com.dashboard.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class MediaService {
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "webp", "gif");

    private final Path projectLogoDir;
    private final Path projectFeatureDir;

    public MediaService(
            @Value("${app.upload.project-logo-dir}") String projectLogoDir,
            @Value("${app.upload.project-feature-dir}") String projectFeatureDir) {

        this.projectLogoDir = Paths.get(projectLogoDir).toAbsolutePath().normalize();
        this.projectFeatureDir = Paths.get(projectFeatureDir).toAbsolutePath().normalize();
    }

    public String storeProjectLogo(MultipartFile file) throws IOException {
        return storeFile(file, projectLogoDir, "/img/project-logos/");
    }

    public String storeFeaturedImage(MultipartFile file) throws IOException {
        return storeFile(file, projectFeatureDir, "/img/portfolio/");
    }

    private String storeFile(
            MultipartFile file,
            Path destination,
            String publicPath) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select an image to upload.");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("Invalid image filename.");
        }

        String extension = originalFilename
                .substring(originalFilename.lastIndexOf('.') + 1)
                .toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Unsupported image format.");
        }

        Files.createDirectories(destination);

        String filename = UUID.randomUUID() + "." + extension;

        Path target = destination.resolve(filename).normalize();

        if (!target.startsWith(destination)) {
            throw new IOException("Invalid upload destination.");
        }

        Files.copy(file.getInputStream(), target);

        return publicPath + filename;
    }

    public void deleteMedia(String mediaPath) throws IOException {

        if (mediaPath == null || mediaPath.isBlank()) {
            return;
        }

        Path target;

        if (mediaPath.startsWith("/img/project-logos/")) {
            target = projectLogoDir.resolve(
                    mediaPath.substring("/img/project-logos/".length()));
        } else if (mediaPath.startsWith("/img/portfolio/")) {
            target = projectFeatureDir.resolve(
                    mediaPath.substring("/img/portfolio/".length()));
        } else {
            throw new IllegalArgumentException("Unsupported media path.");
        }

        target = target.normalize();

        Path allowedDirectory = mediaPath.startsWith("/img/project-logos/")
                ? projectLogoDir : projectFeatureDir;

        if (!target.startsWith(allowedDirectory)) {
            throw new IOException("Invalid media path.");
        }

        Files.deleteIfExists(target);
    }
}