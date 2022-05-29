package com.example.mockito;

import com.example.tddmain.dao.UserDao;
import com.example.tddmain.model.User;
import com.example.tddmain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ContextConfiguration(classes = {UserService.class, UserDao.class})
@SpringBootTest
public class TddWithMockitoSpyTest {

    private static final User IVAN = User.of(1L, "Ivan", 19, "123");
    private static final User PETR = User.of(2L, "Petr", 32, "111");

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @BeforeEach
    void init() {
        userDao = Mockito.spy(new UserDao());
        userService = new UserService(userDao);
    }

    @Test
    void shouldDeleteExistedUser() {
        userService.add(IVAN);

        Mockito.doReturn(true).when(userDao).delete(IVAN.getId()); //with spy works as expected only this
        //type of methods call: first - creating expected answer (stub) in doReturn, then use method. In other way
        //our spy will user real dao class because it will not see preinstalled answer

        //return answers.getOrDefault(userId, userDao.delete(userId)); spy method

        boolean deleteResult = userService.deleteUser(IVAN.getId());
        assertThat(deleteResult).isTrue();
    }

}
