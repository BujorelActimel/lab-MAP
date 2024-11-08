package repository;

import domain.User;
import exceptions.ValidationException;
import validator.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseUserRepository implements Repository<String, User> {
    private final String url;
    private final Validator<User> validator;

    public DatabaseUserRepository(String dbFile, Validator<User> validator) {
        this.url = "jdbc:sqlite:" + dbFile;
        this.validator = validator;
        initDatabase();
    }

    private void initDatabase() {
        try (Connection connection = DriverManager.getConnection(url)) {
            // Create users table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id TEXT PRIMARY KEY,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL
                )
            """;
            
            // Create friendships table
            String createFriendshipsTable = """
                CREATE TABLE IF NOT EXISTS friendships (
                    id TEXT PRIMARY KEY,
                    user1_id TEXT NOT NULL,
                    user2_id TEXT NOT NULL,
                    FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """;

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
                stmt.execute(createUsersTable);
                stmt.execute(createFriendshipsTable);
            }

            // Verificăm dacă tabela users este goală
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
                if (rs.next() && rs.getInt(1) == 0) {
                    // Dacă e goală, populăm cu datele inițiale
                    populateInitialData(connection);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private void populateInitialData(Connection connection) throws SQLException {
        String[][] initialUsers = {
            {"1", "John", "Doe"},
            {"2", "Jane", "Smith"},
            {"3", "Michael", "Johnson"},
            {"4", "Sarah", "Williams"},
            {"5", "David", "Brown"},
            {"6", "Emily", "Jones"},
            {"7", "James", "Wilson"},
            {"8", "Emma", "Taylor"},
            {"9", "Robert", "Anderson"},
            {"10", "Olivia", "Thomas"},
            {"11", "Ion", "Popescu"},
            {"12", "Maria", "Ionescu"},
            {"13", "Alex", "Popa"},
            {"14", "Elena", "Dumitru"},
            {"15", "Andrei", "Radu"},
            {"16", "Ana", "Stan"},
            {"17", "George", "Munteanu"},
            {"18", "Laura", "Gheorghe"},
            {"19", "Daniel", "Stoica"},
            {"20", "Diana", "Matei"},
            {"21", "Mihai", "Bujor"}
        };

        String insertUserSQL = "INSERT INTO users (id, first_name, last_name) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUserSQL)) {
            for (String[] user : initialUsers) {
                pstmt.setString(1, user[0]);
                pstmt.setString(2, user[1]);
                pstmt.setString(3, user[2]);
                pstmt.executeUpdate();
            }
        }

        System.out.println("Database has been populated with initial data successfully!");
    }

    @Override
    public Optional<User> findOne(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                User user = new User(
                    resultSet.getString("id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name")
                );
                loadFriends(user, connection);
                return Optional.of(user);
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user", e);
        }
    }

    private void loadFriends(User user, Connection connection) throws SQLException {
        String sql = """
            SELECT u.* FROM users u
            JOIN friendships f ON (f.user2_id = u.id AND f.user1_id = ?)
            OR (f.user1_id = u.id AND f.user2_id = ?)
        """;
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getId());
            statement.setString(2, user.getId());
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                User friend = new User(
                    resultSet.getString("id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name")
                );
                user.getFriends().add(friend);
            }
        }
    }

    @Override
    public Iterable<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                User user = new User(
                    resultSet.getString("id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name")
                );
                loadFriends(user, connection);
                users.add(user);
            }
            return users;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all users", e);
        }
    }

    @Override
    public Optional<User> save(User entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }
        validator.validate(entity);

        String sql = "INSERT INTO users (id, first_name, last_name) VALUES (?, ?, ?)";
        
        try (Connection connection = DriverManager.getConnection(url)) {
            // Check if user exists
            Optional<User> existing = findOne(entity.getId());
            if (existing.isPresent()) {
                return existing;
            }

            // Save new user
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, entity.getId());
                statement.setString(2, entity.getFirstName());
                statement.setString(3, entity.getLastName());
                statement.executeUpdate();
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public Optional<User> delete(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        Optional<User> user = findOne(id);
        if (user.isEmpty()) {
            return Optional.empty();
        }

        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, id);
            statement.executeUpdate();
            return user;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public Optional<User> update(User entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }
        validator.validate(entity);

        String sql = "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?";
        
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getId());
            
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                return Optional.of(entity);
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    public void saveFriendship(String id, String userId1, String userId2) {
        String sql = "INSERT INTO friendships (id, user1_id, user2_id) VALUES (?, ?, ?)";
        
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, id);
            statement.setString(2, userId1);
            statement.setString(3, userId2);
            statement.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save friendship", e);
        }
    }

    public void deleteFriendship(String userId1, String userId2) {
        String sql = "DELETE FROM friendships WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, userId1);
            statement.setString(2, userId2);
            statement.setString(3, userId2);
            statement.setString(4, userId1);
            statement.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete friendship", e);
        }
    }
}