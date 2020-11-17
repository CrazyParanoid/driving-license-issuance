package ru.mvd.driving.license.infrastructure;

import org.springframework.stereotype.Service;
import ru.mvd.driving.license.domain.model.DomainEventPublisher;
import ru.mvd.driving.license.domain.model.DrivingLicenseRevocationExpired;

@Service
public class DrivingLicenseRevocationExpiredDomainEventPublisher
        implements DomainEventPublisher<DrivingLicenseRevocationExpired> {
    @Override
    public void publish(DrivingLicenseRevocationExpired domainEvent) {

    }
}
