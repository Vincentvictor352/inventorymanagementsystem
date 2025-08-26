package com.vincent.InventoryManagementSystem.service;

import com.vincent.InventoryManagementSystem.dto.CategoryDTO;
import com.vincent.InventoryManagementSystem.dto.Response;

public interface CategoryService {

    Response createCategory(CategoryDTO categoryDTO);

    Response getAllCategory();

    Response getCategoryById(Long id);

    Response updateCategory(Long id, CategoryDTO categoryDTO);

    Response delectCategory(Long id);
}
