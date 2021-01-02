package ru.mvd.driving.license.domain.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public DrivingLicense issueDrivingLicense(IssuanceReason issuanceReason, PersonId personId,
                                              Set<Category> categories, List<Attachment> attachments,
                                              DepartmentId departmentId, AreaCode areaCode,
                                              DrivingLicenseId previousDrivingLicenseId, Set<DrivingLicense.SpecialMark> specialMarks) {
        LocalDate endDate = calculateEndDate(issuanceReason, attachments, previousDrivingLicenseId);
        DrivingLicenseId drivingLicenseId = identifyDrivingLicense(previousDrivingLicenseId, areaCode);
        LocalDate startDate = LocalDate.now();
        DrivingLicense drivingLicense = new DrivingLicense(new ArrayList<>(), drivingLicenseId, departmentId,
                personId, startDate, endDate, categories, specialMarks, attachments,
                DrivingLicense.Status.VALID, issuanceReason);
        Function<List<Attachment>, Boolean> verificationAction = attachmentVerificationActionFactory
                .makeVerificationActionForReason(issuanceReason);
        drivingLicense.verifyAttachmentCompleteness(verificationAction);
        drivingLicense.openSubCategories();
        drivingLicense.raiseDomainEvent(new DrivingLicenseIssued(drivingLicenseId,
                departmentId,
                personId,
                startDate,
                endDate,
                categories,
                specialMarks));
        return drivingLicense;
    }

    private DrivingLicenseId identifyDrivingLicense(DrivingLicenseId previousDrivingLicenseId, AreaCode areaCode) {
        if (Objects.isNull(previousDrivingLicenseId))
            return drivingLicenseRepository.nextIdentity(areaCode);
        return previousDrivingLicenseId;
    }

    private LocalDate calculateEndDate(IssuanceReason issuanceReason, List<Attachment> attachments,
                                       DrivingLicenseId previousDrivingLicenseId) {
        if (issuanceReason == IssuanceReason.PERSON_NAME_DETAILS_CHANGE) {
            if (isMedicalReportExists(attachments)) {
                if (Objects.isNull(previousDrivingLicenseId))
                    throw new IllegalArgumentException("PreviousDrivingLicenseId can't be null");
                DrivingLicense drivingLicense = drivingLicenseRepository.findByDrivingLicenseId(previousDrivingLicenseId);
                return drivingLicense.getEndDate();
            }
        }
        LocalDate startDate = LocalDate.now();
        return startDate.plusYears(DRIVING_LICENSE_VALID_YEAR_PERIOD);
    }

    private boolean isMedicalReportExists(List<Attachment> attachments) {
        return attachments.stream()
                .anyMatch(attachment -> attachment.getAttachmentType() == Attachment.AttachmentType.MEDICAL_REPORT);
    }

}
