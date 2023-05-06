package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private UserController userController;
    private User user;
    private Validator validator;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        user = new User();
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testPostUser() {
        user.setName("Block");
        user.setLogin("brick");
        user.setEmail("block@gmail.com");
        user.setBirthday(LocalDate.of(2002, 8, 6));

        assertEquals(userController.create(user), user, "Созданный пользователь отличается");
        assertEquals(userController.getAll().size(), 1, "Пользователь не добавлен в коллекцию");
    }

    @Test
    void testPostInvalidUser() {
        userController.create(user);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.size(), "Валидация прошла успешно");
    }

    @Test
    void testPostUserWithInvalidEmail() {
        user.setName("Block");
        user.setLogin("brick");
        user.setEmail("block#gmail.com");
        user.setBirthday(LocalDate.of(2002, 8, 6));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Почта должна содержать символ @", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void testPostUserWithoutEmail() {
        user.setName("Block");
        user.setLogin("brick");
        user.setBirthday(LocalDate.of(2002, 8, 6));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Почта не может быть пустой", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void testPostUserWithEmptyEmail() {
        user.setName("Block");
        user.setLogin("brick");
        user.setEmail("");
        user.setBirthday(LocalDate.of(2002, 8, 6));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Почта не может быть пустой", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void testPostUserWithoutName() {
        user.setLogin("brick");
        user.setEmail("block@gmail.com");
        user.setBirthday(LocalDate.of(2002, 8, 6));
        userController.create(user);

        user.setName(user.getLogin());
        assertTrue(userController.getAll().contains(user), "Пользователь не добавлен в коллекцию");
    }

    @Test
    void testPostUserEmptyName() {
        user.setLogin("brick");
        user.setName("");
        user.setEmail("block@gmail.com");
        user.setBirthday(LocalDate.of(2002, 8, 6));
        userController.create(user);

        user.setName(user.getLogin());
        assertTrue(userController.getAll().contains(user), "Пользователь не добавлен в коллекцию");
    }

    @Test
    void testPostUserWithSpaceInLogin() {
        user.setLogin("brick h");
        user.setName("Brick");
        user.setEmail("block@gmail.com");
        user.setBirthday(LocalDate.of(2002, 8, 6));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Логин не может содержать пробелы", violation.getMessage());
        assertEquals("login", violation.getPropertyPath().toString());
    }

    @Test
    void testPostUserWithoutLogin() {
        user.setName("Brick");
        user.setEmail("block@gmail.com");
        user.setBirthday(LocalDate.of(2002, 8, 6));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Логин не может быть пустым", violation.getMessage());
        assertEquals("login", violation.getPropertyPath().toString());
    }

    @Test
    void testPostUserWithFutureBirthday() {
        user.setLogin("brick");
        user.setName("Block");
        user.setEmail("block@gmail.com");
        user.setBirthday(LocalDate.of(2023, 8, 6));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Дата рождения не может быть в будущем", violation.getMessage());
        assertEquals("birthday", violation.getPropertyPath().toString());
    }

    @Test
    void testPostUserWithoutBirthday() {
        user.setLogin("brick");
        user.setName("Block");
        user.setEmail("block@gmail.com");

        assertEquals(userController.create(user), user, "Созданный пользователь отличается");
        assertEquals(userController.getAll().size(), 1, "Пользователь не добавлен в коллекцию");
    }

    @Test
    void testUpdateUser() {
        user.setLogin("brick");
        user.setName("Block");
        user.setEmail("block@gmail.com");
        userController.create(user);

        user.setName("Elina");
        userController.update(user);

        assertEquals(userController.getAll().size(), 1, "Размер коллекции изменился");
        assertTrue(userController.getAll().contains(user), "Пользователь в коллекции отличается");
    }

    @Test
    void testUpdateNonExistentUser() {
        user.setId(200);

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> userController.update(user));
        assertEquals(userController.getAll().size(), 0, "Размер коллекции изменился");
    }

    @Test
    void testUpdateUserWithInvalidData() {
        user.setLogin("brick");
        user.setName("Block");
        user.setEmail("block@gmail.com");
        userController.create(user);

        user.setEmail("elinagmail.com");
        user.setLogin("elina brick");
        userController.update(user);

        assertEquals(userController.getAll().size(), 1, "Размер коллекции изменился");
        assertTrue(userController.getAll().contains(user), "Пользователь в коллекции отличается");
    }
}