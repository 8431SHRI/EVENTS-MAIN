package com.example.projects.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(nullable = false)
    private String username;

    @Valid
    @NotNull(message = "Credentials are required")
    @Embedded
    private Credentials credentials;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER; // ברירת מחדל משתמש רגיל

    @ManyToMany
    @JoinTable(
            name = "user_interests",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    private List<Interests> interests;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference(value = "user-registration")
    private List<Registration> registrations;

    public enum Role {
        ADMIN, INSTRUCTOR, USER
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // בודק קודם שהרול לא נאל (למנוע קריסה) ומוסיף את הקידומת ROLE_ עבור ספרינג
        String roleName = (this.role != null) ? this.role.name() : Role.USER.name();
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    @Override
    public String getPassword() {
        return credentials != null ? credentials.getPassword() : null;
    }

    @Override
    public String getUsername() {
        return credentials != null ? credentials.getEmail() : this.username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}