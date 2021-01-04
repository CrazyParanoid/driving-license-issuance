package ru.mvd.driving.license.domain;

import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;
import ru.mvd.driving.license.domain.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static ru.mvd.driving.license.TestValues.*;

@Component
public class TestDomainObjectsFactory {
    private final DrivingLicenseFactory drivingLicenseFactory;

    public TestDomainObjectsFactory(DrivingLicenseFactory drivingLicenseFactory) {
        this.drivingLicenseFactory = drivingLicenseFactory;
    }

    public Set<Category> createCategories() {
        Set<Category> categories = new HashSet<>();
        Set<DrivingLicense.SpecialMark> specialMarks = new HashSet<>();
        categories.add(Category.open(Category.CategoryType.B, LocalDate.now(),
                LocalDate.of(2024, 10, 12), specialMarks));
        return categories;
    }

    public List<Attachment> createAttachments() {
        List<Attachment> attachments = new ArrayList<>();
        attachments.add(Attachment.newAttachment(Attachment.AttachmentType.STATEMENT, UUID.randomUUID().toString()));
        attachments.add(Attachment.newAttachment(Attachment.AttachmentType.MEDICAL_REPORT, UUID.randomUUID().toString()));
        attachments.add(Attachment.newAttachment(Attachment.AttachmentType.PAYMENT_RECEIPT, UUID.randomUUID().toString()));
        attachments.add(Attachment.newAttachment(Attachment.AttachmentType.DRIVING_SCHOOL_GRADUATION_CERTIFICATE, UUID.randomUUID().toString()));
        return attachments;
    }

    public List<Attachment> createIncorrectAttachments() {
        List<Attachment> attachments = new ArrayList<>();
        attachments.add(Attachment.newAttachment(Attachment.AttachmentType.STATEMENT, UUID.randomUUID().toString()));
        attachments.add(Attachment.newAttachment(Attachment.AttachmentType.MEDICAL_REPORT, UUID.randomUUID().toString()));
        attachments.add(Attachment.newAttachment(Attachment.AttachmentType.DRIVING_SCHOOL_GRADUATION_CERTIFICATE, UUID.randomUUID().toString()));
        return attachments;
    }

    private Set<DrivingLicense.SpecialMark> createSpecialMarks() {
        return DrivingLicense.SpecialMark.setFrom(Set.of("AS", "ML", "MC"));
    }

    public DrivingLicense newDrivingLicense(Set<Category> categories, List<Attachment> attachments,
                                            DrivingLicenseId previousDrivingLicenseId) {
        return drivingLicenseFactory.issueDrivingLicense(
                IssuanceReason.fromName(ISSUANCE_REASON),
                new PersonId(PERSON_ID),
                categories,
                attachments,
                new DepartmentId(DEPARTMENT_ID),
                new AreaCode(AREA_CODE),
                previousDrivingLicenseId,
                createSpecialMarks()
        );
    }

    public DrivingLicense newDrivingLicense() {
        Set<Category> categories = createCategories();
        List<Attachment> attachments = createAttachments();
        return drivingLicenseFactory.issueDrivingLicense(
                IssuanceReason.fromName(ISSUANCE_REASON),
                new PersonId(PERSON_ID),
                categories,
                attachments,
                new DepartmentId(DEPARTMENT_ID),
                new AreaCode(AREA_CODE),
                null,
                createSpecialMarks()
        );
    }

    public DrivingLicense newDrivingLicenseForPersonNameDetailsChanging() {
        Set<Category> categories = createCategories();
        List<Attachment> attachments = createAttachments();
        return drivingLicenseFactory.issueDrivingLicense(
                IssuanceReason.PERSON_NAME_DETAILS_CHANGE,
                new PersonId(PERSON_ID),
                categories,
                attachments,
                new DepartmentId(DEPARTMENT_ID),
                new AreaCode(AREA_CODE),
                null,
                createSpecialMarks()
        );
    }

    public DrivingLicense newDrivingLicenseWithExpiredRevocation() {
        DrivingLicense drivingLicense = newDrivingLicense();
        LocalDateTime revocationEndDate = LocalDateTime.of(2020, 3, 12, 22, 45);
        drivingLicense.revoke(revocationEndDate, JUDGMENT_FILE_ID);
        return drivingLicense;
    }

    public DrivingLicense newDrivingLicenseWithNotExpiredRevocation() {
        DrivingLicense drivingLicense = newDrivingLicense();
        drivingLicense.revoke(REVOCATION_END_DATE, JUDGMENT_FILE_ID);
        return drivingLicense;
    }

    public DrivingLicense newExpiredDrivingLicense() {
        DrivingLicense drivingLicense = newDrivingLicense();
        ReflectionTestUtils.setField(drivingLicense, "endDate", LocalDateTime.of(2019, 2, 14, 22, 45));
        return drivingLicense;
    }

    public DrivingLicense newInvalidDrivingLicense() {
        DrivingLicense drivingLicense = newDrivingLicense();
        ReflectionTestUtils.setField(drivingLicense, "status", DrivingLicense.Status.INVALID);
        return drivingLicense;
    }

    public DrivingLicense newRevokedDrivingLicense() {
        DrivingLicense drivingLicense = newDrivingLicense();
        drivingLicense.revoke(REVOCATION_END_DATE, JUDGMENT_FILE_ID);
        return drivingLicense;
    }
}
