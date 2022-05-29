package com.example.mockito;

import com.example.tddmain.dao.UserDao;
import com.example.tddmain.model.User;
import com.example.tddmain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ContextConfiguration(classes = {UserService.class, UserDao.class})
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MockitoExtensionTest {

    private static final User IVAN = User.of(1L, "Ivan", 19, "123");
    private static final User PETR = User.of(2L, "Petr", 32, "111");

    @Captor
    private ArgumentCaptor<Long> argumentCaptor;
    @MockBean //or @Spy
    private UserDao userDao;
    @Autowired
//    @InjectMocks //class that uses mocks
    private UserService userService;

//    @BeforeEach
//    void init() {
//        userDao = Mockito.mock(UserDao.class);
//        userService = new UserService(userDao);
//    }
//
//    now we do not need this code because of new annotations added

    @Test
    void throwExceptionWhenDbIsNotAvailable() {
        Mockito.doThrow(RuntimeException.class).when(userDao).delete(IVAN.getId());
        assertThatThrownBy(() -> userService.deleteUser(IVAN.getId())).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldDeleteExistedUser() {

        userService.add(IVAN);

        Mockito.doReturn(true).when(userDao).delete(IVAN.getId()); //here we created stub for our mock
        //it means behaviour that we need our mock to do

        boolean deleteResult = userService.deleteUser(IVAN.getId());

//        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        Mockito.verify(userDao, Mockito.atMost(1)).delete(argumentCaptor.capture());
        //we can check how many times mock method is called

        assertThat(argumentCaptor.getValue()).isEqualTo(IVAN.getId());
        //Argument Captor helps us to see what exactly was passed to the mock method
        //there are cases that it may be not that we expected

        assertThat(deleteResult).isTrue();
    }
}
