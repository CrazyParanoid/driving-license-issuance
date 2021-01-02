package ru.mvd.driving.license.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mvd.driving.license.domain.model.*;

import java.util.Objects;

@Service
public class RevokeDrivingLicenseCommandProcessor implements CommandProcessor<RevokeDrivingLicenseCommand, String> {
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final DomainEventPublisher<DrivingLicenseRevoked> drivingLicenseRevokedDomainEventPublisher;

    @Autowired
    public RevokeDrivingLicenseCommandProcessor(DrivingLicenseRepository drivingLicenseRepository,
                                                DomainEventPublisher<DrivingLicenseRevoked> drivingLicenseRevokedDomainEventPublisher) {
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.drivingLicenseRevokedDomainEventPublisher = drivingLicenseRevokedDomainEventPublisher;
    }

    @Override
    public String process(RevokeDrivingLicenseCommand command) {
        DrivingLicenseId drivingLicenseId = DrivingLicenseId.identifyFrom(command.getDrivingLicenseId());
        DrivingLicense drivingLicense = drivingLicenseRepository.findByDrivingLicenseId(drivingLicenseId);
        drivingLicense.revoke(command.getRevocationEndDate(), command.getJudgmentFileId());
        DrivingLicenseRevoked domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseRevoked.class);
        drivingLicenseRevokedDomainEventPublisher.publish(domainEvent);
        drivingLicenseRepository.save(drivingLicense);
        return Objects.requireNonNull(drivingLicenseId).toFullNumber();
    }
}
