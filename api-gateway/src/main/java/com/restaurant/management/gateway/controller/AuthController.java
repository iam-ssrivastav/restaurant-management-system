package com.restaurant.management.gateway.controller;

import com.restaurant.management.gateway.config.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <h2>AuthController</h2>
 * <p>
 * Entry point for user authentication in the Restaurant Management System.
 * Implements the login logic and role mapping for staff and customers.
 * </p>
 * 
 * @author Shivam Srivastav
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Identity Management: Authentication attempt by user: {}", request.getUsername());

        if ("admin".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            return generateSuccessResponse(request.getUsername(), "ROLE_ADMIN,ROLE_USER,ROLE_MANAGER");
        }
        if ("manager".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            return generateSuccessResponse(request.getUsername(), "ROLE_MANAGER,ROLE_USER");
        }
        if ("chef".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            return generateSuccessResponse(request.getUsername(), "ROLE_CHEF,ROLE_USER");
        }
        if ("user".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            return generateSuccessResponse(request.getUsername(), "ROLE_USER");
        }
        if ("delivery".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            return generateSuccessResponse(request.getUsername(), "ROLE_DELIVERY,ROLE_USER");
        }

        return ResponseEntity.status(401).body("Invalid credentials provided.");
    }

    private ResponseEntity<?> generateSuccessResponse(String username, String roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        String token = jwtService.generateToken(username, claims);
        return ResponseEntity.ok(new TokenResponse(token));
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest() {
        }

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class TokenResponse {
        private String accessToken;

        public TokenResponse() {
        }

        public TokenResponse(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
