package com.slema.controller;

import com.slema.dto.ApiResponse;
import com.slema.dto.LoginRequest;
import com.slema.dto.RegisterRequest;
import com.slema.entity.User;
import com.slema.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            String token = authService.register(request.getUsername(), request.getPhone(), request.getPassword());
            User user = authService.getUserByPhone(request.getPhone());

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", user);

            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getPhone(), request.getPassword());
            User user = authService.getUserByPhone(request.getPhone());

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", user);

            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
