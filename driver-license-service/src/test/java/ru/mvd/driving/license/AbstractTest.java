package ru.mvd.driving.license;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mvd.driving.license.domain.model.*;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public abstract class AbstractTest {
    @Autowired
    protected DrivingLicenseFactory drivingLicenseFactory;

    protected static final String DEPARTMENT_ID = "113667";
    protected static final String PERSON_ID = "258890";
    protected static final String ISSUANCE_REASON = "FIRST_ISSUANCE";
    protected static final String AREA_CODE = "77";
    protected static final LocalDate REVOCATION_END_DATE = LocalDate.of(2030, 3, 12);
    protected static final LocalDate PROLONGED_REVOCATION_END_DATE = LocalDate.of(2032, 6, 24);
    protected static final String JUDGMENT_NUMBER = "43367812";

    protected Set<Category> createCategories(){
        Set<Category> categories = new HashSet<>();
        Set<DrivingLicense.SpecialMark> specialMarks = new HashSet<>();
        categories.add(Category.open(Category.CategoryType.B, LocalDate.now(),
                LocalDate.of(2024, 10, 12), specialMarks));
        return categories;
    }

    protected List<Attachment> createAttachments(){
        List<Attachment> attachments = new ArrayList<>();
        attachments.add(Attachment.newAttachment(Attachment.AttachmentType.STATEMENT, UUID.randomUUID().toString()));
        attachments.add(Attachment.newAttachment(Attachment.AttachmentType.MEDICAL_REPORT, UUID.randomUUID().toString()));
        attachments.add(Attachment.newAttachment(Attachment.AttachmentType.PAYMENT_RECEIPT, UUID.randomUUID().toString()));
        attachments.add(Attachment.newAttachment(Attachment.AttachmentType.DRIVING_SCHOOL_GRADUATION_CERTIFICATE, UUID.randomUUID().toString()));
        return attachments;
    }

    protected DrivingLicense newDrivingLicense(Set<Category> categories, List<Attachment> attachments){
        CreateDrivingLicencePayloadObject domainPayloadObject = CreateDrivingLicencePayloadObject.newCreateDrivingLicencePayloadObject()
                .withArea(AREA_CODE)
                .withAttachments(attachments)
                .withCategories(categories)
                .withDepartment(DEPARTMENT_ID)
                .withPerson(PERSON_ID)
                .withIssuanceReason(ISSUANCE_REASON)
                .withSpecialMarks(new HashSet<>())
                .build();
        return drivingLicenseFactory.newDrivingLicenseFrom(domainPayloadObject);
    }


}
