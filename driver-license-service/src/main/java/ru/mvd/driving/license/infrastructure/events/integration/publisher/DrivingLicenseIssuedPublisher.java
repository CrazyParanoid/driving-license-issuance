package ru.mvd.driving.license.infrastructure.events.integration.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.mvd.driving.license.domain.model.Attachment;
import ru.mvd.driving.license.domain.model.Category;
import ru.mvd.driving.license.domain.model.DrivingLicenseIssued;
import ru.mvd.driving.license.infrastructure.events.StoredDomainEventRepository;
import ru.mvd.driving.license.infrastructure.events.integration.DrivingLicenseIssuedIntegrationEvent;
import ru.mvd.driving.license.infrastructure.events.integration.OutputChannelBindings;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DrivingLicenseIssuedPublisher
        extends AbstractIntegrationEventPublisher<DrivingLicenseIssued, DrivingLicenseIssuedIntegrationEvent> {

    @Autowired
    protected DrivingLicenseIssuedPublisher(StoredDomainEventRepository storedDomainEventRepository,
                                            OutputChannelBindings outputChannelBindings) {
        super(storedDomainEventRepository, outputChannelBindings);
    }

    @Override
    protected void publishIntegrationEvent(DrivingLicenseIssuedIntegrationEvent integrationEvent) {
        outputChannelBindings.drivingLicenseIssuedChannel()
                .send(MessageBuilder
                        .withPayload(integrationEvent)
                        .build());
        log.info("DrivingLicenseIssuedIntegrationEvent has been published");
    }

    @Override
    protected DrivingLicenseIssuedIntegrationEvent convertToIntegrationEvent(DrivingLicenseIssued domainEvent) {
        Set<DrivingLicenseIssuedIntegrationEvent.Category> categories = convertCategories(domainEvent.getCategories());
        return new DrivingLicenseIssuedIntegrationEvent(
                domainEvent.getDrivingLicenseId(),
                domainEvent.getDepartmentId(),
                domainEvent.getPersonId(),
                domainEvent.getStartDate(),
                domainEvent.getEndDate(),
                categories,
                domainEvent.getSpecialMarks(),
                convertAttachments(domainEvent.getAttachments()),
                domainEvent.getIssuanceReason()
        );
    }

    private Set<DrivingLicenseIssuedIntegrationEvent.Category> convertCategories(Set<Category> categories) {
        return categories.stream()
                .map(category -> new DrivingLicenseIssuedIntegrationEvent.Category(
                        category.getStartDate(),
                        category.getEndDate(),
                        category.typeToString(),
                        category.specialMarksToStrings()
                ))
                .collect(Collectors.toSet());
    }

    private List<DrivingLicenseIssuedIntegrationEvent.Attachment> convertAttachments(List<Attachment> attachments) {
        return attachments.stream()
                .map(attachment -> new DrivingLicenseIssuedIntegrationEvent.Attachment(
                        attachment.attachmentTypeToString(),
                        attachment.getFileId()
                ))
                .collect(Collectors.toList());
    }

    @Override
    protected String currentDomainEventType() {
        return DrivingLicenseIssued.class.getName();
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${scheduler.delay.event}")
    public void findAndPublishDomainEvent() {
        super.findAndPublishDomainEvent();
    }
}
