package com.example.demo.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class HashUtil {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder(); 

    public static String encode(String rawPassword) {
        // Implement your encoding logic here, e.g., using BCrypt
        return encoder.encode(rawPassword); // Placeholder, replace with actual encoding
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        // Implement your matching logic here
        return encoder.matches(rawPassword, hashedPassword); // Placeholder, replace with actual matching
    }

}
