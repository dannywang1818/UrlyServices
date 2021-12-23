package com.urly.urlyservices.controller;

import com.urly.urlyservices.annotation.CurrentUser;
import com.urly.urlyservices.db.entity.User;
import com.urly.urlyservices.db.repository.UserRepository;
import com.urly.urlyservices.security.userdetail.UserDetailsImpl;
import com.urly.urlyservices.service.UserService;
import com.urly.urlyservices.vo.request.LoginRequest;
import com.urly.urlyservices.vo.request.SignupRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return userService.signup(signupRequest);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserDetailsImpl userDetails) {
        Optional<User> userOptional = userRepository.findById(userDetails.getId());
        if(userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User info not found: " + userDetails.getEmail());
        }
        return userOptional.get();
    }

}
