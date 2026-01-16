package SmartAgricultural.Management.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};

    @Value("${file.upload-dir:uploads/profiles}")
    private String uploadDirPath;

    private Path uploadDir;

    public FileStorageService(@Value("${file.upload-dir:uploads/profiles}") String uploadDirPath) {
        this.uploadDirPath = uploadDirPath;
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();

        try {
            Files.createDirectories(uploadDir);
            logger.info("Upload directory created/verified at: {}", uploadDir);
        } catch (IOException e) {
            logger.error("Could not create upload directory", e);
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String storeFile(MultipartFile file, String userId) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds maximum limit of 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidExtension(originalFilename)) {
            throw new IOException("Invalid file type. Only JPG, JPEG, PNG, and GIF are allowed");
        }

        String fileExtension = getFileExtension(originalFilename);
        String newFilename = userId + "_" + UUID.randomUUID().toString() + fileExtension;

        Path filePath = uploadDir.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        logger.info("File stored successfully at: {}", filePath);

        // Return relative URL path for web access
        return "uploads/profiles/" + newFilename;
    }

    public void deleteFile(String filePath) {
        try {
            if (filePath != null && !filePath.isEmpty()) {
                // Extract just the filename from the path
                String filename = filePath.substring(filePath.lastIndexOf('/') + 1);
                Path path = uploadDir.resolve(filename);

                if (Files.deleteIfExists(path)) {
                    logger.info("File deleted successfully: {}", path);
                } else {
                    logger.warn("File not found for deletion: {}", path);
                }
            }
        } catch (IOException e) {
            logger.error("Error deleting file: {}", filePath, e);
        }
    }

    private boolean isValidExtension(String filename) {
        String lowerFilename = filename.toLowerCase();
        for (String ext : ALLOWED_EXTENSIONS) {
            if (lowerFilename.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : filename.substring(lastDotIndex);
    }
}