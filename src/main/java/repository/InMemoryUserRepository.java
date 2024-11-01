package repository;

import domain.User;
import exceptions.ValidationException;
import validator.Validator;
import java.util.*;

/**
 * In-memory implementation of the user repository
 */
public class InMemoryUserRepository implements Repository<String, User> {
    private final Map<String, User> users;
    private final Validator<User> validator;

    public InMemoryUserRepository(Validator<User> validator) {
        this.users = new HashMap<>();
        this.validator = validator;
    }

    @Override
    public Optional<User> findOne(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Iterable<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> save(User entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }
        validator.validate(entity);
        return Optional.ofNullable(users.putIfAbsent(entity.getId(), entity));
    }

    @Override
    public Optional<User> delete(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        return Optional.ofNullable(users.remove(id));
    }

    @Override
    public Optional<User> update(User entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }
        validator.validate(entity);
        
        if (users.containsKey(entity.getId())) {
            users.put(entity.getId(), entity);
            return Optional.empty();
        }
        return Optional.of(entity);
    }
}