package ru.mvd.driving.license.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import ru.mvd.driving.license.domain.supertype.AggregateRoot;
import ru.mvd.driving.license.domain.supertype.DomainEvent;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

public class DrivingLicense extends AggregateRoot {
    public static final long DRIVING_LICENSE_VALID_YEAR_PERIOD = 10;

    private DrivingLicenseId drivingLicenseId;
    private DepartmentId departmentId;
    private PersonId personId;
    private LocalDate startDate;
    @Getter(AccessLevel.PACKAGE)
    private LocalDate endDate;
    private Set<Category> categories;
    private Set<SpecialMark> specialMarks;
    private List<Attachment> attachments;
    private Revocation revocation;
    private Status status;
    private IssuanceReason issuanceReason;

    DrivingLicense(List<DomainEvent> domainEvents, DrivingLicenseId drivingLicenseId,
                   DepartmentId departmentId, PersonId personId, LocalDate startDate,
                   LocalDate endDate, Set<Category> categories, Set<SpecialMark> specialMarks,
                   List<Attachment> attachments, Status status, IssuanceReason issuanceReason) {
        super(domainEvents);
        this.drivingLicenseId = drivingLicenseId;
        this.departmentId = departmentId;
        this.personId = personId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categories = categories;
        this.specialMarks = specialMarks;
        this.attachments = attachments;
        this.status = status;
        this.issuanceReason = issuanceReason;
    }

    public void revoke(LocalDate revocationEndDate, String judgmentFileId){
        if(this.status == Status.INVALID)
            throw new IllegalStateException("Wrong invocation for current state");
        this.revocation = startRevocation(revocationEndDate);
        attachJudgment(judgmentFileId);
        this.status = Status.REVOKED;
        raiseDomainEvent(new DrivingLicenseRevoked(this.drivingLicenseId, this.revocation.getRevocationId(),
                this.revocation.getStartDate(), this.revocation.getEndDate()));
    }

    void openSubCategories(){
        openSubCategory(Category.CategoryType.A, Category.CategoryType.A1);
        openSubCategory(Category.CategoryType.B, Category.CategoryType.B1);
        openSubCategory(Category.CategoryType.C, Category.CategoryType.C1);
        openSubCategory(Category.CategoryType.D, Category.CategoryType.D1);
    }

    private void openSubCategory(Category.CategoryType categoryType, Category.CategoryType subCategoryType){
        if(isCategoryOpen(categoryType)){
            Category category = this.categories.stream()
                    .filter(c -> c.getType() == categoryType)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("Category %s is not found", categoryType.getName())));
            this.categories.add(Category.open(subCategoryType, category.getStartDate(),
                    category.getEndDate(), category.getSpecialMarks()));
        }
    }

    private boolean isCategoryOpen(Category.CategoryType categoryType){
        return this.categories.stream().anyMatch(category -> category.getType() == categoryType);
    }

    public void prolongRevocation(LocalDate endDate){
        if(this.status != Status.REVOKED)
            throw new IllegalStateException("Wrong invocation for current state");
        this.revocation.prolong(endDate);
        raiseDomainEvent(new DrivingLicenseRevocationProlonged(this.drivingLicenseId, this.revocation.getRevocationId(), endDate));
    }

    private Revocation startRevocation(LocalDate endDate){
        UUID uuid = UUID.randomUUID();
        RevocationId revocationId = new RevocationId(uuid.toString());
        return new Revocation(revocationId, LocalDate.now(), endDate, false);
    }

    public void attachJudgment(String fileId){
        Attachment attachment = Attachment.newJudgmentAttachment(fileId);
        this.attachments.add(attachment);
    }

    public void disable(){
        if(this.status != Status.INVALID){
            this.status = Status.INVALID;
            raiseDomainEvent(new DrivingLicenseDisabled(this.drivingLicenseId));
        }
    }

    public void disableIfRevocationExpired(){
        if(status == Status.REVOKED){
            this.revocation.defineExpiration();
            if(this.revocation.isExpired()){
                disable();
                raiseDomainEvent(new DrivingLicenseRevocationExpired(this.drivingLicenseId, this.revocation.getRevocationId()));
            }
        }
    }

    public void disableIfExpired(){
        if(status == Status.VALID){
            if(isExpired()){
                this.status = Status.INVALID;
                raiseDomainEvent(new DrivingLicenseDisabled(this.drivingLicenseId));
            }
        }
    }

    private boolean isExpired(){
        LocalDate currentDate = LocalDate.now();
        return currentDate.isAfter(this.endDate) || currentDate.isEqual(this.endDate);
    }

    public void verifyAttachmentCompleteness(Function<List<Attachment>, Boolean> action){
        boolean allAttachmentsExist = action.apply(this.attachments);
        if(!allAttachmentsExist)
            throw new IllegalStateException("Illegal document completeness for issuance");
    }

    @RequiredArgsConstructor
    public enum Status{
        VALID("Права действительны"),
        INVALID("Права недействительны"),
        REVOKED("Лишение прав");

        @Getter
        private final String description;
    }

    @RequiredArgsConstructor
    public enum SpecialMark{
        GCL("GCL"),
        ML("ML"),
        AS("AS"),
        MS("MS"),
        AT("AT"),
        MC("MC"),
        APS("APS"),
        HA_CF("HA_CF"),
        Unknown("Unknown");

        @Getter(AccessLevel.PACKAGE)
        private final String name;

        public static SpecialMark fromName(String name){
            for(SpecialMark specialMark: SpecialMark.values()){
                if(specialMark.getName().equals(name))
                    return specialMark;
            }
            return Unknown;
        }

        public static Set<SpecialMark> setFrom(Set<String> aSpecialMarks){
            Set<SpecialMark> specialMarks = new HashSet<>();
            if(CollectionUtils.isNotEmpty(aSpecialMarks)){
                for (String rawSpecialMark: aSpecialMarks){
                    SpecialMark specialMark = SpecialMark.fromName(rawSpecialMark);
                    if(specialMark == SpecialMark.Unknown)
                        throw new IllegalArgumentException(String.format("Unknown special mark %s", rawSpecialMark));
                    specialMarks.add(specialMark);
                }
            }
            return specialMarks;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        DrivingLicense drivingLicense = (DrivingLicense) object;
        return Objects.equals(drivingLicenseId, drivingLicense.drivingLicenseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(drivingLicenseId);
    }
}
