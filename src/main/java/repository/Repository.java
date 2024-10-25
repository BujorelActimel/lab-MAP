package repository;

import exceptions.ValidationException;
import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface
 */
public interface Repository<ID, E> {
    void add(E entity) throws ValidationException;
    void remove(ID id);
    Optional<E> findById(ID id);
    List<E> findAll();
}