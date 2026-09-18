package com.example.scholarmatch.filestorage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload.base-dir:uploads}")
    private String baseDir;

    @Value("${file.upload.allowed-extensions:pdf,jpg,jpeg,png}")
    private String allowedExtensionsCsv;

    @Value("${file.upload.max-size-bytes:5242880}")
    private long maxSizeBytes;

    public StoredFile store(String subFolder, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException("File exceeds maximum allowed size of " + maxSizeBytes + " bytes");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        String extension = getExtension(originalFilename).toLowerCase();

        List<String> allowed = List.of(allowedExtensionsCsv.toLowerCase().split(","));
        if (extension.isBlank() || !allowed.contains(extension)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: " + allowedExtensionsCsv);
        }

        try {
            Path targetDir = Paths.get(baseDir, sanitizeFolder(subFolder)).normalize();
            Files.createDirectories(targetDir);

            String storedFileName = UUID.randomUUID() + "." + extension;
            Path targetPath = targetDir.resolve(storedFileName).normalize();

            if (!targetPath.startsWith(targetDir)) {
                throw new IllegalArgumentException("Invalid file path");
            }

            Files.copy(file.getInputStream(), targetPath);

            String relativePath = Paths.get(sanitizeFolder(subFolder), storedFileName).toString().replace("\\", "/");

            return new StoredFile(relativePath, originalFilename, file.getContentType(), file.getSize());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store file: " + e.getMessage(), e);
        }
    }

    public Resource loadAsResource(String relativePath) {
        try {
            Path base = Paths.get(baseDir).normalize();
            Path filePath = base.resolve(relativePath).normalize();
            if (!filePath.startsWith(base)) {
                throw new IllegalArgumentException("Invalid file path");
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException("File not found: " + relativePath);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new IllegalStateException("File not found: " + relativePath, e);
        }
    }

    private String sanitizeFolder(String subFolder) {
        return subFolder.replaceAll("[^a-zA-Z0-9_\\-/]", "");
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1 || dotIndex == filename.length() - 1) ? "" : filename.substring(dotIndex + 1);
    }

    public static class StoredFile {
        private final String relativePath;
        private final String originalFileName;
        private final String contentType;
        private final long sizeBytes;

        public StoredFile(String relativePath, String originalFileName, String contentType, long sizeBytes) {
            this.relativePath = relativePath;
            this.originalFileName = originalFileName;
            this.contentType = contentType;
            this.sizeBytes = sizeBytes;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public String getContentType() {
            return contentType;
        }

        public long getSizeBytes() {
            return sizeBytes;
        }
    }
}