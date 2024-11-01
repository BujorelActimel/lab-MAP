package domain;

import java.util.Objects;

/**
 * Represents a friendship between two users
 */
public class Friendship {
    private final String id;
    private final User user1;
    private final User user2;

    public Friendship(String id, User user1, User user2) {
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
    }

    public String getId() { return id; }
    public User getUser1() { return user1; }
    public User getUser2() { return user2; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
