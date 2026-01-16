package SmartAgricultural.Management.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // Créez ce dossier dans votre projet : src/main/resources/static/uploads/crops/
    @Value("${file.upload-dir:uploads/crops}")
    private String uploadDir;

    @PostMapping("/crop-image")
    public ResponseEntity<Map<String, String>> uploadCropImage(
            @RequestParam("file") MultipartFile file) {

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
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Sauvegarder le fichier
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Construire l'URL accessible
            String fileUrl = "/uploads/crops/" + uniqueFilename;

            response.put("success", "true");
            response.put("message", "File uploaded successfully");
            response.put("imageUrl", fileUrl);
            response.put("filename", uniqueFilename);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", "false");
            response.put("message", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/crop-image")
    public ResponseEntity<Map<String, String>> deleteCropImage(
            @RequestParam("filename") String filename) {

        Map<String, String> response = new HashMap<>();

        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                response.put("success", "true");
                response.put("message", "File deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", "false");
                response.put("message", "File not found");
                return ResponseEntity.notFound().build();
            }

        } catch (IOException e) {
            response.put("success", "false");
            response.put("message", "Failed to delete file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}