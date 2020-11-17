package ru.mvd.driving.license.infrastructure;

import org.springframework.stereotype.Component;
import ru.mvd.driving.license.domain.model.*;

@Component
public class DrivingLicenseRepositoryImpl implements DrivingLicenseRepository {

    @Override
    public DrivingLicense findByDrivingLicenseId(DrivingLicenseId drivingLicenseId) {
        return null;
    }

    @Override
    public DrivingLicense findByPersonId(PersonId personId) {
        return null;
    }

    @Override
    public DrivingLicense findNextRevokedDrivingLicense() {
        return null;
    }

    @Override
    public DrivingLicense findNextValidDrivingLicense() {
        return null;
    }

    @Override
    public void save(DrivingLicense drivingLicense) {

    }

    @Override
    public DrivingLicenseId nextIdentity(AreaCode areaCode) {
        return new DrivingLicenseId("0102", "123456");
    }
}
