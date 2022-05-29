package com.example.mockito;

import com.example.tddmain.dao.UserDao;
import com.example.tddmain.model.User;
import com.example.tddmain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ContextConfiguration(classes = {UserService.class, UserDao.class})
@SpringBootTest
public class TddWithMockitoTest {

    private static final User IVAN = User.of(1L, "Ivan", 19, "123");
    private static final User PETR = User.of(2L, "Petr", 32, "111");

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @BeforeEach
    void init() {
        userDao = Mockito.mock(UserDao.class);
        userService = new UserService(userDao);
    }

    @Test
    void shouldDeleteExistedUser() {
        userService.add(IVAN);

        Mockito.doReturn(true).when(userDao).delete(IVAN.getId()); //here we created stub for our mock
        //it means behaviour that we need our mock to do

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        Mockito.verify(userDao, Mockito.atMost(1)).delete(argumentCaptor.capture());
        //we can check how many times mock method is called

        assertThat(argumentCaptor.getValue()).isEqualTo(IVAN.getId());
        //Argument Captor helps us to see what exactly was passed to the mock method
        //there are cases that it may be not that we expected

        boolean deleteResult = userService.deleteUser(IVAN.getId());
        assertThat(deleteResult).isTrue();
    }

    @Test
    void shouldDeleteExistedUser2() {
        userService.add(IVAN);

        Mockito.when(userDao.delete(IVAN.getId()))
               .thenReturn(true)
               .thenReturn(false);

        boolean deleteResult = userService.deleteUser(IVAN.getId());
        assertThat(deleteResult).isTrue();

        boolean deleteResult2 = userService.deleteUser(IVAN.getId());
        assertThat(deleteResult2).isFalse();

    }

    @Test
    void shouldDeleteExistedUser3() {
        userService.add(IVAN);

        Mockito.when(userDao.delete(Mockito.any())) //Mockito.any() if this value is not important for us
               .thenReturn(true)
               .thenReturn(false);

        boolean deleteResult = userService.deleteUser(IVAN.getId());
        assertThat(deleteResult).isTrue();

        boolean deleteResult2 = userService.deleteUser(IVAN.getId());
        assertThat(deleteResult2).isFalse();

    }



}
