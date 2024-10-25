package validator;

import domain.User;
import exceptions.ValidationException;
import org.junit.Test;

public class UserValidatorTest {
    private final UserValidator validator = new UserValidator();
    
    @Test
    public void testValidUser() throws ValidationException {
        User user = new User("1", "John", "Doe");
        validator.validate(user);
    }
    
    @Test(expected = ValidationException.class)
    public void testInvalidId() throws ValidationException {
        User user = new User("", "John", "Doe");
        validator.validate(user);
    }
    
    @Test(expected = ValidationException.class)
    public void testInvalidFirstName() throws ValidationException {
        User user = new User("1", "", "Doe");
        validator.validate(user);
    }
}