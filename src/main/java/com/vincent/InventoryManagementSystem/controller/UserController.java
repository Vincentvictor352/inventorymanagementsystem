package com.vincent.InventoryManagementSystem.controller;

import com.vincent.InventoryManagementSystem.dto.Response;
import com.vincent.InventoryManagementSystem.dto.UserDTO;
import com.vincent.InventoryManagementSystem.entity.User;
import com.vincent.InventoryManagementSystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class


UserController {


    private final UserService userService;


    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/Update/{id}" )
    public ResponseEntity<Response> updateUser(@PathVariable long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }



    @DeleteMapping ("/delete/{id}" )
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteUser(@PathVariable long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }


    @GetMapping("/transaction/{userId}")
    public ResponseEntity<Response> getUsersAndTransactions(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUserTransactions(userId));
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(){
        return ResponseEntity.ok(userService.getCurrentLoggedInUser());
    }

}
