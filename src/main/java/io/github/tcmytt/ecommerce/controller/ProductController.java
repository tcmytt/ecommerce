package io.github.tcmytt.ecommerce.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.tcmytt.ecommerce.domain.Product;
import io.github.tcmytt.ecommerce.domain.ProductImage;
import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.domain.request.ReqCreateProductDTO;
import io.github.tcmytt.ecommerce.service.FileStorageService;
import io.github.tcmytt.ecommerce.service.ProductImageService;
import io.github.tcmytt.ecommerce.service.ProductService;
import io.github.tcmytt.ecommerce.service.UserService;
import io.github.tcmytt.ecommerce.util.SecurityUtil;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final ProductImageService productImageService;

    public ProductController(
            ProductService productService,
            UserService userService,
            FileStorageService fileStorageService,
            ProductImageService productImageService) {
        this.productImageService = productImageService;
        this.productService = productService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<Page<Product>> fetchAllWithPaginationAndSorting(
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction) {

        int page = 0;
        int size = 10;

        // Xác định tiêu chí sắp xếp
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Tạo đối tượng Pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Truy vấn dữ liệu
        return ResponseEntity.ok().body(this.productService.fetchAllWithPaginationAndSorting(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> fetchById(@PathVariable("id") long id) throws Exception {
        Product p = this.productService.getProductById(id);
        if (p == null) {
            throw new Exception("Product với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(p);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Product> createProduct(
            @RequestPart("product") ReqCreateProductDTO productDTO,
            @RequestPart("files") MultipartFile[] files) {

        // Lấy user từ SecurityContext
        var userOptional = SecurityContextHolder.getContext().getAuthentication();
        if (userOptional == null || userOptional.getName() == null) {
            throw new RuntimeException("User is not authenticated");
        }

        User user = userService.handleGetUserByUsername(userOptional.getName());

        // Lưu ảnh vào thư mục và lấy đường dẫn
        List<String> imagePaths = fileStorageService.storeFiles(files);

        Product savedProduct = productService.createProduct(productDTO, imagePaths, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }
}
