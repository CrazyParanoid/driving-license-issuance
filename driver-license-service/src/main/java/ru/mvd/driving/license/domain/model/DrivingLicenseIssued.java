package ru.mvd.driving.license.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import ru.mvd.driving.license.domain.supertype.DomainEvent;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class DrivingLicenseIssued implements DomainEvent {
    private DrivingLicenseId drivingLicenseId;
    private DepartmentId departmentId;
    private PersonId personId;
    @Getter
    private LocalDate startDate;
    @Getter
    private LocalDate endDate;
    @Getter
    private Set<Category> categories;
    private Set<DrivingLicense.SpecialMark> specialMarks;

    public String getDrivingLicenseId(){
        return this.drivingLicenseId.toFullNumber();
    }

    public String getDepartmentId(){
        return this.departmentId.getId();
    }

    public String getPersonId(){
        return this.personId.getId();
    }

    public Set<String> getSpecialMarks(){
        Set<String> specialMarks = new HashSet<>();
        if(CollectionUtils.isNotEmpty(this.specialMarks))
            this.specialMarks.forEach(specialMark -> specialMarks.add(specialMark.getName()));
        return specialMarks;
    }
}
