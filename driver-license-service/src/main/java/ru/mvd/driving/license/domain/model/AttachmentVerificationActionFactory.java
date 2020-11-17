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
            boolean statementExists = isAttachmentsContainsType(attachments, STATEMENT);
            boolean medicalReportExists = isAttachmentsContainsType(attachments, MEDICAL_REPORT);
            boolean paymentReceiptExists = isAttachmentsContainsType(attachments, PAYMENT_RECEIPT);
            return statementExists & medicalReportExists & paymentReceiptExists;
        };
    }

    private Function<List<Attachment>, Boolean> makeFirstIssuanceVerification(){
        return (attachments) -> {
            boolean statementExists = isAttachmentsContainsType(attachments, STATEMENT);
            boolean medicalReportExists = isAttachmentsContainsType(attachments, MEDICAL_REPORT);
            boolean paymentReceiptExists = isAttachmentsContainsType(attachments, PAYMENT_RECEIPT);
            boolean drivingSchoolGraduationCertificateExists = isAttachmentsContainsType(attachments, DRIVING_SCHOOL_GRADUATION_CERTIFICATE);
            return statementExists & medicalReportExists & paymentReceiptExists & drivingSchoolGraduationCertificateExists;
        };
    }

    private Function<List<Attachment>, Boolean> makePersonNameDetailsChangeVerification(){
        return (attachments) -> {
            boolean statementExists = isAttachmentsContainsType(attachments, STATEMENT);
            boolean paymentReceiptExists = isAttachmentsContainsType(attachments, PAYMENT_RECEIPT);
            boolean confirmationExists = isAttachmentsContainsType(attachments, CONFIRMATION_CHANGE_VALIDITY);
            return statementExists & paymentReceiptExists & confirmationExists;
        };
    }

    private boolean isAttachmentsContainsType(List<Attachment> attachments, AttachmentType attachmentType){
        return attachments.stream().anyMatch(attachment -> attachment.getAttachmentType() == attachmentType);
    }

}
