package com.codewithmosh.store.controllers;

import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;

    public User getcurrentUser(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId= (Long)authentication.getPrincipal();
        return userRepository.findById(userId).orElse(null);
    }
}
