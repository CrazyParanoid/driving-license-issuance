package ru.mvd.driving.license.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import ru.mvd.driving.license.domain.supertype.DomainEvent;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DrivingLicenseIssued implements DomainEvent {
    private DrivingLicenseId drivingLicenseId;
    private DepartmentId departmentId;
    private PersonId personId;
    @Getter
    private LocalDateTime startDate;
    @Getter
    private LocalDateTime endDate;
    @Getter
    private Set<Category> categories;
    private Set<DrivingLicense.SpecialMark> specialMarks;
    private IssuanceReason issuanceReason;
    @Getter
    private List<Attachment> attachments;

    public String getDrivingLicenseId() {
        return this.drivingLicenseId.getFullNumber();
    }

    public String getDepartmentId() {
        return this.departmentId.getId();
    }

    public String getPersonId() {
        return this.personId.getId();
    }

    public Set<String> getSpecialMarks() {
        if (CollectionUtils.isNotEmpty(this.specialMarks)) {
            return specialMarks.stream()
                    .map(DrivingLicense.SpecialMark::getName)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public String getIssuanceReason() {
        return issuanceReason.getName();
    }

}
