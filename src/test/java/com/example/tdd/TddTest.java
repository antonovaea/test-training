package com.example.tdd;

import com.example.TestBase;
import com.example.myextension.MyConditionalExtension;
import com.example.tddmain.dao.UserDao;
import com.example.tddmain.model.User;
import com.example.tddmain.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.RepeatedTest.SHORT_DISPLAY_NAME;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {UserService.class, UserDao.class})
@SpringBootTest
//@ExtendWith(MyGlobalExtension.class) is in TestBase class
@ExtendWith({MyConditionalExtension.class,
//        PostProcessingExtension.class,
//        ThrowableExtension.class
})
class TddTest extends TestBase {

    private static final User IVAN = User.of(1L, "Ivan", 19, "123");
    private static final User PETR = User.of(2L, "Petr", 32, "111");

    @Autowired
    private UserService userService;

    @BeforeEach
    void init() {
        userService = new UserService(null);
    }

    @Test
    @Order(1)
    @DisplayName("creating and adding new user  to DB")
    @Disabled("flucky test, not stable")
    void addNewUser() {
        userService.add(IVAN, PETR);
        List<User> users = userService.getAll();
        assertThat(users).describedAs("list with users").hasSize(2);
    }

    @RepeatedTest(value = 5, name = SHORT_DISPLAY_NAME)
    @Order(2)
    void usersAreEmptyIfNoUserAdded() {
        List<User> all = userService.getAll();
        assertThat(all).isEmpty();
    }

    @Test
    void throwExceptionIfUserNameOrPasswordIsNull() {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> userService.login("name", null)),
                  () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "123")));
    }

    @Test
    void throwExceptionIfUserNameOrPasswordIsNull2() {
        assertAll(() -> assertThatThrownBy(() -> userService.login("IVAN", null)).isInstanceOf(IllegalArgumentException.class).hasMessage("username or password is null"), () -> assertThatThrownBy(() -> userService.login(null, "123")).isInstanceOf(IllegalArgumentException.class).hasMessage("username or password is null"));
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);
        Map<Long, User> users = userService.getAllConvertedById();
        assertAll(() -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                  () -> assertThat(users).containsValues(IVAN, PETR));
    }

    @Nested
    @Tag("login")
    @Timeout(value = 200L, unit = TimeUnit.MILLISECONDS) //covers all tests in annotated class with timeout
    class LoginTest {

        @Test
        void loginSuccessfulIfUserExists() {
            userService.add(IVAN);
            Optional<User> user = userService.login(IVAN.getName(), IVAN.getPassword());
            assertThat(user).describedAs("user").isPresent();
            user.ifPresent(user1 -> assertThat(user1).isEqualTo(IVAN));
        }

        @Test
        void loginFailedIfPasswordIsWrong() {
            userService.add(PETR);
            Optional<User> user = userService.login("PETR", "wrong");
            assertThat(user).isNotPresent();
        }

        @Test
        void loginFailedIfNameIsWrong() {
            userService.add(PETR);
            Optional<User> user = userService.login("WRONG", "111");
            assertThat(user).isNotPresent();
        }

        @Test
        void checkLoginFunctionalityPerformance() {
            userService.add(IVAN);
            Optional<User> user = assertTimeout(Duration.ofMillis(200L), () -> userService.login(IVAN.getName(),
                                                                                                 IVAN.getPassword()));
            assertThat(user).isPresent().isEqualTo(Optional.of(IVAN));
        }

        @ParameterizedTest
        @MethodSource("getArgumentsForLoginTest")
        void loginParametrizedTest(String username, String password, Optional<User> user) {
            userService.add(IVAN, PETR);
            Optional<User> maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(user);
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)
        void loginParametrizedTest2(String username, String password) {
            userService.add(IVAN, PETR);
            Optional<User> maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(Optional.of(IVAN));
        }

        @ParameterizedTest
        @CsvSource({"Ivan, wrong", "wrong, 111"})
        void loginParametrizedTest3(String username, String password) {
            userService.add(IVAN, PETR);
            Optional<User> maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(Optional.empty());
        }

        static Stream<Arguments> getArgumentsForLoginTest() {
            return Stream.of(Arguments.of("Ivan", "123", Optional.of(IVAN)),
                             Arguments.of("Petr", "111", Optional.of(PETR)),
                             Arguments.of("Petr", "wrong", Optional.empty()),
                             Arguments.of("wrong", "123", Optional.empty()));
        }
    }

}
