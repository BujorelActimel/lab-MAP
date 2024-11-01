package domain;

/**
 * Base entity class that all domain objects must extend
 * @param <ID> type of the entity ID
 */
public abstract class Entity<ID> {
    private ID id;
    
    public Entity(ID id) {
        this.id = id;
    }
    
    public ID getId() {
        return id;
    }
    
    public void setId(ID id) {
        this.id = id;
    }
}