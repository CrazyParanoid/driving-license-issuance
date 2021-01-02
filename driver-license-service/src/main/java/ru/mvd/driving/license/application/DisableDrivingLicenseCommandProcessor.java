package ru.mvd.driving.license.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mvd.driving.license.domain.model.*;

import java.util.Objects;

@Service
public class DisableDrivingLicenseCommandProcessor implements CommandProcessor<DisableDrivingLicenseCommand, String> {
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final DomainEventPublisher<DrivingLicenseDisabled> drivingLicenseDisabledDomainEventPublisher;

    @Autowired
    public DisableDrivingLicenseCommandProcessor(DrivingLicenseRepository drivingLicenseRepository,
                                                 DomainEventPublisher<DrivingLicenseDisabled> drivingLicenseDisabledDomainEventPublisher) {
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.drivingLicenseDisabledDomainEventPublisher = drivingLicenseDisabledDomainEventPublisher;
    }

    @Override
    public String process(DisableDrivingLicenseCommand command) {
        DrivingLicenseId drivingLicenseId = DrivingLicenseId.identifyFrom(command.getDrivingLicenseId());
        DrivingLicense drivingLicense = drivingLicenseRepository.findByDrivingLicenseId(drivingLicenseId);
        drivingLicense.disable();
        DrivingLicenseDisabled domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseDisabled.class);
        drivingLicenseDisabledDomainEventPublisher.publish(domainEvent);
        drivingLicenseRepository.save(drivingLicense);
        return Objects.requireNonNull(drivingLicenseId).toFullNumber();
    }
}
