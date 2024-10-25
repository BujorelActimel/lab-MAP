package service;

import domain.User;
import exceptions.ValidationException;
import org.junit.Before;
import org.junit.Test;
import repository.InMemoryUserRepository;
import validator.UserValidator;

import static org.junit.Assert.*;

public class SocialNetworkServiceTest {
    private SocialNetworkService service;
    
    @Before
    public void setUp() {
        service = new SocialNetworkService(new InMemoryUserRepository(new UserValidator()));
    }
    
    @Test
    public void testAddUser() throws ValidationException {
        User user = new User("1", "John", "Doe");
        service.addUser(user);
        assertEquals(1, service.getNumberOfCommunities());
    }
    
    @Test(expected = ValidationException.class)
    public void testAddInvalidUser() throws ValidationException {
        User user = new User("", "", "");
        service.addUser(user);
    }
    
    @Test
    public void testAddFriendship() throws ValidationException {
        User user1 = new User("1", "John", "Doe");
        User user2 = new User("2", "Jane", "Doe");
        service.addUser(user1);
        service.addUser(user2);
        service.addFriendship("1", "2");
        assertEquals(1, service.getNumberOfCommunities());
    }
}