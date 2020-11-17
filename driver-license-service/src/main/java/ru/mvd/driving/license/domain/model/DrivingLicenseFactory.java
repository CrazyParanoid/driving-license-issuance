package ru.mvd.driving.license.domain.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static ru.mvd.driving.license.domain.model.DrivingLicense.DRIVING_LICENSE_VALID_YEAR_PERIOD;

@Component
public class DrivingLicenseFactory {
    private final AttachmentVerificationActionFactory attachmentVerificationActionFactory;
    private final DrivingLicenseRepository drivingLicenseRepository;

    @Autowired
    public DrivingLicenseFactory(AttachmentVerificationActionFactory attachmentVerificationActionFactory,
                                 DrivingLicenseRepository drivingLicenseRepository) {
        this.attachmentVerificationActionFactory = attachmentVerificationActionFactory;
        this.drivingLicenseRepository = drivingLicenseRepository;
    }

    public DrivingLicense newDrivingLicenseFrom(CreateDrivingLicencePayloadObject domainPayloadObject){
        LocalDate endDate = calculateEndDate(domainPayloadObject);
        IssuanceReason issuanceReason = domainPayloadObject.getIssuanceReason();
        DrivingLicenseId drivingLicenseId = drivingLicenseRepository.nextIdentity(domainPayloadObject.getAreaCode());
        DepartmentId departmentId = domainPayloadObject.getDepartmentId();
        PersonId personId = domainPayloadObject.getPersonId();
        Set<Category> categories = domainPayloadObject.getCategories();
        Set<DrivingLicense.SpecialMark> specialMarks = domainPayloadObject.getSpecialMarks();
        List<Attachment> attachments = domainPayloadObject.getAttachments();
        LocalDate startDate = LocalDate.now();
        DrivingLicense drivingLicense = new DrivingLicense(new ArrayList<>(), drivingLicenseId, departmentId,
                personId, startDate, endDate, categories, specialMarks, attachments,
                DrivingLicense.Status.VALID, issuanceReason);
        Function<List<Attachment>, Boolean> verificationAction = attachmentVerificationActionFactory
                .makeVerificationActionForReason(issuanceReason);
        drivingLicense.verifyAttachmentCompleteness(verificationAction);
        drivingLicense.openSubCategories();
        drivingLicense.raiseDomainEvent(new DrivingLicenseCreated(drivingLicenseId, departmentId, personId,
                startDate, endDate, categories, specialMarks));
        return drivingLicense;
    }
    private LocalDate calculateEndDate(CreateDrivingLicencePayloadObject domainPayloadObject){
        IssuanceReason issuanceReason = domainPayloadObject.getIssuanceReason();
        List<Attachment> attachments = domainPayloadObject.getAttachments();
        if(issuanceReason == IssuanceReason.PERSON_NAME_DETAILS_CHANGE){
            if(isMedicalReportExists(attachments)){
                DrivingLicense drivingLicense = drivingLicenseRepository
                        .findByDrivingLicenseId(domainPayloadObject.getPreviousDrivingLicenseId());
                return drivingLicense.getEndDate();
            }
        }
        LocalDate startDate = LocalDate.now();
        return startDate.plusYears(DRIVING_LICENSE_VALID_YEAR_PERIOD);
    }

    private boolean isMedicalReportExists(List<Attachment> attachments){
        return attachments.stream()
                .anyMatch(attachment -> attachment.getAttachmentType() == Attachment.AttachmentType.MEDICAL_REPORT);
    }

}
