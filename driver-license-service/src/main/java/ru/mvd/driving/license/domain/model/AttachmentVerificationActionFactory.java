package ru.mvd.driving.license.domain.model;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

import static ru.mvd.driving.license.domain.model.Attachment.*;
import static ru.mvd.driving.license.domain.model.Attachment.AttachmentType.*;

@Component
public class AttachmentVerificationActionFactory {

    public Function<List<Attachment>, Boolean> makeVerificationActionForReason(IssuanceReason issuanceReason){
        switch (issuanceReason){
            case FIRST_ISSUANCE:
                return makeFirstIssuanceVerification();
            case PERSON_NAME_DETAILS_CHANGE:
                return makePersonNameDetailsChangeVerification();
            default:
                return makeBasisVerification();
        }
    }

    private Function<List<Attachment>, Boolean> makeBasisVerification(){
        return (attachments) -> {
            boolean statementExists = areAttachmentsContainType(attachments, STATEMENT);
            boolean medicalReportExists = areAttachmentsContainType(attachments, MEDICAL_REPORT);
            boolean paymentReceiptExists = areAttachmentsContainType(attachments, PAYMENT_RECEIPT);
            return statementExists
                    & medicalReportExists
                    & paymentReceiptExists;
        };
    }

    private Function<List<Attachment>, Boolean> makeFirstIssuanceVerification(){
        return (attachments) -> {
            boolean statementExists = areAttachmentsContainType(attachments, STATEMENT);
            boolean medicalReportExists = areAttachmentsContainType(attachments, MEDICAL_REPORT);
            boolean paymentReceiptExists = areAttachmentsContainType(attachments, PAYMENT_RECEIPT);
            boolean drivingSchoolGraduationCertificateExists =
                    areAttachmentsContainType(attachments, DRIVING_SCHOOL_GRADUATION_CERTIFICATE);
            return statementExists
                    & medicalReportExists
                    & paymentReceiptExists
                    & drivingSchoolGraduationCertificateExists;
        };
    }

    private Function<List<Attachment>, Boolean> makePersonNameDetailsChangeVerification(){
        return (attachments) -> {
            boolean statementExists = areAttachmentsContainType(attachments, STATEMENT);
            boolean paymentReceiptExists = areAttachmentsContainType(attachments, PAYMENT_RECEIPT);
            boolean confirmationExists = areAttachmentsContainType(attachments, CONFIRMATION_CHANGE_VALIDITY);
            return statementExists
                    & paymentReceiptExists
                    & confirmationExists;
        };
    }

    private boolean areAttachmentsContainType(List<Attachment> attachments, AttachmentType attachmentType){
        return attachments.stream()
                .anyMatch(attachment -> attachment.getAttachmentType() == attachmentType);
    }

}
