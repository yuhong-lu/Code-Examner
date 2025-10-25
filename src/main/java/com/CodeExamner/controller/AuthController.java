// controller/AuthController.java
package com.CodeExamner.controller;

import com.CodeExamner.dto.request.LoginRequest;
import com.CodeExamner.dto.request.RegisterRequest;
import com.CodeExamner.dto.response.AuthResponse;
import com.CodeExamner.entity.Student;
import com.CodeExamner.entity.User;
import com.CodeExamner.entity.enums.UserRole;
import com.CodeExamner.repository.UserRepository;
import com.CodeExamner.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(user.getUsername());

            return ResponseEntity.ok(new AuthResponse(jwt, user.getId(), user.getUsername(), user.getRole()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse("登录失败: 用户名或密码错误"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new AuthResponse("用户名已存在"));
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new AuthResponse("邮箱已被注册"));
        }

        User user;
        if (registerRequest.getRole() == UserRole.STUDENT) {
            Student student = new Student();
            student.setStudentId(registerRequest.getStudentId());
            student.setRealName(registerRequest.getRealName());
            student.setClassName(registerRequest.getClassName());
            user = student;
        } else {
            user = new User();
        }

        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setRole(registerRequest.getRole());

        User savedUser = userRepository.save(user);
        String jwt = jwtUtil.generateToken(savedUser.getUsername());

        return ResponseEntity.ok(new AuthResponse(jwt, savedUser.getId(), savedUser.getUsername(), savedUser.getRole()));
    }
}