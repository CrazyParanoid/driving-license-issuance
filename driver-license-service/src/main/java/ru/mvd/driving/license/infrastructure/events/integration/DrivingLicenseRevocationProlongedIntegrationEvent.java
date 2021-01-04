package ru.mvd.driving.license.infrastructure.events.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLicenseRevocationProlongedIntegrationEvent implements IntegrationEvent{
    private String drivingLicenseId;
    private String revocationId;
    private LocalDateTime revocationEndDate;
}
