package ru.mvd.driving.license.infrastructure;

import org.springframework.stereotype.Service;
import ru.mvd.driving.license.domain.model.DomainEventPublisher;
import ru.mvd.driving.license.domain.model.DrivingLicenseRevoked;

@Service
public class DrivingLicenseRevokedDomainEventPublisher implements DomainEventPublisher<DrivingLicenseRevoked> {
    @Override
    public void publish(DrivingLicenseRevoked domainEvent) {

    }
}
