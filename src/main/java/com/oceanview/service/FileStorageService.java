package com.oceanview.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.uploadDir = base.resolve("rooms");
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String store(MultipartFile file) throws IOException {
        if (file.isEmpty()) return null;
        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + (ext != null ? "." + ext : "");
        Path target = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), target);
        return "/uploads/rooms/" + filename;
    }

    private String getExtension(String name) {
        if (name == null || !name.contains(".")) return null;
        return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    }
}
