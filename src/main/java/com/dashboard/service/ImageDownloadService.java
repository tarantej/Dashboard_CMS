package com.dashboard.service;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service

public class ImageDownloadService
{
    private static final String UPLOAD_DIR =
            "src/main/resources/static/uploads/profile";

    public String downloadProfilePicture(String imageUrl,
                                         String username)
    {
        try
        {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath))
            {
                Files.createDirectories(uploadPath);
            }

            // Determine file extension
            String extension = ".jpg";

            try
            {
                URL url = new URL(imageUrl);

                URLConnection connection = url.openConnection();

                String contentType = connection.getContentType();

                if (contentType != null)
                {
                    switch (contentType.toLowerCase())
                    {
                        case "image/png":
                            extension = ".png";
                            break;

                        case "image/jpeg":
                            extension = ".jpg";
                            break;

                        case "image/jpg":
                            extension = ".jpg";
                            break;

                        case "image/gif":
                            extension = ".gif";
                            break;

                        case "image/webp":
                            extension = ".webp";
                            break;
                    }
                }

                // Fallback for providers that don't return a Content-Type
                else
                {
                    String lowerUrl = imageUrl.toLowerCase();

                    // Google avatars
                    if (lowerUrl.contains("googleusercontent.com"))
                    {
                        extension = ".jpg";
                    }

                    // GitHub avatars
                    else if (lowerUrl.contains("githubusercontent.com")
                            || lowerUrl.contains("avatars.githubusercontent.com"))
                    {
                        extension = ".png";
                    }
                }

                if(imageUrl.contains("googleusercontent.com"))
                {
                    imageUrl = imageUrl.replaceAll("=s\\d+.*$", "=s1024");
                }
            }
            catch (Exception ignored)
            {
                extension = ".jpg";
            }

            // Create filename
            String timestamp =
                    LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            String filename =
                    username.replaceAll("[^a-zA-Z0-9]", "")
                            + "_"
                            + timestamp
                            + extension;

            // Download image
            try (InputStream inputStream = new URL(imageUrl).openStream())
            {
                Files.copy(
                        inputStream,
                        uploadPath.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            return filename;
        }
        catch (Exception e)
        {
            System.out.println("Failed to download OAuth profile picture.");
            return null;
        }
    }
}