package repository;

import domain.User;
import exceptions.ValidationException;
import validator.Validator;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * File-based implementation of the user repository
 */
public class FileUserRepository implements Repository<String, User> {
    private final String filename;
    private final Validator<User> validator;
    private final Map<String, User> users;

    public FileUserRepository(String filename, Validator<User> validator) {
        this.filename = filename;
        this.validator = validator;
        this.users = new HashMap<>();
        loadData();
    }

    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.lines()
                .filter(line -> !line.trim().isEmpty())
                .map(this::parseLine)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(user -> users.put(user.getId(), user));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<User> parseLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                return Optional.of(new User(parts[0].trim(), parts[1].trim(), parts[2].trim()));
            }
        } catch (Exception e) {
            System.err.println("Error parsing line: " + line);
        }
        return Optional.empty();
    }

    private void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            users.values().stream()
                .map(this::formatUser)
                .forEach(line -> {
                    try {
                        bw.write(line);
                        bw.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatUser(User user) {
        return String.format("%s,%s,%s",
            user.getId(),
            user.getFirstName(),
            user.getLastName());
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
        Optional<User> existingUser = Optional.ofNullable(users.putIfAbsent(entity.getId(), entity));
        if (existingUser.isEmpty()) {
            saveData();
        }
        return existingUser;
    }

    @Override
    public Optional<User> delete(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        Optional<User> removedUser = Optional.ofNullable(users.remove(id));
        if (removedUser.isPresent()) {
            saveData();
        }
        return removedUser;
    }

    @Override
    public Optional<User> update(User entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }
        validator.validate(entity);
        
        if (users.containsKey(entity.getId())) {
            users.put(entity.getId(), entity);
            saveData();
            return Optional.empty();
        }
        return Optional.of(entity);
    }
}
