package com.example.projects.Repositories;

import com.example.projects.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Long> {

    // 1. בודק האם המייל קיים במערכת (נכנס לתוך אובייקט Credentials) - מעולה לחסימת הרשמה כפולה
    boolean existsByCredentialsEmail(String email);

    // 2. שולף משתמש לפי המייל שלו (עטוף ב-Optional בשביל אבטחה וניהול שגיאות קל ב-Spring Security)
    Optional<Users> findByCredentialsEmail(String email);

    // 3. שולף משתמש לפי ה-Username שלו (אם תצטרכו בהמשך)
    Optional<Users> findByUsername(String username);
}