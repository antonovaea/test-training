package com.example.mockito.dao;

import com.example.tddmain.dao.UserDao;

import java.util.HashMap;
import java.util.Map;

public class UserDaoMock extends UserDao {

    private Map<Integer, Boolean> answers = new HashMap<>();

    @Override
    public boolean delete(Long userId) {
        // invocation++ if we want to count how many times our mock method was called
        return super.delete(userId);
    }

    //@Override
    //    public boolean delete(Long userId) {
    //        return answers.getOrDefault(userId, false);
    //    }
}
