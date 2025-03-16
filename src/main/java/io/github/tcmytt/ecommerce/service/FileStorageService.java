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

    @Value("${file.user-avatar-dir}")
    private String userAvatarDir;

    @Value("${file.category-image-dir}")
    private String categoryImageDir;

    /**
     * Lưu avatar (chỉ 1 ảnh). Nếu đã có avatar cũ, xóa ảnh cũ.
     */
    public String storeAvatar(MultipartFile file) {
        try {
            // Tạo tên file mới
            String fileName = generateHashedFileName(file.getOriginalFilename());
            Path filePath = Paths.get(userAvatarDir, fileName);

            // Tạo thư mục nếu chưa tồn tại
            File directory = new File(userAvatarDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Xóa avatar cũ (nếu có)
            deleteFileIfExists(userAvatarDir, "/upload/avatars/");

            // Lưu file
            Files.copy(file.getInputStream(), filePath);

            return "/upload/avatars/" + fileName;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to upload avatar", e);
        }
    }

    /**
     * Lưu ảnh cho danh mục (chỉ 1 ảnh). Nếu đã có ảnh cũ, xóa ảnh cũ.
     */
    public String storeCategoryImage(MultipartFile file) {
        try {
            // Tạo tên file mới
            String fileName = generateHashedFileName(file.getOriginalFilename());
            Path filePath = Paths.get(categoryImageDir, fileName);

            // Tạo thư mục nếu chưa tồn tại
            File directory = new File(categoryImageDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Xóa ảnh cũ (nếu có)
            deleteFileIfExists(categoryImageDir, "/upload/categories/");

            // Lưu file
            Files.copy(file.getInputStream(), filePath);

            return "/upload/categories/" + fileName;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to upload category image", e);
        }
    }

    /**
     * Lưu nhiều ảnh cho sản phẩm.
     */
    public List<String> storeProductImages(MultipartFile[] files) {
        List<String> imagePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                // Tạo tên file mới
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
                throw new RuntimeException("Failed to upload product image", e);
            }
        }
        return imagePaths;
    }

    /**
     * Xóa file nếu tồn tại
     */
    private void deleteFileIfExists(String directoryPath, String relativePathPrefix) {
        File directory = new File(directoryPath);
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                String relativePath = relativePathPrefix + file.getName();
                System.out.println("Deleting file: " + relativePath);
                // Ở đây, relativePath được sử dụng để in ra đường dẫn tương đối của file trước
                // khi xóa.
                file.delete();
            }
        }
    }

    /**
     * Xóa một file cụ thể
     */
    public void deleteFile(String imagePath, String directoryPath) {
        try {
            Path filePath = Paths.get(directoryPath, imagePath.replace("/upload/", ""));
            File file = filePath.toFile();

            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    /**
     * Tạo tên file duy nhất bằng mã hash MD5
     */
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