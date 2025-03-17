package io.github.tcmytt.ecommerce.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.tcmytt.ecommerce.domain.Product;
import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.domain.request.ReqCreateProductDTO;
import io.github.tcmytt.ecommerce.domain.request.ReqUpdateProductDTO;
import io.github.tcmytt.ecommerce.service.FileStorageService;
import io.github.tcmytt.ecommerce.service.ProductImageService;
import io.github.tcmytt.ecommerce.service.ProductService;
import io.github.tcmytt.ecommerce.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public ProductController(
            ProductService productService,
            UserService userService,
            FileStorageService fileStorageService,
            ProductImageService productImageService) {
        this.productService = productService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @Operation(summary = "Get all products", description = "Returns a list of all products with pagination and sorting")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<Product>> fetchAllWithPaginationAndSorting(
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "categoryId", required = false) Long categoryId) {

        int page = 0;
        int size = 10;

        // Xác định tiêu chí sắp xếp
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Tạo đối tượng Pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Truy vấn dữ liệu
        if (categoryId != null) {
            // Lọc sản phẩm theo categoryId
            return ResponseEntity.ok()
                    .body(this.productService.fetchByCategoryWithPaginationAndSorting(categoryId, pageable));
        } else {
            // Lấy tất cả sản phẩm nếu không có categoryId
            return ResponseEntity.ok().body(this.productService.fetchAllWithPaginationAndSorting(pageable));
        }
    }

    @Operation(summary = "Get product by id", description = "Returns a product by id")
    @ApiResponse(responseCode = "200", description = "Product retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<Product> fetchById(@PathVariable("id") long id) throws Exception {
        Product p = this.productService.getProductById(id);
        if (p == null) {
            throw new Exception("Product với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(p);
    }

    @Operation(summary = "Create a new product", description = "Create a new product with images")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Product> createProduct(
            @RequestPart("product") ReqCreateProductDTO productDTO,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {

        // Lấy user từ SecurityContext
        var userOptional = SecurityContextHolder.getContext().getAuthentication();
        if (userOptional == null || userOptional.getName() == null) {
            throw new RuntimeException("User is not authenticated");
        }

        User user = userService.handleGetUserByUsername(userOptional.getName());

        // Lưu ảnh vào thư mục và lấy đường dẫn
        List<String> imagePaths = new ArrayList<>();
        if (files != null && files.length > 0) {
            imagePaths = fileStorageService.storeProductImages(files);
        }

        // Tạo sản phẩm mới
        Product savedProduct = productService.createProduct(productDTO, imagePaths, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @Operation(summary = "Update a product", description = "Update a product without updating images")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @PutMapping
    public ResponseEntity<Product> updateProduct(@RequestBody ReqUpdateProductDTO productDTO) {

        long id = productDTO.getId();
        if (productService.isIdExist(id) == false) {
            throw new RuntimeException("Product not found");
        }

        // Cập nhật sản phẩm không cập nhập ảnh, logic xử lý cập nhật ảnh ở phần khác
        Product updatedProduct = productService.updateProduct(productDTO);

        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Delete a product", description = "Delete a product by id")
    @ApiResponse(responseCode = "200", description = "Product deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") long id) {
        if (productService.isIdExist(id) == false) {
            throw new RuntimeException("Product not found");
        }
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}
