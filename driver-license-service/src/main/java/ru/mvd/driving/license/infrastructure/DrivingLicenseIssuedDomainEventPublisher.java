package ru.mvd.driving.license.infrastructure;

import org.springframework.stereotype.Service;
import ru.mvd.driving.license.domain.model.DomainEventPublisher;
import ru.mvd.driving.license.domain.model.DrivingLicenseIssued;

@Service
public class DrivingLicenseIssuedDomainEventPublisher implements DomainEventPublisher<DrivingLicenseIssued> {
    @Override
    public void publish(DrivingLicenseIssued domainEvent) {

    }
}
