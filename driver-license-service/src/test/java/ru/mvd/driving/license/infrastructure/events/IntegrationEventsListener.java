package ru.mvd.driving.license.infrastructure.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import ru.mvd.driving.license.infrastructure.events.integration.*;

@Slf4j
@Component
public class IntegrationEventsListener {
    private final IntegrationEventHandler integrationEventHandler;

    public IntegrationEventsListener(IntegrationEventHandler integrationEventHandler) {
        this.integrationEventHandler = integrationEventHandler;
    }

    @StreamListener(OutputChannelBindings.DRIVING_LICENSE_ISSUED_CHANNEL)
    public void onEvent(DrivingLicenseIssuedIntegrationEvent integrationEvent) {
        integrationEventHandler.handle(integrationEvent);
    }

    @StreamListener(OutputChannelBindings.DRIVING_LICENSE_DISABLED_CHANNEL)
    public void onEvent(DrivingLicenseDisabledIntegrationEvent integrationEvent) {
        integrationEventHandler.handle(integrationEvent);
    }

    @StreamListener(OutputChannelBindings.DRIVING_LICENSE_REVOCATION_EXPIRED_CHANNEL)
    public void onEvent(DrivingLicenseRevocationExpiredIntegrationEvent integrationEvent) {
        integrationEventHandler.handle(integrationEvent);
    }

    @StreamListener(OutputChannelBindings.DRIVING_LICENSE_REVOKED_CHANNEL)
    public void onEvent(DrivingLicenseRevokedIntegrationEvent integrationEvent) {
        integrationEventHandler.handle(integrationEvent);
    }

    @StreamListener(OutputChannelBindings.DRIVING_LICENSE_REVOCATION_PROLONGED_CHANNEL)
    public void onEvent(DrivingLicenseRevocationProlongedIntegrationEvent integrationEvent) {
        integrationEventHandler.handle(integrationEvent);
    }
}
