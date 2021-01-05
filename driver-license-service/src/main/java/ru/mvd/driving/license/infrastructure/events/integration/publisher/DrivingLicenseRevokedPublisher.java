package ru.mvd.driving.license.infrastructure.events.integration.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.mvd.driving.license.domain.model.DrivingLicenseRevoked;
import ru.mvd.driving.license.infrastructure.events.StoredDomainEventRepository;
import ru.mvd.driving.license.infrastructure.events.integration.DrivingLicenseRevokedIntegrationEvent;
import ru.mvd.driving.license.infrastructure.events.integration.OutputChannelBindings;

@Slf4j
@Component
public class DrivingLicenseRevokedPublisher
        extends AbstractIntegrationEventPublisher<DrivingLicenseRevoked, DrivingLicenseRevokedIntegrationEvent> {

    @Autowired
    protected DrivingLicenseRevokedPublisher(StoredDomainEventRepository storedDomainEventRepository, OutputChannelBindings outputChannelBindings) {
        super(storedDomainEventRepository, outputChannelBindings);
    }

    @Override
    protected void publishIntegrationEvent(DrivingLicenseRevokedIntegrationEvent integrationEvent) {
        outputChannelBindings.drivingLicenseRevokedChannel()
                .send(MessageBuilder
                        .withPayload(integrationEvent)
                        .build());
        log.info("DrivingLicenseRevokedIntegrationEvent has been published");
    }

    @Override
    protected DrivingLicenseRevokedIntegrationEvent convertToIntegrationEvent(DrivingLicenseRevoked domainEvent) {
        return new DrivingLicenseRevokedIntegrationEvent(
                domainEvent.getDrivingLicenseId(),
                domainEvent.getRevocationId(),
                domainEvent.getRevocationStartDate(),
                domainEvent.getRevocationEndDate()
        );
    }

    @Override
    protected String currentDomainEventType() {
        return DrivingLicenseRevoked.class.getName();
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${scheduler.delay.event}")
    public void findAndPublishDomainEvent() {
        super.findAndPublishDomainEvent();
    }
}
