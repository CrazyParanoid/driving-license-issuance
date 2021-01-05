package ru.mvd.driving.license.infrastructure.events.integration.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.mvd.driving.license.domain.model.DrivingLicenseRevocationExpired;
import ru.mvd.driving.license.infrastructure.events.StoredDomainEventRepository;
import ru.mvd.driving.license.infrastructure.events.integration.DrivingLicenseRevocationExpiredIntegrationEvent;
import ru.mvd.driving.license.infrastructure.events.integration.OutputChannelBindings;

@Slf4j
@Component
public class DrivingLicenseRevocationExpiredPublisher
        extends AbstractIntegrationEventPublisher<DrivingLicenseRevocationExpired, DrivingLicenseRevocationExpiredIntegrationEvent> {

    @Autowired
    protected DrivingLicenseRevocationExpiredPublisher(StoredDomainEventRepository storedDomainEventRepository, OutputChannelBindings outputChannelBindings) {
        super(storedDomainEventRepository, outputChannelBindings);
    }

    @Override
    protected void publishIntegrationEvent(DrivingLicenseRevocationExpiredIntegrationEvent integrationEvent) {
        outputChannelBindings.drivingLicenseRevocationExpiredChannel()
                .send(MessageBuilder
                        .withPayload(integrationEvent)
                        .build());
        log.info("DrivingLicenseRevocationExpiredIntegrationEvent has been published");
    }

    @Override
    protected DrivingLicenseRevocationExpiredIntegrationEvent convertToIntegrationEvent(DrivingLicenseRevocationExpired domainEvent) {
        return new DrivingLicenseRevocationExpiredIntegrationEvent(
                domainEvent.getDrivingLicenseId(),
                domainEvent.getRevocationId()
        );
    }

    @Override
    protected String currentDomainEventType() {
        return DrivingLicenseRevocationExpired.class.getName();
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${scheduler.delay.event}")
    public void findAndPublishDomainEvent() {
        super.findAndPublishDomainEvent();
    }
}
