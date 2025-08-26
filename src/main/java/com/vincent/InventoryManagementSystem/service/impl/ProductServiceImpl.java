package com.vincent.InventoryManagementSystem.service.impl;

import com.vincent.InventoryManagementSystem.dto.ProductDTO;
import com.vincent.InventoryManagementSystem.dto.Response;
import com.vincent.InventoryManagementSystem.entity.Category;
import com.vincent.InventoryManagementSystem.entity.Product;
import com.vincent.InventoryManagementSystem.exceptions.NotFoundException;
import com.vincent.InventoryManagementSystem.repository.CategoryRepository;
import com.vincent.InventoryManagementSystem.repository.ProductRepository;
import com.vincent.InventoryManagementSystem.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    private static final String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/product-images/";

    @Override
    public Response saveProduct(ProductDTO productDTO, MultipartFile imageFile) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        // map our dto to product entity
        Product productToSave = new Product();
        productToSave.setName(productDTO.getName());
        productToSave.setSku(productDTO.getSku());
        productToSave.setPrice(productDTO.getPrice());
        productToSave.setStockQuantity(productDTO.getStockQuantity());
        productToSave.setDescription(productDTO.getDescription());
        productToSave.setCategory(category);

        if (imageFile != null && !imageFile.isEmpty()) {
            log.info("Image file exists");
            String imagePath = saveImage(imageFile);
            log.info("IMAGE URL IS: {}", imagePath);
            productToSave.setImageUrl(imagePath);
        }

        // save the product entity
        productRepository.save(productToSave);

        return Response.builder()
                .status(200)
                .message("Product successfully saved")
                .build();
    }

    @Override
    public Response updateProduct(ProductDTO productDTO, MultipartFile imageFile) {
        // check if product exists
        Product existingProduct = productRepository.findById(productDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        // check if image is associated with the product
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            log.info("IMAGE URL IS: {}", imagePath);
            existingProduct.setImageUrl(imagePath);
        }

        // check if category is to be changed
        if (productDTO.getCategoryId() != null && productDTO.getCategoryId() > 0) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category Not Found"));
            existingProduct.setCategory(category);
        }

        // update product fields
        if (productDTO.getName() != null && !productDTO.getName().isBlank()) {
            existingProduct.setName(productDTO.getName());
        }
        if (productDTO.getSku() != null && !productDTO.getSku().isBlank()) {
            existingProduct.setSku(productDTO.getSku());
        }
        if (productDTO.getDescription() != null && !productDTO.getDescription().isBlank()) {
            existingProduct.setDescription(productDTO.getDescription());
        }
        if (productDTO.getPrice() != null && productDTO.getPrice().compareTo(BigDecimal.ZERO) >= 0) {
            existingProduct.setPrice(productDTO.getPrice());
        }
        if (productDTO.getStockQuantity() != null && productDTO.getStockQuantity() >= 0) {
            existingProduct.setStockQuantity(productDTO.getStockQuantity());
        }

        // update the product
        productRepository.save(existingProduct);

        return Response.builder()
                .status(200)
                .message("Product Updated successfully")
                .build();
    }

    @Override
    public Response getAllProducts() {
        List<Product> productList = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<ProductDTO> productDTOList = modelMapper.map(productList, new TypeToken<List<ProductDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .products(productDTOList)
                .build();
    }

    @Override
    public Response getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        return Response.builder()
                .status(200)
                .message("success")
                .product(modelMapper.map(product, ProductDTO.class))
                .build();
    }

    @Override
    public Response deleteProduct(Long id) {
        productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        productRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Product Deleted successfully")
                .build();
    }

    @Override
    public Response searchProduct(String input) {
        List<Product> products = productRepository.findByNameContainingOrDescriptionContaining(input, input);

        if (products.isEmpty()) {
            throw new NotFoundException("Product Not Found");
        }

        List<ProductDTO> productDTOList = modelMapper.map(products, new TypeToken<List<ProductDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .products(productDTOList)
                .build();
    }

    public String saveImage(MultipartFile imageFile) {
        // Validate image
        if (!imageFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Create a directory to store images if it doesn't exist
        File directory = new File(IMAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
            log.info("Directory was created");
        }

        // Generate unique file name
        String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        String imagePath = IMAGE_DIRECTORY + uniqueFileName;

        try {
            File destinationFile = new File(imagePath);
            imageFile.transferTo(destinationFile);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving Image: " + e.getMessage());
        }

        return imagePath;
    }
}
