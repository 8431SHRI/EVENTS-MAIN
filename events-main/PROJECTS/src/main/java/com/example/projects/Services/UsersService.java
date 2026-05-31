package com.example.projects.Services;

import com.example.projects.Dtos.UserDTO;
import com.example.projects.Repositories.UsersRepo;
import com.example.projects.entities.Users;
import com.example.projects.exceptions.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService {

    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepo usersRepo, PasswordEncoder passwordEncoder) {
        this.usersRepo = usersRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public Users addUser(Users user) {
        if (usersRepo.existsByCredentialsEmail(user.getCredentials().getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (user.getRole() == null) {
            user.setRole(Users.Role.USER);
        }

        String encodedPassword = passwordEncoder.encode(user.getCredentials().getPassword());
        user.getCredentials().setPassword(encodedPassword);

        return usersRepo.save(user);
    }

    public List<Users> getAll() {
        return usersRepo.findAll();
    }

    public Users getUserById(Long id) {
        return usersRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public Users getUserByEmail(String email) {
        return usersRepo.findByCredentialsEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public Users updateUser(Long id, Users updatedUser) {
        Users existingUser = getUserById(id);

        existingUser.setUsername(updatedUser.getUsername());
        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());
        }
        if (updatedUser.getInterests() != null) {
            existingUser.setInterests(updatedUser.getInterests());
        }

        if (updatedUser.getCredentials() != null) {
            if (updatedUser.getCredentials().getEmail() != null) {
                existingUser.getCredentials().setEmail(updatedUser.getCredentials().getEmail());
            }
            if (updatedUser.getCredentials().getPassword() != null && !updatedUser.getCredentials().getPassword().isEmpty()) {
                existingUser.getCredentials().setPassword(passwordEncoder.encode(updatedUser.getCredentials().getPassword()));
            }
        }

        return usersRepo.save(existingUser);
    }

    public void deleteUser(Long id) {
        Users user = getUserById(id);
        usersRepo.delete(user);
    }

    public UserDTO convertToDTO(Users user) {
        String email = user.getCredentials() != null ? user.getCredentials().getEmail() : null;
        String roleName = user.getRole() != null ? user.getRole().name() : null;
        return new UserDTO(user.getId(), user.getUsername(), roleName, email);
    }
}