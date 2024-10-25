package repository;

import domain.User;
import exceptions.ValidationException;
import validator.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * In-memory implementation of the user repository
 */
public class InMemoryUserRepository implements Repository<String, User> {
    private final List<User> users;
    private final Validator<User> validator;

    public InMemoryUserRepository(Validator<User> validator) {
        this.users = new ArrayList<>();
        this.validator = validator;
    }

    @Override
    public void add(User user) throws ValidationException {
        validator.validate(user);
        users.add(user);
    }

    @Override
    public void remove(String id) {
        users.removeIf(user -> user.getId().equals(id));
    }

    @Override
    public Optional<User> findById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }
}