package service;

import domain.User;
import domain.Friendship;
import repository.Repository;
import exceptions.ValidationException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service class that implements the business logic
 */
public class SocialNetworkService {
    private final Repository<String, User> userRepository;
    private final List<Friendship> friendships;

    public SocialNetworkService(Repository<String, User> userRepository) {
        this.userRepository = userRepository;
        this.friendships = new ArrayList<>();
    }

    public void addUser(User user) throws ValidationException {
        userRepository.save(user)
                .ifPresent(u -> {
                    throw new IllegalArgumentException("User already exists!");
                });
    }

    public void removeUser(String userId) {
        userRepository.delete(userId);
        friendships.removeIf(friendship -> 
            friendship.getUser1().getId().equals(userId) ||
            friendship.getUser2().getId().equals(userId));
    }

    public void addFriendship(String userId1, String userId2) throws ValidationException {
        User user1 = userRepository.findOne(userId1)
                .orElseThrow(() -> new ValidationException("First user doesn't exist!"));
        User user2 = userRepository.findOne(userId2)
                .orElseThrow(() -> new ValidationException("Second user doesn't exist!"));

        if (user1.getFriends().contains(user2)) {
            throw new ValidationException("Users are already friends!");
        }

        user1.getFriends().add(user2);
        user2.getFriends().add(user1);

        friendships.add(new Friendship(UUID.randomUUID().toString(), user1, user2));
    }

    public void removeFriendship(String userId1, String userId2) {
        Optional<User> user1 = userRepository.findOne(userId1);
        Optional<User> user2 = userRepository.findOne(userId2);

        if (user1.isPresent() && user2.isPresent()) {
            User u1 = user1.get();
            User u2 = user2.get();

            u1.getFriends().remove(u2);
            u2.getFriends().remove(u1);

            friendships.removeIf(friendship ->
                (friendship.getUser1().equals(u1) && friendship.getUser2().equals(u2)) ||
                (friendship.getUser1().equals(u2) && friendship.getUser2().equals(u1)));
        }
    }

    public int getNumberOfCommunities() {
        Set<User> visited = new HashSet<>();
        int communities = 0;

        for (User user : userRepository.findAll()) {
            if (!visited.contains(user)) {
                communities++;
                dfs(user, visited);
            }
        }

        return communities;
    }

    public List<User> getMostSociableCommunity() {
        List<User> mostSociable = new ArrayList<>();
        int maxPathLength = -1;

        // Convert Iterable to List using StreamSupport
        List<User> allUsers = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        for (User startUser : allUsers) {
            Set<User> visited = new HashSet<>();
            int[] maxLength = {0};

            dfsWithPath(startUser, visited, 0, maxLength);

            if (maxLength[0] > maxPathLength) {
                maxPathLength = maxLength[0];
                Set<User> communityVisited = new HashSet<>();
                dfs(startUser, communityVisited);
                mostSociable = new ArrayList<>(communityVisited);
            }
        }

        return mostSociable;
    }

    private void dfs(User user, Set<User> visited) {
        visited.add(user);
        for (User friend : user.getFriends()) {
            if (!visited.contains(friend)) {
                dfs(friend, visited);
            }
        }
    }

    private void dfsWithPath(User user, Set<User> visited, int currentLength, int[] maxLength) {
        visited.add(user);
        maxLength[0] = Math.max(maxLength[0], currentLength);

        for (User friend : user.getFriends()) {
            if (!visited.contains(friend)) {
                dfsWithPath(friend, visited, currentLength + 1, maxLength);
            }
        }
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Friendship> getAllFriendships() {
        return new ArrayList<>(friendships);
    }
}