package ru.mvd.driving.license.application;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mvd.driving.license.domain.model.*;
import ru.mvd.driving.license.domain.supertype.DomainEvent;

import java.util.*;

@Slf4j
@Service
public class IssueDrivingLicenseCommandProcessor implements CommandProcessor<IssueDrivingLicenseCommand, String> {
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final DrivingLicenseFactory drivingLicenseFactory;
    private final DomainEventPublisher domainEventPublisher;

    @Autowired
    public IssueDrivingLicenseCommandProcessor(DrivingLicenseRepository drivingLicenseRepository,
                                               DrivingLicenseFactory drivingLicenseFactory,
                                               DomainEventPublisher domainEventPublisher) {
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.drivingLicenseFactory = drivingLicenseFactory;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    @Transactional
    public String process(IssueDrivingLicenseCommand command) {
        Set<Category> categories = categoriesFrom(command.getCategories());
        List<Attachment> attachments = attachmentsFrom(command.getAttachments());
        DrivingLicense drivingLicense = drivingLicenseFactory.issueDrivingLicense(
                IssuanceReason.fromName(command.getIssuanceReason()),
                new PersonId(command.getPersonId()),
                categories,
                attachments,
                new DepartmentId(command.getDepartmentId()),
                new AreaCode(command.getAreaCode()),
                DrivingLicenseId.identifyFrom(command.getPreviousDrivingLicenseId()),
                DrivingLicense.SpecialMark.setFrom(command.getSpecialMarks()));

        List<DomainEvent> domainEvents = drivingLicense.getDomainEvents();
        domainEventPublisher.publish(domainEvents);
        drivingLicenseRepository.save(drivingLicense);

        String fullNumber = drivingLicense.getFullNumber();
        log.info("DrivingLicense with id {} has been issued", fullNumber);
        return fullNumber;
    }

    private Set<Category> categoriesFrom(Set<IssueDrivingLicenseCommand.CategoryDTO> rawCategories) {
        Set<Category> categories = new HashSet<>();
        if (CollectionUtils.isNotEmpty(rawCategories)) {
            for (IssueDrivingLicenseCommand.CategoryDTO rawCategory : rawCategories) {
                Category category = Category.open(rawCategory.getCategoryType(),
                        rawCategory.getStartDate(),
                        rawCategory.getEndDate(),
                        rawCategory.getSpecialMarks());
                categories.add(category);
            }
        }
        return categories;
    }

    private List<Attachment> attachmentsFrom(Set<IssueDrivingLicenseCommand.AttachmentDTO> rawAttachments) {
        List<Attachment> attachments = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(rawAttachments)) {
            for (IssueDrivingLicenseCommand.AttachmentDTO rawAttachment : rawAttachments) {
                Attachment attachment = Attachment.newAttachment(rawAttachment.getAttachmentType(),
                        rawAttachment.getFileId());
                attachments.add(attachment);
            }
        }
        return attachments;
    }

}
