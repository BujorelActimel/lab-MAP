package service;

import domain.User;
import domain.Friendship;
import repository.DatabaseUserRepository;
import repository.Repository;
import exceptions.ValidationException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SocialNetworkService {
    private final Repository<String, User> userRepository;

    public SocialNetworkService(Repository<String, User> userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) throws ValidationException {
        userRepository.save(user)
                .ifPresent(u -> {
                    throw new IllegalArgumentException("User already exists!");
                });
    }

    public void removeUser(String userId) {
        userRepository.delete(userId);
    }

    public void addFriendship(String userId1, String userId2) throws ValidationException {
        User user1 = userRepository.findOne(userId1)
                .orElseThrow(() -> new ValidationException("First user doesn't exist!"));
        User user2 = userRepository.findOne(userId2)
                .orElseThrow(() -> new ValidationException("Second user doesn't exist!"));

        if (user1.getFriends().contains(user2)) {
            throw new ValidationException("Users are already friends!");
        }

        String friendshipId = UUID.randomUUID().toString();
        
        if (userRepository instanceof DatabaseUserRepository dbRepo) {
            dbRepo.saveFriendship(friendshipId, userId1, userId2);
        }

        user1.getFriends().add(user2);
        user2.getFriends().add(user1);
    }

    public void removeFriendship(String userId1, String userId2) {
        Optional<User> user1 = userRepository.findOne(userId1);
        Optional<User> user2 = userRepository.findOne(userId2);

        if (user1.isPresent() && user2.isPresent()) {
            User u1 = user1.get();
            User u2 = user2.get();

            if (userRepository instanceof DatabaseUserRepository dbRepo) {
                dbRepo.deleteFriendship(userId1, userId2);
            }

            u1.getFriends().remove(u2);
            u2.getFriends().remove(u1);
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
}