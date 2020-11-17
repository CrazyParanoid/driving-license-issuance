package ru.mvd.driving.license.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.mvd.driving.license.domain.supertype.DomainPayloadObject;

import java.util.List;
import java.util.Set;

@Getter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateDrivingLicencePayloadObject implements DomainPayloadObject {
    private DepartmentId departmentId;
    private PersonId personId;
    private Set<Category> categories;
    private Set<DrivingLicense.SpecialMark> specialMarks;
    private AreaCode areaCode;
    private List<Attachment> attachments;
    private IssuanceReason issuanceReason;
    private DrivingLicenseId previousDrivingLicenseId;

    public static Builder newCreateDrivingLicencePayloadObject(){
        return new CreateDrivingLicencePayloadObject().new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class Builder{
        public Builder withDepartment(String aDepartmentId){
            CreateDrivingLicencePayloadObject.this.departmentId = new DepartmentId(aDepartmentId);
            return this;
        }

        public Builder withPerson(String aPersonId){
            CreateDrivingLicencePayloadObject.this.personId = new PersonId(aPersonId);
            return this;
        }

        public Builder withCategories(Set<Category> categories){
            CreateDrivingLicencePayloadObject.this.categories = categories;
            return this;
        }

        public Builder withSpecialMarks(Set<String> aSpecialMarks){
            CreateDrivingLicencePayloadObject.this.specialMarks = DrivingLicense.SpecialMark.setFrom(aSpecialMarks);
            return this;
        }

        public Builder withArea(String anAreaCode){
            CreateDrivingLicencePayloadObject.this.areaCode = new AreaCode(anAreaCode);
            return this;
        }

        public Builder withAttachments(List<Attachment> attachments){
            CreateDrivingLicencePayloadObject.this.attachments = attachments;
            return this;
        }

        public Builder withIssuanceReason(String anIssuanceReason){
            IssuanceReason issuanceReason = IssuanceReason.fromName(anIssuanceReason);
            if(issuanceReason == IssuanceReason.UNKNOWN)
                throw new IllegalArgumentException(String.format("Unknown issuance reason type %s", anIssuanceReason));
            CreateDrivingLicencePayloadObject.this.issuanceReason = issuanceReason;
            return this;
        }

        public Builder withPreviousDrivingLicense(String aPreviousDrivingLicenseId){
            CreateDrivingLicencePayloadObject.this.previousDrivingLicenseId = DrivingLicenseId.identifyFrom(aPreviousDrivingLicenseId);
            return this;
        }

        public CreateDrivingLicencePayloadObject build(){
            return CreateDrivingLicencePayloadObject.this;
        }
    }

}
