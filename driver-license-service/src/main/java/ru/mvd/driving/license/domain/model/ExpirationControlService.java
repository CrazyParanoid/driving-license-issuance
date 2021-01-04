package ru.mvd.driving.license.domain.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.mvd.driving.license.domain.supertype.DomainEvent;
import ru.mvd.driving.license.domain.supertype.DomainService;

import java.util.List;
import java.util.Objects;

@Service
public class ExpirationControlService implements DomainService {
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Autowired
    public ExpirationControlService(DrivingLicenseRepository drivingLicenseRepository,
                                    DomainEventPublisher domainEventPublisher) {
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Scheduled(fixedDelayString = "${scheduler.delay.revocation}")
    public void checkRevocationExpiration() {
        DrivingLicense drivingLicense = drivingLicenseRepository.findNextRevokedDrivingLicense();
        if (!Objects.isNull(drivingLicense)) {
            drivingLicense.disableIfRevocationExpired();
            List<DomainEvent> domainEvents = drivingLicense.getDomainEvents();
            domainEventPublisher.publish(domainEvents);
            drivingLicenseRepository.save(drivingLicense);
        }
    }

    @Scheduled(fixedDelayString = "${scheduler.delay.driving-license}")
    public void checkDrivingLicenseExpiration() {
        DrivingLicense drivingLicense = drivingLicenseRepository.findNextValidDrivingLicense();
        if (!Objects.isNull(drivingLicense)) {
            drivingLicense.disableIfExpired();
            List<DomainEvent> domainEvents = drivingLicense.getDomainEvents();
            domainEventPublisher.publish(domainEvents);
            drivingLicenseRepository.save(drivingLicense);
        }
    }

}
