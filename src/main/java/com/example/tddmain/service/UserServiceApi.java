package com.example.tddmain.service;

import com.example.tddmain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserServiceApi {

    void add(User... users);

    List<User> getAll();

    Optional<User> login(String name, String password);
}
