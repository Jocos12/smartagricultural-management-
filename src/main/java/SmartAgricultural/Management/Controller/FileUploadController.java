package SmartAgricultural.Management.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upload")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileUploadController {

    private static final String UPLOAD_DIR_CROPS = "uploads/crops";
    private static final String UPLOAD_DIR_PROFILES = "uploads/profiles";

    @PostMapping("/crop-image")
    public ResponseEntity<Map<String, String>> uploadCropImage(
            @RequestParam("file") MultipartFile file) {
        return uploadImage(file, UPLOAD_DIR_CROPS, "/uploads/crops/");
    }

    @PostMapping("/profile-image")
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @RequestParam("file") MultipartFile file) {
        return uploadImage(file, UPLOAD_DIR_PROFILES, "/uploads/profiles/");
    }

    private ResponseEntity<Map<String, String>> uploadImage(
            MultipartFile file, String uploadDir, String urlPrefix) {

        Map<String, String> response = new HashMap<>();

        try {
            // Validation du fichier
            if (file.isEmpty()) {
                response.put("success", "false");
                response.put("message", "Please select a file to upload");
                return ResponseEntity.badRequest().body(response);
            }

            // Validation du type de fichier
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", "false");
                response.put("message", "Only image files are allowed");
                return ResponseEntity.badRequest().body(response);
            }

            // Validation de la taille (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("success", "false");
                response.put("message", "File size must not exceed 5MB");
                return ResponseEntity.badRequest().body(response);
            }

            // Créer le répertoire s'il n'existe pas
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
                System.out.println("✅ Created directory: " + uploadDirFile.getAbsolutePath());
            }

            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase() : ".jpg";
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Sauvegarder le fichier
            Path filePath = Paths.get(uploadDir, uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Log de confirmation
            System.out.println("✅ File saved: " + filePath.toAbsolutePath());

            // Construire l'URL accessible
            String fileUrl = urlPrefix + uniqueFilename;

            response.put("success", "true");
            response.put("message", "File uploaded successfully");
            response.put("imageUrl", fileUrl);
            response.put("filename", uniqueFilename);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", "false");
            response.put("message", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/crop-image")
    public ResponseEntity<Map<String, String>> deleteCropImage(
            @RequestParam("filename") String filename) {
        return deleteImage(filename, UPLOAD_DIR_CROPS);
    }

    @DeleteMapping("/profile-image")
    public ResponseEntity<Map<String, String>> deleteProfileImage(
            @RequestParam("filename") String filename) {
        return deleteImage(filename, UPLOAD_DIR_PROFILES);
    }

    private ResponseEntity<Map<String, String>> deleteImage(
            String filename, String uploadDir) {

        Map<String, String> response = new HashMap<>();

        try {
            Path filePath = Paths.get(uploadDir, filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                response.put("success", "true");
                response.put("message", "File deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", "false");
                response.put("message", "File not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (IOException e) {
            response.put("success", "false");
            response.put("message", "Failed to delete file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}