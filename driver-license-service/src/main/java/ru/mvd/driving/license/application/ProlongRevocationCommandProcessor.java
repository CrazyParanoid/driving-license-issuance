package ru.mvd.driving.license.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mvd.driving.license.domain.model.*;
import ru.mvd.driving.license.domain.supertype.DomainEvent;

import java.util.List;
import java.util.Objects;

@Service
public class ProlongRevocationCommandProcessor implements CommandProcessor<ProlongRevocationCommand, String> {
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Autowired
    public ProlongRevocationCommandProcessor(DrivingLicenseRepository drivingLicenseRepository,
                                             DomainEventPublisher domainEventPublisher) {
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    @Transactional
    public String process(ProlongRevocationCommand command) {
        DrivingLicenseId drivingLicenseId = DrivingLicenseId.identifyFrom(command.getDrivingLicenseId());
        DrivingLicense drivingLicense = drivingLicenseRepository.findByDrivingLicenseId(drivingLicenseId);
        drivingLicense.prolongRevocation(command.getRevocationEndDate());
        List<DomainEvent> domainEvents = drivingLicense.getDomainEvents();
        domainEventPublisher.publish(domainEvents);
        drivingLicenseRepository.save(drivingLicense);
        return Objects.requireNonNull(drivingLicenseId).toFullNumber();
    }
}
