package ru.mvd.driving.license.infrastructure;

import org.springframework.stereotype.Service;
import ru.mvd.driving.license.domain.model.DomainEventPublisher;
import ru.mvd.driving.license.domain.model.DrivingLicenseDisabled;

@Service
public class DrivingLicenseDisabledDomainEventPublisher implements DomainEventPublisher<DrivingLicenseDisabled> {
    @Override
    public void publish(DrivingLicenseDisabled domainEvent) {

    }
}
