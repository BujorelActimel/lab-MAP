package service;

import domain.User;
import exceptions.ValidationException;
import org.junit.Before;
import org.junit.Test;
import repository.InMemoryUserRepository;
import repository.Repository;
import validator.UserValidator;

import java.util.List;

import static org.junit.Assert.*;

public class SocialNetworkServiceTest {
    private SocialNetworkService service;
    private Repository<String, User> repository;

    @Before
    public void setUp() {
        repository = new InMemoryUserRepository(new UserValidator());
        service = new SocialNetworkService(repository);
    }

    @Test
    public void testAddUser_ValidUser_ShouldAddSuccessfully() throws ValidationException {
        User user = new User("1", "John", "Doe");
        service.addUser(user);
        assertTrue(repository.findOne("1").isPresent());
    }

    @Test(expected = ValidationException.class)
    public void testAddUser_InvalidUser_ShouldThrowValidationException() throws ValidationException {
        User invalidUser = new User("", "", "");
        service.addUser(invalidUser);
    }

    @Test
    public void testAddFriendship_ValidUsers_ShouldCreateFriendship() throws ValidationException {
        User user1 = new User("1", "John", "Doe");
        User user2 = new User("2", "Jane", "Smith");
        service.addUser(user1);
        service.addUser(user2);

        service.addFriendship("1", "2");

        User updatedUser1 = repository.findOne("1").get();
        User updatedUser2 = repository.findOne("2").get();

        assertTrue(updatedUser1.getFriends().contains(user2));
        assertTrue(updatedUser2.getFriends().contains(user1));
    }

    @Test(expected = ValidationException.class)
    public void testAddFriendship_NonexistentUser_ShouldThrowValidationException() throws ValidationException {
        User user1 = new User("1", "John", "Doe");
        service.addUser(user1);
        service.addFriendship("1", "999");
    }

    @Test(expected = ValidationException.class)
    public void testAddFriendship_AlreadyFriends_ShouldThrowValidationException() throws ValidationException {
        User user1 = new User("1", "John", "Doe");
        User user2 = new User("2", "Jane", "Smith");
        service.addUser(user1);
        service.addUser(user2);

        service.addFriendship("1", "2");
        service.addFriendship("1", "2"); // Should throw exception
    }

    @Test
    public void testRemoveFriendship_ExistingFriendship_ShouldRemove() throws ValidationException {
        User user1 = new User("1", "John", "Doe");
        User user2 = new User("2", "Jane", "Smith");
        service.addUser(user1);
        service.addUser(user2);
        service.addFriendship("1", "2");

        service.removeFriendship("1", "2");

        User updatedUser1 = repository.findOne("1").get();
        User updatedUser2 = repository.findOne("2").get();

        assertFalse(updatedUser1.getFriends().contains(user2));
        assertFalse(updatedUser2.getFriends().contains(user1));
    }

    @Test
    public void testGetNumberOfCommunities_SingleCommunity() throws ValidationException {
        User user1 = new User("1", "John", "Doe");
        User user2 = new User("2", "Jane", "Smith");
        service.addUser(user1);
        service.addUser(user2);
        service.addFriendship("1", "2");

        assertEquals(1, service.getNumberOfCommunities());
    }

    @Test
    public void testGetNumberOfCommunities_MultipleCommunities() throws ValidationException {
        User user1 = new User("1", "John", "Doe");
        User user2 = new User("2", "Jane", "Smith");
        User user3 = new User("3", "Bob", "Wilson");
        User user4 = new User("4", "Alice", "Brown");

        service.addUser(user1);
        service.addUser(user2);
        service.addUser(user3);
        service.addUser(user4);

        service.addFriendship("1", "2");
        // users 3 and 4 are isolated

        assertEquals(3, service.getNumberOfCommunities());
    }

    @Test
    public void testGetMostSociableCommunity() throws ValidationException {
        // Create a network with two communities
        // Community 1: 1-2-3 (path length 2)
        // Community 2: 4-5-6-7 (path length 3)
        setupTestNetwork();

        List<User> mostSociable = service.getMostSociableCommunity();
        assertEquals(4, mostSociable.size());
    }

    private void setupTestNetwork() throws ValidationException {
        for (int i = 1; i <= 7; i++) {
            service.addUser(new User(String.valueOf(i), "User" + i, "Last" + i));
        }

        // Create first community
        service.addFriendship("1", "2");
        service.addFriendship("2", "3");

        // Create second community (larger)
        service.addFriendship("4", "5");
        service.addFriendship("5", "6");
        service.addFriendship("6", "7");
    }
}