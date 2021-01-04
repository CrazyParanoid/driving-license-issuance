package ru.mvd.driving.license.infrastructure.events.integration;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OutputChannelBindings {
    String DRIVING_LICENSE_ISSUED_CHANNEL = "drivingLicenseIssuedChannel";
    String DRIVING_LICENSE_DISABLED_CHANNEL = "drivingLicenseDisabledChannel";
    String DRIVING_LICENSE_REVOKED_CHANNEL = "drivingLicenseRevokedChannel";
    String DRIVING_LICENSE_REVOCATION_PROLONGED_CHANNEL = "drivingLicenseRevocationProlongedChannel";
    String DRIVING_LICENSE_REVOCATION_EXPIRED_CHANNEL = "drivingLicenseRevocationExpiredChannel";

    @Output(DRIVING_LICENSE_ISSUED_CHANNEL)
    MessageChannel drivingLicenseIssuedChannel();

    @Output(DRIVING_LICENSE_DISABLED_CHANNEL)
    MessageChannel drivingLicenseDisabledChannel();

    @Output(DRIVING_LICENSE_REVOKED_CHANNEL)
    MessageChannel drivingLicenseRevokedChannel();

    @Output(DRIVING_LICENSE_REVOCATION_PROLONGED_CHANNEL)
    MessageChannel drivingLicenseRevocationProlongedChannel();

    @Output(DRIVING_LICENSE_REVOCATION_EXPIRED_CHANNEL)
    MessageChannel drivingLicenseRevocationExpiredChannel();
}
