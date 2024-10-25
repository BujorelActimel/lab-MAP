package repository;

import domain.User;
import exceptions.ValidationException;
import validator.Validator;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * File-based implementation of the user repository
 */
public class FileUserRepository implements Repository<String, User> {
    private final String filename;
    private final Validator<User> validator;
    private List<User> users;

    public FileUserRepository(String filename, Validator<User> validator) {
        this.filename = filename;
        this.validator = validator;
        this.users = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.add(new User(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (User user : users) {
                bw.write(String.format("%s,%s,%s%n", 
                    user.getId(), user.getFirstName(), user.getLastName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(User user) throws ValidationException {
        validator.validate(user);
        users.add(user);
        saveData();
    }

    @Override
    public void remove(String id) {
        users.removeIf(user -> user.getId().equals(id));
        saveData();
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
