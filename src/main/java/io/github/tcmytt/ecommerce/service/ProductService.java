package io.github.tcmytt.ecommerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.tcmytt.ecommerce.domain.Category;
import io.github.tcmytt.ecommerce.domain.Product;
import io.github.tcmytt.ecommerce.domain.ProductImage;
import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.domain.request.ReqCreateProductDTO;
import io.github.tcmytt.ecommerce.domain.request.ReqUpdateProductDTO;
import io.github.tcmytt.ecommerce.repository.CategoryRepository;
import io.github.tcmytt.ecommerce.repository.ProductImageRepository;
import io.github.tcmytt.ecommerce.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageService productImageService;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final FileStorageService fileStorageService;

    @Value("${file.product-image-dir}")
    private String productImageDir;

    public ProductService(ProductRepository productRepository,
            ProductImageService productImageService,
            CategoryService categoryService,
            CategoryRepository categoryRepository,
            ProductImageRepository productImageRepository,
            FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
        this.productImageService = productImageService;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    public boolean isIdExist(long id) {
        return this.productRepository.existsById(id);
    }

    public Page<Product> fetchAllWithPaginationAndSorting(Pageable pageable) {
        return this.productRepository.findAll(pageable);
    }

    public Page<Product> fetchByCategoryWithPaginationAndSorting(long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    public Product getProductById(long id) {
        return this.productRepository.findById(id).orElse(null);
    }

    public Product createProduct(ReqCreateProductDTO productDTO, List<String> imagePaths, User user) {

        // Tạo sản phẩm
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setCategory(categoryService.findById(productDTO.getCategoryId()));
        product.setUser(user);
        product.setActive(true);

        Product savedProduct = this.productRepository.save(product);

        // Lưu ảnh và đánh dấu ảnh chính
        for (int i = 0; i < imagePaths.size(); i++) {
            ProductImage productImage = new ProductImage();
            productImage.setProduct(savedProduct);
            productImage.setImage(imagePaths.get(i));
            productImage.setIsMain(i == productDTO.getMainImageIndex());
            productImageService.save(productImage);
        }
        product.setMainImage(imagePaths.get(productDTO.getMainImageIndex()));
        this.productRepository.save(product);

        return savedProduct;
    }

    // Tìm sản phẩm theo ID
    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product updateProduct(ReqUpdateProductDTO productDTO) {
        // Lấy sản phẩm hiện tại
        Product product = findProductById(productDTO.getId());

        // Cập nhật thông tin sản phẩm
        if (productDTO.getName() != null)
            product.setName(productDTO.getName());
        if (productDTO.getDescription() != null)
            product.setDescription(productDTO.getDescription());
        if (productDTO.getPrice() != null)
            product.setPrice(productDTO.getPrice());
        if (productDTO.getQuantity() != null)
            product.setQuantity(productDTO.getQuantity());
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        // Lưu sản phẩm đã cập nhật
        return productRepository.save(product);
    }

    // Xóa sản phẩm
    public void deleteProduct(Long productId) {
        // Lấy sản phẩm hiện tại
        Product product = findProductById(productId);

        // Có thể có logic check quyền của user

        // Lấy danh sách ảnh của sản phẩm
        List<ProductImage> images = productImageRepository.findByProductId(productId);

        // Xóa file ảnh từ thư mục upload
        for (ProductImage image : images) {
            fileStorageService.deleteFile(image.getImage(), productImageDir);
        }

        // Xóa ảnh khỏi database
        productImageRepository.deleteAll(images);

        // Xóa sản phẩm khỏi database
        productRepository.delete(product);
    }

}
