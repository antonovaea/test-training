package com.example.mockito.dao;

import com.example.tddmain.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(classes = {UserDao.class})
public class UserDaoSpy extends UserDao {

    private final UserDao userDao;
    private Map<Long, Boolean> answers = new HashMap<>();

    @Autowired
    public UserDaoSpy(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean delete(Long userId) {
        return answers.getOrDefault(userId, userDao.delete(userId));
    }
}
