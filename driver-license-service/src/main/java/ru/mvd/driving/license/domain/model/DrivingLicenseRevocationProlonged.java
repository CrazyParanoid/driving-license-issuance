package ru.mvd.driving.license.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.mvd.driving.license.domain.supertype.DomainEvent;

import java.time.LocalDate;

@AllArgsConstructor
public class DrivingLicenseRevocationProlonged implements DomainEvent {
    private DrivingLicenseId drivingLicenseId;
    private RevocationId revocationId;
    @Getter
    private LocalDate revocationEndDate;

    public String getDrivingLicenseId(){
        return this.drivingLicenseId.toFullNumber();
    }

    public String getRevocationId(){
        return this.revocationId.getId();
    }

}
