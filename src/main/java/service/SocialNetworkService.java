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
        List<User> allUsers = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        
        // Map to store communities
        Map<User, Set<User>> communities = new HashMap<>();
        Set<User> visited = new HashSet<>();

        // Find all communities
        for (User user : allUsers) {
            if (!visited.contains(user)) {
                Set<User> community = new HashSet<>();
                dfs(user, community);
                visited.addAll(community);
                communities.put(user, community);
            }
        }

        // Find the most sociable community
        int maxSocialScore = -1;
        Set<User> mostSociableCommunity = new HashSet<>();

        for (Map.Entry<User, Set<User>> entry : communities.entrySet()) {
            Set<User> community = entry.getValue();
            if (community.size() < 2) continue; // Skip isolated users

            // Calculate social score based on number of friendships within community
            int socialScore = calculateCommunityScore(community);
            
            if (socialScore > maxSocialScore) {
                maxSocialScore = socialScore;
                mostSociableCommunity = community;
            }
        }

        return new ArrayList<>(mostSociableCommunity);
    }

    private int calculateCommunityScore(Set<User> community) {
        int totalConnections = 0;
        
        for (User user : community) {
            for (User friend : user.getFriends()) {
                if (community.contains(friend)) {
                    totalConnections++;
                }
            }
        }
        
        // Each connection is counted twice (once for each user), so divide by 2
        return totalConnections / 2;
    }

    private void dfs(User user, Set<User> community) {
        community.add(user);
        for (User friend : user.getFriends()) {
            if (!community.contains(friend)) {
                dfs(friend, community);
            }
        }
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}