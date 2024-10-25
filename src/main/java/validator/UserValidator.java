package validator;

import domain.User;
import exceptions.ValidationException;

/**
 * Validator implementation for User entities
 */
public class UserValidator implements Validator<User> {
    @Override
    public void validate(User user) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        if (user.getId() == null || user.getId().trim().isEmpty()) {
            errors.append("Id cannot be null or empty! ");
        }

        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            errors.append("First name cannot be null or empty! ");
        }

        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            errors.append("Last name cannot be null or empty! ");
        }

        if (errors.length() > 0) {
            throw new ValidationException(errors.toString());
        }
    }
}
