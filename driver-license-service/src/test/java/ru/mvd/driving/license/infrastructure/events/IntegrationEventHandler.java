package ru.mvd.driving.license.infrastructure.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.mvd.driving.license.infrastructure.events.integration.*;

@Slf4j
@Component
public class IntegrationEventHandler {

    void handle(DrivingLicenseIssuedIntegrationEvent integrationEvent) {
        log.info(integrationEvent.toString());
    }

    void handle(DrivingLicenseDisabledIntegrationEvent integrationEvent) {
        log.info(integrationEvent.toString());
    }

    void handle(DrivingLicenseRevokedIntegrationEvent integrationEvent) {
        log.info(integrationEvent.toString());
    }

    void handle(DrivingLicenseRevocationExpiredIntegrationEvent integrationEvent) {
        log.info(integrationEvent.toString());
    }

    void handle(DrivingLicenseRevocationProlongedIntegrationEvent integrationEvent) {
        log.info(integrationEvent.toString());
    }
}
