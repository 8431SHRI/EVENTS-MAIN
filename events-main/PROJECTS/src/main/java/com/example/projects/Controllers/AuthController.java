package com.example.projects.Controllers;

import com.example.projects.Jwt.JwtUtil;
import com.example.projects.entities.Users;
import com.example.projects.Services.UsersService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UsersService usersService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserDetailsService userDetailsService,
                          UsersService usersService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.usersService = usersService;
    }

    @PostMapping("/login")
    public String login(@RequestBody Users loginRequest) {
        String email = loginRequest.getCredentials().getEmail();
        String password = loginRequest.getCredentials().getPassword();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        final UserDetails user = userDetailsService.loadUserByUsername(email);
        return jwtUtil.generateToken(user);
    }

    @PostMapping("/register")
    public Users register(@Valid @RequestBody Users user) {
        return usersService.addUser(user);
    }

    // קלאס עזר פנימי במידה ותרצי להשתמש בו בעתיד לטובת Request Body ייעודי
    public static class CredentialsLoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}