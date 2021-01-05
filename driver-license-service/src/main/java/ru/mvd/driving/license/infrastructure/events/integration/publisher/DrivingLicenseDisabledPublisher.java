package ru.mvd.driving.license.infrastructure.events.integration.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.mvd.driving.license.domain.model.DrivingLicenseDisabled;
import ru.mvd.driving.license.infrastructure.events.StoredDomainEventRepository;
import ru.mvd.driving.license.infrastructure.events.integration.DrivingLicenseDisabledIntegrationEvent;
import ru.mvd.driving.license.infrastructure.events.integration.OutputChannelBindings;

@Slf4j
@Component
public class DrivingLicenseDisabledPublisher
        extends AbstractIntegrationEventPublisher<DrivingLicenseDisabled, DrivingLicenseDisabledIntegrationEvent> {

    @Autowired
    protected DrivingLicenseDisabledPublisher(StoredDomainEventRepository storedDomainEventRepository, OutputChannelBindings outputChannelBindings) {
        super(storedDomainEventRepository, outputChannelBindings);
    }

    @Override
    protected void publishIntegrationEvent(DrivingLicenseDisabledIntegrationEvent integrationEvent) {
        outputChannelBindings.drivingLicenseDisabledChannel()
                .send(MessageBuilder
                        .withPayload(integrationEvent)
                        .build());
        log.info("DrivingLicenseDisabledIntegrationEvent has been published");
    }

    @Override
    protected DrivingLicenseDisabledIntegrationEvent convertToIntegrationEvent(DrivingLicenseDisabled domainEvent) {
        return new DrivingLicenseDisabledIntegrationEvent(domainEvent.getDrivingLicenseId());
    }

    @Override
    protected String currentDomainEventType() {
        return DrivingLicenseDisabled.class.getName();
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${scheduler.delay.event}")
    public void findAndPublishDomainEvent() {
        super.findAndPublishDomainEvent();
    }
}
