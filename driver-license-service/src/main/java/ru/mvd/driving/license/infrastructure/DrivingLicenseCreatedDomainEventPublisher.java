package ru.mvd.driving.license.infrastructure;

import org.springframework.stereotype.Service;
import ru.mvd.driving.license.domain.model.DomainEventPublisher;
import ru.mvd.driving.license.domain.model.DrivingLicenseCreated;

@Service
public class DrivingLicenseCreatedDomainEventPublisher implements DomainEventPublisher<DrivingLicenseCreated> {
    @Override
    public void publish(DrivingLicenseCreated domainEvent) {

    }
}
