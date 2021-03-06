package ru.mvd.driving.license.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.mvd.driving.license.domain.supertype.DomainEvent;

import java.time.LocalDateTime;

@AllArgsConstructor
public class DrivingLicenseRevoked implements DomainEvent {
    private DrivingLicenseId drivingLicenseId;
    private RevocationId revocationId;
    @Getter
    private LocalDateTime revocationStartDate;
    @Getter
    private LocalDateTime revocationEndDate;

    public String getDrivingLicenseId(){
        return this.drivingLicenseId.getFullNumber();
    }

    public String getRevocationId(){
        return this.revocationId.getId();
    }

}
