package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.model.User;

import java.util.List;

/**
 * Interfaz para el servicio de la entidad User.
 */
public interface UserService {
    User createUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}