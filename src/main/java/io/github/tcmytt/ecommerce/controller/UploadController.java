package io.github.tcmytt.ecommerce.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

// hiện tại chưa dùng thằng này

@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

    @Value("${file.product-image-dir}")
    private String productImageDir;

    @PostMapping("/product-images")
    public ResponseEntity<String> uploadProductImage(@RequestParam("file") MultipartFile file) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File(productImageDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Tạo tên file duy nhất
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(productImageDir, fileName);

            // Lưu file vào thư mục
            Files.copy(file.getInputStream(), filePath);

            // Trả về đường dẫn tương đối
            return ResponseEntity.ok("/upload/products/" + fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }
}