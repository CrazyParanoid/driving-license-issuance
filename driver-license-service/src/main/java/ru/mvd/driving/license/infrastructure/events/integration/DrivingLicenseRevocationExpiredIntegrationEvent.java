package ru.mvd.driving.license.infrastructure.events.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLicenseRevocationExpiredIntegrationEvent implements IntegrationEvent{
    private String drivingLicenseId;
    private String revocationId;
}
