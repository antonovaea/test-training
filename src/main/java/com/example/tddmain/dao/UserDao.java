package com.example.tddmain.dao;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class UserDao {

    @SneakyThrows //catch exception
    public boolean delete(Long userId) {
        try (Connection connection = DriverManager.getConnection("url", "username", "password")) {
            return true;
        }
    }
}
