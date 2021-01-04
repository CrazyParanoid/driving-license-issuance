package ru.mvd.driving.license.infrastructure.persistence;

public class RepositoryAccessException extends RuntimeException{
    public RepositoryAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
