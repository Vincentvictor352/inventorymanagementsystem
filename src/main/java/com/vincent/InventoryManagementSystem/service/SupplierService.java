package com.vincent.InventoryManagementSystem.service;

import com.vincent.InventoryManagementSystem.dto.Response;
import com.vincent.InventoryManagementSystem.dto.SupplierDTO;

public interface SupplierService {
    Response addSupplier(SupplierDTO supplierDTO);

    Response updateSupplier(Long id, SupplierDTO supplierDTO);

    Response getAllSupplier();

    Response getSupplierById(Long id);

    Response deleteSupplier(Long id);

}

