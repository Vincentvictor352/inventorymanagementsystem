package com.vincent.InventoryManagementSystem.service;

import com.vincent.InventoryManagementSystem.dto.LoginRequest;
import com.vincent.InventoryManagementSystem.dto.RegisterRequest;
import com.vincent.InventoryManagementSystem.dto.Response;
import com.vincent.InventoryManagementSystem.dto.UserDTO;
import com.vincent.InventoryManagementSystem.entity.User;

public interface UserService {
    Response registerUser(RegisterRequest registerRequest);

    Response loginUser(LoginRequest loginRequest);

    Response getAllUsers();

    User getCurrentLoggedInUser();

    Response getUserById(Long id);

    Response updateUser(Long id, UserDTO userDTO);

    Response deleteUser(Long id);

    Response getUserTransactions(Long id);
}

