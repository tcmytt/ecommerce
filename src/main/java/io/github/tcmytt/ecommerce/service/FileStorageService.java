package io.github.tcmytt.ecommerce.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileStorageService {

    @Value("${file.product-image-dir}")
    private String productImageDir;

    public List<String> storeFiles(MultipartFile[] files) {
        List<String> imagePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                // Tạo tên file mới bằng mã hash MD5
                String fileName = generateHashedFileName(file.getOriginalFilename());
                Path filePath = Paths.get(productImageDir, fileName);

                // Tạo thư mục nếu chưa tồn tại
                File directory = new File(productImageDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Lưu file
                Files.copy(file.getInputStream(), filePath);
                imagePaths.add("/upload/products/" + fileName);
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        return imagePaths;
    }

    private String generateHashedFileName(String originalFilename) throws NoSuchAlgorithmException {
        // Lấy phần mở rộng của file
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        // Tạo mã hash MD5 từ tên file gốc
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(originalFilename.getBytes());
        byte[] digest = md.digest();

        // Chuyển đổi mã hash thành chuỗi hex
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }

        // Trả về tên file mới với mã hash và phần mở rộng
        return hexString.toString() + extension;
    }
}