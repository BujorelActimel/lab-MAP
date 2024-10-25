package service;

import domain.User;
import domain.Friendship;
import repository.Repository;
import exceptions.ValidationException;
import java.util.*;

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
        userRepository.add(user);
    }

    public void removeUser(String userId) {
        userRepository.remove(userId);
        friendships.removeIf(friendship -> 
            friendship.getUser1().getId().equals(userId) ||
            friendship.getUser2().getId().equals(userId));
    }

    public void addFriendship(String userId1, String userId2) throws ValidationException {
        Optional<User> user1 = userRepository.findById(userId1);
        Optional<User> user2 = userRepository.findById(userId2);

        if (user1.isEmpty() || user2.isEmpty()) {
            throw new ValidationException("One or both users don't exist!");
        }

        User u1 = user1.get();
        User u2 = user2.get();

        if (u1.getFriends().contains(u2)) {
            throw new ValidationException("Users are already friends!");
        }

        u1.getFriends().add(u2);
        u2.getFriends().add(u1);

        friendships.add(new Friendship(UUID.randomUUID().toString(), u1, u2));
    }

    public void removeFriendship(String userId1, String userId2) {
        Optional<User> user1 = userRepository.findById(userId1);
        Optional<User> user2 = userRepository.findById(userId2);

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

        for (User startUser : userRepository.findAll()) {
            List<User> currentCommunity = new ArrayList<>();
            Set<User> visited = new HashSet<>();
            int[] maxLength = {0};

            dfsWithPath(startUser, visited, 0, maxLength);

            if (maxLength[0] > maxPathLength) {
                maxPathLength = maxLength[0];
                currentCommunity.clear();
                dfs(startUser, new HashSet<>());
                currentCommunity.addAll(visited);
                mostSociable = currentCommunity;
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
}