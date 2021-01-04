package ru.mvd.driving.license.domain.model;

public interface DrivingLicenseRepository {

    DrivingLicense findByDrivingLicenseId(DrivingLicenseId drivingLicenseId);

    DrivingLicense findByPersonId(PersonId personId);

    DrivingLicense findNextRevokedDrivingLicense();

    DrivingLicense findNextValidDrivingLicense();

    void save(DrivingLicense drivingLicense);

    DrivingLicenseId nextIdentity(AreaCode areaCode);

    void deleteAll();
}
