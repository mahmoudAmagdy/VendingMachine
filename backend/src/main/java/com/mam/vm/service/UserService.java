package com.mam.vm.service;


import com.mam.vm.entities.User;
import com.mam.vm.repositories.rest.DTOs.request.UserCreateRequest;
import com.mam.vm.repositories.rest.DTOs.request.UserUpdateRequest;
import com.mam.vm.repositories.rest.DTOs.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    
    UserResponse createUser(UserCreateRequest request);
    
    UserResponse getUserById(Long id);
    
    UserResponse getUserByUsername(String username);
    
    Page<UserResponse> getAllUsers(Pageable pageable);
    
    UserResponse updateUser(Long id, UserUpdateRequest request);
    
    void deleteUser(Long id);
    
    User findUserEntityById(Long id);
    
    User findUserEntityByUsername(String username);
    
    void updateUserDeposit(Long userId, Integer newDeposit);
}