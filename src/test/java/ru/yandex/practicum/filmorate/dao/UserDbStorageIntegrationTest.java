package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.users.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class UserDbStorageIntegrationTest {
    private User user;
    private User user2;
    private User user3;

    @Autowired
    private UserDbStorage userDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcTemplate.update("DELETE FROM USERS");

        user = new User();
        user.setName("Block");
        user.setLogin("brick");
        user.setEmail("block@gmail.com");
        user.setBirthday(LocalDate.of(2002, 8, 6));

        user2 = new User();
        user2.setName("Brick");
        user2.setLogin("sew");
        user2.setEmail("fff@ee.tt");
        user2.setBirthday(LocalDate.of(2005, 1, 10));

        user3 = new User();
        user3.setName("Elisa");
        user3.setLogin("Gre");
        user3.setEmail("pp@ee.er");
        user3.setBirthday(LocalDate.of(1990, 5, 12));
    }

    @Test
    void testCreateUser() {
        User createdUser = userDbStorage.create(user);

        assertNotNull(createdUser.getId());
        assertEquals(createdUser.getName(), user.getName());
        assertEquals(createdUser.getLogin(), user.getLogin());
        assertEquals(createdUser.getEmail(), user.getEmail());
        assertEquals(createdUser.getBirthday(), user.getBirthday());
        assertTrue(userDbStorage.getAll().contains(createdUser));
    }

    @Test
    void testCreateUserWithEmptyEmail() {
        user.setEmail("");

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> userDbStorage.create(user));
        assertEquals(userDbStorage.getAll().size(), 0);
    }

    @Test
    void testCreateUserWithInvalidEmail() {
        user.setEmail("invalid");

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> userDbStorage.create(user));
        assertEquals(userDbStorage.getAll().size(), 0);
    }

    @Test
    void testCreateUserWithInvalidLogin() {
        user.setLogin("login spaces");

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> userDbStorage.create(user));
        assertEquals(userDbStorage.getAll().size(), 0);
    }

    @Test
    void testCreateUserWithFutureBirthday() {
        user.setBirthday(LocalDate.now().plusDays(1));

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> userDbStorage.create(user));
        assertEquals(userDbStorage.getAll().size(), 0);
    }

    @Test
    void testUpdateUser() {
        User createdUser = userDbStorage.create(user);

        createdUser.setName("Elina");
        userDbStorage.update(createdUser);

        assertEquals(userDbStorage.getAll().size(), 1);
        assertTrue(userDbStorage.getAll().contains(createdUser));
    }

    @Test
    void testUpdateUserWithEmptyEmail() {
        User createdUser = userDbStorage.create(user);

        createdUser.setEmail("");

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> userDbStorage.update(createdUser));
        assertEquals(userDbStorage.getAll().size(), 1);
    }

    @Test
    void testUpdateUserWithInvalidLogin() {
        User createdUser = userDbStorage.create(user);

        createdUser.setLogin("login ff");

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> userDbStorage.update(createdUser));
        assertEquals(userDbStorage.getAll().size(), 1);
    }

    @Test
    void testUpdateUserWithEmptyName() {
        User createdUser = userDbStorage.create(user);

        createdUser.setName("");
        User updatedUser = userDbStorage.update(createdUser);

        assertEquals(userDbStorage.getAll().size(), 1);
        assertTrue(userDbStorage.getAll().contains(updatedUser));
    }

    @Test
    void testUpdateUserWithFutureBirthday() {
        User createdUser = userDbStorage.create(user);

        createdUser.setBirthday(LocalDate.now().plusDays(1));
        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> userDbStorage.update(createdUser));
        assertEquals(userDbStorage.getAll().size(), 1);
    }

    @Test
    void testGetById() {
        User createdUser = userDbStorage.create(user);

        User retrievedUser = userDbStorage.getById(user.getId());

        assertEquals(createdUser.getName(), retrievedUser.getName());
        assertEquals(createdUser.getLogin(), retrievedUser.getLogin());
        assertEquals(createdUser.getEmail(), retrievedUser.getEmail());
        assertEquals(createdUser.getBirthday(), retrievedUser.getBirthday());
    }

    @Test
    void testGetByIdForNonExistentUser() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> userDbStorage.getById(9999L));
    }

    @Test
    void testGetAll() {
        User createdUser = userDbStorage.create(user);
        User createdUser2 = userDbStorage.create(user2);

        Collection<User> users = userDbStorage.getAll();

        assertTrue(users.contains(createdUser));
        assertTrue(users.contains(createdUser2));
        assertEquals(users.size(), 2);
    }

    @Test
    void testGetAllForEmptyDatabase() {
        Collection<User> films = userDbStorage.getAll();
        assertTrue(films.isEmpty());
    }

    @Test
    void testAddAsFriend() {
        User createdUser = userDbStorage.create(user);
        User createdUser2 = userDbStorage.create(user2);

        userDbStorage.addAsFriend(createdUser.getId(), createdUser2.getId());

        assertTrue(userDbStorage.getFriends(createdUser.getId()).contains(createdUser2));
        assertFalse(userDbStorage.getFriends(createdUser2.getId()).contains(createdUser));
    }

    @Test
    void testGetFriends() {
        User createdUser = userDbStorage.create(user);
        User createdUser2 = userDbStorage.create(user2);

        userDbStorage.addAsFriend(createdUser.getId(), createdUser2.getId());

        assertTrue(userDbStorage.getFriends(createdUser.getId()).contains(createdUser2));
        assertFalse(userDbStorage.getFriends(createdUser2.getId()).contains(createdUser));
        assertEquals(userDbStorage.getFriends(createdUser.getId()).size(), 1);
        assertEquals(userDbStorage.getFriends(createdUser2.getId()).size(), 0);
    }

    @Test
    void testGetMutualFriends() {
        User createdUser = userDbStorage.create(user);
        User createdUser2 = userDbStorage.create(user2);
        User createdUser3 = userDbStorage.create(user3);

        userDbStorage.addAsFriend(createdUser.getId(), createdUser2.getId());
        userDbStorage.addAsFriend(createdUser3.getId(), createdUser2.getId());

        assertTrue(userDbStorage.getMutualFriends(createdUser.getId(), createdUser3.getId()).contains(createdUser2));
        assertEquals(userDbStorage.getMutualFriends(createdUser.getId(), createdUser3.getId()).size(), 1);
    }

    @Test
    void testRemoveFromFriends() {
        User createdUser = userDbStorage.create(user);
        User createdUser2 = userDbStorage.create(user2);

        userDbStorage.addAsFriend(createdUser.getId(), createdUser2.getId());
        userDbStorage.removeFromFriends(createdUser.getId(), createdUser2.getId());

        assertFalse(userDbStorage.getFriends(createdUser.getId()).contains(createdUser2));
        assertEquals(userDbStorage.getFriends(createdUser.getId()).size(), 0);
    }
}
