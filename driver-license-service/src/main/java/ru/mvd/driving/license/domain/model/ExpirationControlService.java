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

    @Scheduled
    public void checkRevocationExpiration(){
        DrivingLicense drivingLicense = drivingLicenseRepository.findNextRevokedDrivingLicense();
        if(!Objects.isNull(drivingLicense)){
            drivingLicense.disableIfRevocationExpired();
            DrivingLicenseRevocationExpired domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseRevocationExpired.class);
            drivingLicenseRevocationExpiredDomainEventPublisher.publish(domainEvent);
        }
    }

    @Scheduled
    public void checkDrivingLicenseExpiration(){
        DrivingLicense drivingLicense = drivingLicenseRepository.findNextValidDrivingLicense();
        if(!Objects.isNull(drivingLicense)){
            drivingLicense.disableIfExpired();
            DrivingLicenseDisabled domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseDisabled.class);
            drivingLicenseDisabledDomainEventPublisher.publish(domainEvent);
        }
    }

}
