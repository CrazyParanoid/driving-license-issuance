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

@Slf4j
@Service
public class RevokeDrivingLicenseCommandProcessor implements CommandProcessor<RevokeDrivingLicenseCommand, String> {
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Autowired
    public RevokeDrivingLicenseCommandProcessor(DrivingLicenseRepository drivingLicenseRepository,
                                                DomainEventPublisher domainEventPublisher) {
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    @Transactional
    public String process(RevokeDrivingLicenseCommand command) {
        DrivingLicenseId drivingLicenseId = DrivingLicenseId.identifyFrom(command.getDrivingLicenseId());
        DrivingLicense drivingLicense = drivingLicenseRepository.findByDrivingLicenseId(drivingLicenseId);
        drivingLicense.revoke(command.getRevocationEndDate(), command.getJudgmentFileId());
        List<DomainEvent> domainEvents = drivingLicense.getDomainEvents();
        domainEventPublisher.publish(domainEvents);
        drivingLicenseRepository.save(drivingLicense);
        String fullNumber = drivingLicense.getFullNumber();
        log.info("DrivingLicense with id {} has been revoked", fullNumber);
        return fullNumber;
    }
}
