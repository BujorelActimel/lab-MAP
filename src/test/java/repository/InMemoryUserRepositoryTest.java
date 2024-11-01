package repository;

import domain.User;
import exceptions.ValidationException;
import org.junit.Before;
import org.junit.Test;
import validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class InMemoryUserRepositoryTest {
    private Repository<String, User> repository;
    private User testUser;

    @Before
    public void setUp() {
        repository = new InMemoryUserRepository(new UserValidator());
        testUser = new User("1", "John", "Doe");
    }

    @Test
    public void testSave_ValidUser_ShouldSaveSuccessfully() throws ValidationException {
        Optional<User> result = repository.save(testUser);
        assertTrue(result.isEmpty());
        assertEquals(testUser, repository.findOne("1").get());
    }

    @Test(expected = ValidationException.class)
    public void testSave_InvalidUser_ShouldThrowValidationException() throws ValidationException {
        User invalidUser = new User("", "", "");
        repository.save(invalidUser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSave_NullUser_ShouldThrowIllegalArgumentException() throws ValidationException {
        repository.save(null);
    }

    @Test
    public void testSave_DuplicateId_ShouldReturnExistingUser() throws ValidationException {
        repository.save(testUser);
        User duplicateUser = new User("1", "Jane", "Smith");
        Optional<User> result = repository.save(duplicateUser);
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    public void testFindOne_ExistingUser_ShouldReturnUser() throws ValidationException {
        repository.save(testUser);
        Optional<User> result = repository.findOne("1");
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    public void testFindOne_NonexistentUser_ShouldReturnEmpty() {
        Optional<User> result = repository.findOne("999");
        assertTrue(result.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindOne_NullId_ShouldThrowIllegalArgumentException() {
        repository.findOne(null);
    }

    @Test
    public void testDelete_ExistingUser_ShouldDeleteAndReturnUser() throws ValidationException {
        repository.save(testUser);
        Optional<User> deleted = repository.delete("1");
        assertTrue(deleted.isPresent());
        assertTrue(repository.findOne("1").isEmpty());
    }

    @Test
    public void testDelete_NonexistentUser_ShouldReturnEmpty() {
        Optional<User> deleted = repository.delete("999");
        assertTrue(deleted.isEmpty());
    }

    @Test
    public void testUpdate_ExistingUser_ShouldUpdateSuccessfully() throws ValidationException {
        repository.save(testUser);
        User updatedUser = new User("1", "John Updated", "Doe Updated");
        Optional<User> result = repository.update(updatedUser);
        assertTrue(result.isEmpty());
        assertEquals("John Updated", repository.findOne("1").get().getFirstName());
    }

    @Test
    public void testUpdate_NonexistentUser_ShouldReturnUser() throws ValidationException {
        User newUser = new User("999", "New", "User");
        Optional<User> result = repository.update(newUser);
        assertTrue(result.isPresent());
        assertEquals(newUser, result.get());
    }

    @Test
    public void testFindAll_EmptyRepository_ShouldReturnEmptyList() {
        Iterable<User> all = repository.findAll();
        assertFalse(all.iterator().hasNext());
    }

    @Test
    public void testFindAll_WithUsers_ShouldReturnAllUsers() throws ValidationException {
        repository.save(testUser);
        repository.save(new User("2", "Jane", "Smith"));
        
        List<User> users = new ArrayList<>();
        repository.findAll().forEach(users::add);
        
        assertEquals(2, users.size());
    }
}
