package ru.mvd.driving.license.application;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static ru.mvd.driving.license.TestValues.*;

@Component
public class TestCommandFactory {
    private static final String STATEMENT = "STATEMENT";
    private static final String MEDICAL_REPORT = "MEDICAL_REPORT";
    private static final String PAYMENT_RECEIPT = "PAYMENT_RECEIPT";
    private static final String DRIVING_SCHOOL_GRADUATION_CERTIFICATE = "DRIVING_SCHOOL_GRADUATION_CERTIFICATE";
    private static final String B_CATEGORY = "B";
    private static final String AS_SPECIAL_MARK = "AS";

    public IssueDrivingLicenseCommand createIssueDrivingLicenseCommand() {
        return new IssueDrivingLicenseCommand(
                DEPARTMENT_ID,
                PERSON_ID,
                AREA_CODE,
                createAttachments(),
                createCategories(),
                 Set.of(AS_SPECIAL_MARK),
                ISSUANCE_REASON,
                null
        );
    }

    public IssueDrivingLicenseCommand createInvalidIssueDrivingLicenseCommand() {
        return new IssueDrivingLicenseCommand(
                DEPARTMENT_ID,
                null,
                AREA_CODE,
                null,
                createCategories(),
                Set.of(AS_SPECIAL_MARK),
                ISSUANCE_REASON,
                null
        );
    }

    public RevokeDrivingLicenseCommand createRevokeDrivingLicenseCommand(){
        return new RevokeDrivingLicenseCommand(
                SERIES + NUMBER,
                REVOCATION_END_DATE,
                JUDGMENT_FILE_ID
        );
    }

    public DisableDrivingLicenseCommand createDisableDrivingLicenseCommand(){
        return new DisableDrivingLicenseCommand(SERIES + NUMBER);
    }

    public ProlongRevocationCommand createProlongRevocationCommand(){
        return new ProlongRevocationCommand(
                SERIES + NUMBER,
                PROLONGED_REVOCATION_END_DATE,
                JUDGMENT_FILE_ID
        );
    }

    private Set<IssueDrivingLicenseCommand.AttachmentDTO> createAttachments() {
        Set<IssueDrivingLicenseCommand.AttachmentDTO> attachments = new HashSet<>();
        attachments.add(new IssueDrivingLicenseCommand.AttachmentDTO(STATEMENT,
                UUID.randomUUID().toString()));
        attachments.add(new IssueDrivingLicenseCommand.AttachmentDTO(MEDICAL_REPORT,
                UUID.randomUUID().toString()));
        attachments.add(new IssueDrivingLicenseCommand.AttachmentDTO(PAYMENT_RECEIPT,
                UUID.randomUUID().toString()));
        attachments.add(new IssueDrivingLicenseCommand.AttachmentDTO(DRIVING_SCHOOL_GRADUATION_CERTIFICATE,
                UUID.randomUUID().toString()));
        return attachments;
    }

    private Set<IssueDrivingLicenseCommand.CategoryDTO> createCategories() {
        Set<IssueDrivingLicenseCommand.CategoryDTO> categories = new HashSet<>();
        categories.add(new IssueDrivingLicenseCommand.CategoryDTO(
                LocalDate.now(),
                LocalDate.of(2024, 10, 12),
                B_CATEGORY,
                new HashSet<>()
        ));
        return categories;
    }

}
