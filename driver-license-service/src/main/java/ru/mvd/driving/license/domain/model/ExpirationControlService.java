package ru.mvd.driving.license.domain.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ExpirationControlService {
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final DomainEventPublisher<DrivingLicenseRevocationExpired> drivingLicenseRevocationExpiredDomainEventPublisher;
    private final DomainEventPublisher<DrivingLicenseDisabled> drivingLicenseDisabledDomainEventPublisher;

    @Autowired
    public ExpirationControlService(DrivingLicenseRepository drivingLicenseRepository,
                                    DomainEventPublisher<DrivingLicenseRevocationExpired> drivingLicenseRevocationExpiredDomainEventPublisher,
                                    DomainEventPublisher<DrivingLicenseDisabled> drivingLicenseDisabledDomainEventPublisher) {
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.drivingLicenseRevocationExpiredDomainEventPublisher = drivingLicenseRevocationExpiredDomainEventPublisher;
        this.drivingLicenseDisabledDomainEventPublisher = drivingLicenseDisabledDomainEventPublisher;
    }

    @Scheduled(fixedDelayString = "${delay.revocation}")
    public void checkRevocationExpiration() {
        DrivingLicense drivingLicense = drivingLicenseRepository.findNextRevokedDrivingLicense();
        if (!Objects.isNull(drivingLicense)) {
            drivingLicense.disableIfRevocationExpired();
            DrivingLicenseRevocationExpired drivingLicenseRevocationExpiredDomainEvent = drivingLicense
                    .getDomainEventByType(DrivingLicenseRevocationExpired.class);
            DrivingLicenseDisabled drivingLicenseDisabledDomainEvent = drivingLicense
                    .getDomainEventByType(DrivingLicenseDisabled.class);
            drivingLicenseDisabledDomainEventPublisher.publish(drivingLicenseDisabledDomainEvent);
            drivingLicenseRevocationExpiredDomainEventPublisher.publish(drivingLicenseRevocationExpiredDomainEvent);
            drivingLicenseRepository.save(drivingLicense);
        }
    }

    @Scheduled(fixedDelayString = "${delay.driving-license}")
    public void checkDrivingLicenseExpiration() {
        DrivingLicense drivingLicense = drivingLicenseRepository.findNextValidDrivingLicense();
        if (!Objects.isNull(drivingLicense)) {
            drivingLicense.disableIfExpired();
            DrivingLicenseDisabled domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseDisabled.class);
            drivingLicenseDisabledDomainEventPublisher.publish(domainEvent);
            drivingLicenseRepository.save(drivingLicense);
        }
    }

}
