package com.autolot.autolotbackend.service.FileUpload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class HandleFileUpload {

    @Value("${app.upload-dir}")
    private String uploadDirectory;

    public void storeFiles(MultipartFile[] files, String dealerShipSlug, String vehicleId) throws IOException {

        // 1. Define the specific target directory for this vehicle
        // This creates: ./uploads/toyota-city/v123/
        Path targetFolder = Paths.get(uploadDirectory, dealerShipSlug, vehicleId);

        // 2. Create the directory and any missing parent directories
        if (!Files.exists(targetFolder)) {
            Files.createDirectories(targetFolder);
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // 3. Generate UUID and extract extension
                String originalFilename = file.getOriginalFilename();
                String extension = StringUtils.getFilenameExtension(originalFilename);

                String newFileName = UUID.randomUUID().toString() + (extension != null ? "." + extension : "");

                // 4. Resolve the final file path
                Path destinationFile = targetFolder.resolve(newFileName);

                // 5. Save the file
                file.transferTo(destinationFile);
            }
        }
    }

}
