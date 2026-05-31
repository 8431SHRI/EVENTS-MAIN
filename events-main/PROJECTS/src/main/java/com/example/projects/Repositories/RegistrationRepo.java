package com.example.projects.Repositories;

import com.example.projects.entities.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepo extends JpaRepository<Registration, Long> {

    // 1. בודק אם משתמש כבר רשום לאירוע מסוים (מונע כפילויות)
    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    // 2. סופר כמה אנשים סך הכל נרשמו לאירוע מסוים
    int countByEventId(Long eventId);

    // 3. סופר כמה אנשים נמצאים בסטטוס מסוים באירוע (למשל: כמה מאושרים או כמה בהמתנה)
    int countByEventIdAndStatus(Long eventId, Registration.RegistrationStatus status);

    // 4. מוצא רישום ספציפי לפי יוזר ואירוע (מעולה בשביל לבצע unregister)
    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);

    // 5. שולף את כל הרישומים של יוזר ספציפי (מהקובץ השני)
    List<Registration> findByUserId(Long userId);

    // 6. הלוגיקה של רשימת ההמתנה: שולף לפי סטטוס וממיין מהישן לחדש (פיפו - נרשמת קודם, תיכנס קודם)
    List<Registration> findByEventIdAndStatusOrderByCreatedAtAsc(
            Long eventId,
            Registration.RegistrationStatus status
    );
}