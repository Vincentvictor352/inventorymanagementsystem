package com.vincent.InventoryManagementSystem.service;

import com.vincent.InventoryManagementSystem.dto.ProductDTO;
import com.vincent.InventoryManagementSystem.dto.Response;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    Response saveProduct(ProductDTO productDTO, MultipartFile imageFile);

    Response updateProduct(ProductDTO productDTO, MultipartFile imageFile);

    Response getAllProducts();

    Response getProductById(Long id);

    Response deleteProduct(Long id);

    Response searchProduct(String input);
}
