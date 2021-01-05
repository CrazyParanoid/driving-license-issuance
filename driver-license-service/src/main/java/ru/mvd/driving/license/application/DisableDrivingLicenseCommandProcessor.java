package ru.mvd.driving.license.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mvd.driving.license.domain.model.DomainEventPublisher;
import ru.mvd.driving.license.domain.model.DrivingLicense;
import ru.mvd.driving.license.domain.model.DrivingLicenseId;
import ru.mvd.driving.license.domain.model.DrivingLicenseRepository;
import ru.mvd.driving.license.domain.supertype.DomainEvent;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DisableDrivingLicenseCommandProcessor implements CommandProcessor<DisableDrivingLicenseCommand, String> {
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Autowired
    public DisableDrivingLicenseCommandProcessor(DrivingLicenseRepository drivingLicenseRepository,
                                                 DomainEventPublisher domainEventPublisher) {
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    @Transactional
    public String process(DisableDrivingLicenseCommand command) {
        DrivingLicenseId drivingLicenseId = DrivingLicenseId.identifyFrom(command.getDrivingLicenseId());
        DrivingLicense drivingLicense = drivingLicenseRepository.findByDrivingLicenseId(drivingLicenseId);
        drivingLicense.disable();
        List<DomainEvent> domainEvents = drivingLicense.getDomainEvents();
        domainEventPublisher.publish(domainEvents);
        drivingLicenseRepository.save(drivingLicense);
        String fullNumber = Objects.requireNonNull(drivingLicenseId).getFullNumber();
        log.info("DrivingLicense with id {} has been disabled", fullNumber);
        return fullNumber;
    }
}
