package ru.mvd.driving.license.application;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mvd.driving.license.domain.model.*;

import java.util.*;

@Service
public class CreateDrivingLicenseCommandProcessor implements CommandProcessor<CreateDrivingLicenseCommand>{
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final DomainEventPublisher<DrivingLicenseCreated> drivingLicenseCreatedDomainEventPublisher;
    private final DrivingLicenseFactory drivingLicenseFactory;

    @Autowired
    public CreateDrivingLicenseCommandProcessor(DrivingLicenseRepository drivingLicenseRepository,
                                                DomainEventPublisher<DrivingLicenseCreated> drivingLicenseCreatedDomainEventPublisher,
                                                DrivingLicenseFactory drivingLicenseFactory) {
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.drivingLicenseCreatedDomainEventPublisher = drivingLicenseCreatedDomainEventPublisher;
        this.drivingLicenseFactory = drivingLicenseFactory;
    }

    @Override
    @Transactional
    public void process(CreateDrivingLicenseCommand command) {
        deduplicate(command.getPersonId());
        Set<Category> categories = categoriesFrom(command.getCategories());
        List<Attachment> attachments = attachmentsFrom(command.getAttachments());
        CreateDrivingLicencePayloadObject domainPayloadObject = CreateDrivingLicencePayloadObject.newCreateDrivingLicencePayloadObject()
                .withArea(command.getAreaCode())
                .withAttachments(attachments)
                .withCategories(categories)
                .withDepartment(command.getDepartmentId())
                .withPerson(command.getPersonId())
                .withIssuanceReason(command.getIssuanceReason())
                .withSpecialMarks(command.getSpecialMarks())
                .build();
        DrivingLicense drivingLicense = drivingLicenseFactory.newDrivingLicenseFrom(domainPayloadObject);
        DrivingLicenseCreated domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseCreated.class);
        drivingLicenseCreatedDomainEventPublisher.publish(domainEvent);
        drivingLicenseRepository.save(drivingLicense);
    }

    private void deduplicate(String aPersonId){
        PersonId personId = new PersonId(aPersonId);
        DrivingLicense drivingLicense = drivingLicenseRepository.findByPersonId(personId);
        if(!Objects.isNull(drivingLicense))
            throw new UnsupportedOperationException(String.format("The person with id %s already has driving license", aPersonId));
    }

    private Set<Category> categoriesFrom(Set<CreateDrivingLicenseCommand.CategoryDTO> rawCategories){
        Set<Category> categories = new HashSet<>();
        if(CollectionUtils.isNotEmpty(rawCategories)){
            for(CreateDrivingLicenseCommand.CategoryDTO rawCategory: rawCategories){
                Category category = Category.open(rawCategory.getCategoryType(),
                        rawCategory.getStartDate(), rawCategory.getEndDate(), rawCategory.getSpecialMarks());
                categories.add(category);
            }
        }
        return categories;
    }

    private List<Attachment> attachmentsFrom(Set<CreateDrivingLicenseCommand.AttachmentDTO> rawAttachments){
        List<Attachment> attachments = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(rawAttachments)){
            for(CreateDrivingLicenseCommand.AttachmentDTO rawAttachment: rawAttachments){
                Attachment attachment = Attachment.newAttachment(rawAttachment.getAttachmentType(),
                        rawAttachment.getFileId());
                attachments.add(attachment);
            }
        }
        return attachments;
    }

}
