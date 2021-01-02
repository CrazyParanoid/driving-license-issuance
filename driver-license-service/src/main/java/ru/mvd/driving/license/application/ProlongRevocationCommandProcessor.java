package ru.mvd.driving.license.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mvd.driving.license.domain.model.*;

import java.util.Objects;

@Service
public class ProlongRevocationCommandProcessor implements CommandProcessor<ProlongRevocationCommand, String> {
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final DomainEventPublisher<DrivingLicenseRevocationProlonged> drivingLicenseRevocationProlongedDomainEventPublisher;

    @Autowired
    public ProlongRevocationCommandProcessor(DrivingLicenseRepository drivingLicenseRepository,
                                             DomainEventPublisher<DrivingLicenseRevocationProlonged> drivingLicenseRevocationProlongedDomainEventPublisher) {
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.drivingLicenseRevocationProlongedDomainEventPublisher = drivingLicenseRevocationProlongedDomainEventPublisher;
    }

    @Override
    public String process(ProlongRevocationCommand command) {
        DrivingLicenseId drivingLicenseId = DrivingLicenseId.identifyFrom(command.getDrivingLicenseId());
        DrivingLicense drivingLicense = drivingLicenseRepository.findByDrivingLicenseId(drivingLicenseId);
        drivingLicense.prolongRevocation(command.getRevocationEndDate());
        DrivingLicenseRevocationProlonged domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseRevocationProlonged.class);
        drivingLicenseRevocationProlongedDomainEventPublisher.publish(domainEvent);
        drivingLicenseRepository.save(drivingLicense);
        return Objects.requireNonNull(drivingLicenseId).toFullNumber();
    }
}
