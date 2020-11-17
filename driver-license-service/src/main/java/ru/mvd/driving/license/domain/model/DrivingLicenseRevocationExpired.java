package ru.mvd.driving.license.domain.model;

import lombok.AllArgsConstructor;
import ru.mvd.driving.license.domain.supertype.DomainEvent;

@AllArgsConstructor
public class DrivingLicenseRevocationExpired implements DomainEvent {
    private DrivingLicenseId drivingLicenseId;
    private RevocationId revocationId;

    public String getDrivingLicenseId(){
        return this.drivingLicenseId.toFullNumber();
    }

    public String getRevocationId(){
        return this.revocationId.getId();
    }

}
