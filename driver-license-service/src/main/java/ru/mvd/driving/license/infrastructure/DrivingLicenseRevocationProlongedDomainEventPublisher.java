package ru.mvd.driving.license.infrastructure;

import org.springframework.stereotype.Service;
import ru.mvd.driving.license.domain.model.DomainEventPublisher;
import ru.mvd.driving.license.domain.model.DrivingLicenseRevocationProlonged;

@Service
public class DrivingLicenseRevocationProlongedDomainEventPublisher
        implements DomainEventPublisher<DrivingLicenseRevocationProlonged> {
    @Override
    public void publish(DrivingLicenseRevocationProlonged domainEvent) {

    }
}
