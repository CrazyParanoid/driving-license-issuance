package ru.mvd.driving.license.infrastructure.events.integration.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.mvd.driving.license.domain.model.DrivingLicenseRevocationProlonged;
import ru.mvd.driving.license.infrastructure.events.StoredDomainEventRepository;
import ru.mvd.driving.license.infrastructure.events.integration.DrivingLicenseRevocationProlongedIntegrationEvent;
import ru.mvd.driving.license.infrastructure.events.integration.OutputChannelBindings;

@Component
public class DrivingLicenseRevocationProlongedPublisher
        extends AbstractIntegrationEventPublisher<DrivingLicenseRevocationProlonged, DrivingLicenseRevocationProlongedIntegrationEvent> {

    @Autowired
    protected DrivingLicenseRevocationProlongedPublisher(StoredDomainEventRepository storedDomainEventRepository, OutputChannelBindings outputChannelBindings) {
        super(storedDomainEventRepository, outputChannelBindings);
    }

    @Override
    protected void publishIntegrationEvent(DrivingLicenseRevocationProlongedIntegrationEvent integrationEvent) {
        outputChannelBindings.drivingLicenseRevocationProlongedChannel()
                .send(MessageBuilder
                        .withPayload(integrationEvent)
                        .build());
    }

    @Override
    protected DrivingLicenseRevocationProlongedIntegrationEvent convertToIntegrationEvent(DrivingLicenseRevocationProlonged domainEvent) {
        return new DrivingLicenseRevocationProlongedIntegrationEvent(
                domainEvent.getDrivingLicenseId(),
                domainEvent.getRevocationId(),
                domainEvent.getRevocationEndDate()
        );
    }

    @Override
    protected String currentDomainEventType() {
        return DrivingLicenseRevocationProlonged.class.getName();
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${scheduler.delay.event}")
    public void findAndPublishDomainEvent() {
        super.findAndPublishDomainEvent();
    }
}
