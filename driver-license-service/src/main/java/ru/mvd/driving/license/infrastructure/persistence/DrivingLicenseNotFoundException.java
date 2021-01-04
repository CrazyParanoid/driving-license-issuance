package ru.mvd.driving.license.infrastructure.persistence;

public class DrivingLicenseNotFoundException extends RuntimeException{
    public DrivingLicenseNotFoundException(String message) {
        super(message);
    }
}
