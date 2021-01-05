package ru.mvd.driving.license.domain.model;

import lombok.AllArgsConstructor;
import ru.mvd.driving.license.domain.supertype.DomainEvent;

@AllArgsConstructor
public class DrivingLicenseDisabled implements DomainEvent {
    private DrivingLicenseId drivingLicenseId;

    public String getDrivingLicenseId(){
        return this.drivingLicenseId.getFullNumber();
    }

}
