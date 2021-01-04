package ru.mvd.driving.license.infrastructure.events.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLicenseDisabledIntegrationEvent implements IntegrationEvent{
    private String drivingLicenseId;
}
