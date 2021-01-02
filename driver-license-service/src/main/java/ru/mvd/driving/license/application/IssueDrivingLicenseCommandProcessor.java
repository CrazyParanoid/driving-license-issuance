package ru.mvd.driving.license.application;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mvd.driving.license.domain.model.*;

import java.util.*;

@Service
public class IssueDrivingLicenseCommandProcessor implements CommandProcessor<IssueDrivingLicenseCommand, String> {
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final DomainEventPublisher<DrivingLicenseIssued> drivingLicenseIssuedDomainEventPublisher;
    private final DrivingLicenseFactory drivingLicenseFactory;

    @Autowired
    public IssueDrivingLicenseCommandProcessor(DrivingLicenseRepository drivingLicenseRepository,
                                               DomainEventPublisher<DrivingLicenseIssued> drivingLicenseIssuedDomainEventPublisher,
                                               DrivingLicenseFactory drivingLicenseFactory) {
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.drivingLicenseIssuedDomainEventPublisher = drivingLicenseIssuedDomainEventPublisher;
        this.drivingLicenseFactory = drivingLicenseFactory;
    }

    @Override
    public String process(IssueDrivingLicenseCommand command) {
        deduplicate(command.getPersonId());
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
        DrivingLicenseIssued domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseIssued.class);
        drivingLicenseIssuedDomainEventPublisher.publish(domainEvent);
        drivingLicenseRepository.save(drivingLicense);
        DrivingLicenseId drivingLicenseId = drivingLicense.getDrivingLicenseId();
        return drivingLicenseId.toFullNumber();
    }

    private void deduplicate(String aPersonId) {
        PersonId personId = new PersonId(aPersonId);
        DrivingLicense drivingLicense = drivingLicenseRepository.findByPersonId(personId);
        if (!Objects.isNull(drivingLicense))
            throw new UnsupportedOperationException(
                    String.format("The person with id %s already has driving license", aPersonId));
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
