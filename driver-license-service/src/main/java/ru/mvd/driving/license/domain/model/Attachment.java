package ru.mvd.driving.license.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.mvd.driving.license.domain.supertype.ValueObject;

import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Attachment implements ValueObject {
    @Getter(AccessLevel.PACKAGE)
    private AttachmentType attachmentType;
    private String fileId;

    public static Attachment newAttachment(String anAttachment, String fileId){
        Attachment.AttachmentType attachmentType = Attachment.AttachmentType.fromName(anAttachment);
        if(attachmentType == Attachment.AttachmentType.UNKNOWN)
            throw new IllegalArgumentException(String.format("Unknown attachment type %s", anAttachment));
        return new Attachment(attachmentType, fileId);
    }

    public static Attachment newAttachment(AttachmentType attachmentType, String fileId){
        return new Attachment(attachmentType, fileId);
    }

    static Attachment newJudgmentAttachment(String fileId){
        return new Attachment(AttachmentType.JUDGMENT, fileId);
    }

    @RequiredArgsConstructor
    public enum AttachmentType{
        STATEMENT("STATEMENT"),
        MEDICAL_REPORT("MEDICAL_REPORT"),
        PAYMENT_RECEIPT("PAYMENT_RECEIPT"),
        CONFIRMATION_CHANGE_VALIDITY("CONFIRMATION_CHANGE_VALIDITY"),
        DRIVING_SCHOOL_GRADUATION_CERTIFICATE("DRIVING_SCHOOL_GRADUATION_CERTIFICATE"),
        JUDGMENT("JUDGMENT"),
        UNKNOWN("UNKNOWN");

        @Getter(AccessLevel.PRIVATE)
        private final String name;

        public static Attachment.AttachmentType fromName(String name){
            for(Attachment.AttachmentType attachmentType: Attachment.AttachmentType.values()){
                if(attachmentType.getName().equals(name))
                    return attachmentType;
            }
            return UNKNOWN;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Attachment attachment = (Attachment) object;
        return attachmentType == attachment.attachmentType &&
                Objects.equals(fileId, attachment.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attachmentType, fileId);
    }
}
