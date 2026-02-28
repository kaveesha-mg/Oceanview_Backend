package com.oceanview.config;

import com.oceanview.model.Room;
import com.oceanview.model.User;
import com.oceanview.repository.RoomRepository;
import com.oceanview.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(UserRepository userRepo, RoomRepository roomRepo, PasswordEncoder encoder) {
        return args -> {
            if (userRepo.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setFullName("Super Admin");
                admin.setEmail("admin@oceanview.com");
                admin.setRole(User.Role.SUPER_ADMIN);
                userRepo.save(admin);
            }
            if (roomRepo.count() == 0) {
                String[][] rooms = {
                        {"101", "Standard", "15000", "Standard room with ocean view"},
                        {"102", "Standard", "15000", "Standard room with ocean view"},
                        {"201", "Deluxe", "25000", "Deluxe room with balcony"},
                        {"202", "Deluxe", "25000", "Deluxe room with balcony"},
                        {"301", "Suite", "40000", "Luxury suite with private terrace"}
                };
                for (String[] r : rooms) {
                    Room room = new Room();
                    room.setRoomNumber(r[0]);
                    room.setRoomType(r[1]);
                    room.setRatePerNight(Double.parseDouble(r[2]));
                    room.setDescription(r[3]);
                    room.setAvailable(true);
                    roomRepo.save(room);
                }
            }
        };
    }
}
