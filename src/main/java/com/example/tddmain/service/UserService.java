package com.example.tddmain.service;

import com.example.tddmain.dao.UserDao;
import com.example.tddmain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService implements UserServiceApi {

    List<User> users = new ArrayList<>();
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void add(User... users) {
        this.users.addAll(Arrays.asList(users));
    }

    public boolean deleteUser(Long userId) {
        return userDao.delete(userId);
    }

    public List<User> getAll() {
        return users;
    }

    public Optional<User> login(String name, String password) {
        if (name == null || password == null) {
            throw new IllegalArgumentException("username or password is null");
        }
        return users.stream()
                    .filter(user -> user.getName().equals(name))
                    .filter(user -> user.getPassword().equals(password))
                    .findFirst();
    }

    public Map<Long, User> getAllConvertedById() {
        return users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
