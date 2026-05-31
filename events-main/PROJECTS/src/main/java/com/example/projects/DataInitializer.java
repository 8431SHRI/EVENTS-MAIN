//package com.example.projects;
//
//import com.example.projects.entities.Events;
//import com.example.projects.entities.Interests;
//import com.example.projects.entities.Users;
//import com.example.projects.Repositories.EventsRepo;
//import com.example.projects.Repositories.UsersRepo;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    private final UsersRepo usersRepository;
//    private final EventsRepo eventsRepository;
//
//    public DataInitializer(UsersRepo usersRepository, EventsRepo eventsRepository) {
//        this.usersRepository = usersRepository;
//        this.eventsRepository = eventsRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // הוספת משתמשים לדאטהבייס
//        Users user1 = new Users(123456789L, "john_doe", "password123", "john@example.com", Users.Role.USER, null, null);
//        Users user2 = new Users(987654321L, "jane_doe", "password456", "jane@example.com", Users.Role.ADMIN, null, null);
//
//        usersRepository.save(user1);
//        usersRepository.save(user2);
//        // הוספת תחומי עניין לדאטהבייס
//        Interests interest1 = new Interests("Music");
//        Interests interest2 = new Interests("Technology",null);
//
//        interestsRepo.save(interest1);
//        interestsRepo.save(interest2);
//
//
//        // הוספת אירועים לדאטהבייס
//        Events event1 = new Events("Music Concert", "A great music event.", 100, LocalDate.of(2023, 12, 15), "Tel Aviv", DayOfWeek.FRIDAY, LocalTime.of(20, 0), user1, interest1, null);
//        Events event2 = new Events("Tech Conference", "A conference about new technologies.", 150, LocalDate.of(2023, 11, 20), "Jerusalem", DayOfWeek.MONDAY, LocalTime.of(9, 0), user2, interest2, null);
//        eventsRepository.save(event1);
//        eventsRepository.save(event2);
//
//        System.out.println("Data has been initialized!");
//    }
//}