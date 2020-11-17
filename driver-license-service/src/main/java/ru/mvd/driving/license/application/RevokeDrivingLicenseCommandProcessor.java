package ru.mvd.driving.license.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mvd.driving.license.domain.model.*;

@Service
public class RevokeDrivingLicenseCommandProcessor implements CommandProcessor<RevokeDrivingLicenseCommand>{
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final DomainEventPublisher<DrivingLicenseRevoked> drivingLicenseRevokedDomainEventPublisher;

    @Autowired
    public RevokeDrivingLicenseCommandProcessor(DrivingLicenseRepository drivingLicenseRepository,
                                                DomainEventPublisher<DrivingLicenseRevoked> drivingLicenseRevokedDomainEventPublisher) {
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.drivingLicenseRevokedDomainEventPublisher = drivingLicenseRevokedDomainEventPublisher;
    }

    @Override
    public void process(RevokeDrivingLicenseCommand command) {
        DrivingLicenseId drivingLicenseId = DrivingLicenseId.identifyFrom(command.getDrivingLicenseId());
        DrivingLicense drivingLicense = drivingLicenseRepository.findByDrivingLicenseId(drivingLicenseId);
        drivingLicense.revoke(command.getRevocationEndDate(), command.getJudgmentFileId());
        DrivingLicenseRevoked domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseRevoked.class);
        drivingLicenseRevokedDomainEventPublisher.publish(domainEvent);
    }
}
