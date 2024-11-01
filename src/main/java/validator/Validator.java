package validator;

import exceptions.ValidationException;

/**
 * Generic validator interface
 */
public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}