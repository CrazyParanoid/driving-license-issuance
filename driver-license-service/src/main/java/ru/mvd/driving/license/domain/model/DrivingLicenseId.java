package ru.mvd.driving.license.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.mvd.driving.license.domain.supertype.ValueObject;

import java.util.Objects;

@Getter(AccessLevel.PACKAGE)
@AllArgsConstructor
public class DrivingLicenseId implements ValueObject {
    private String series;
    private String number;

    public String toFullNumber(){
        return this.series + this.number;
    }

    public static DrivingLicenseId identifyFrom(String fullId){
        if(StringUtils.isEmpty(fullId))
            return null;
        String number = fullId.substring(0, 3);
        String series = fullId.substring(3);
        return new DrivingLicenseId(number, series);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        DrivingLicenseId drivingLicenseId = (DrivingLicenseId) object;
        return Objects.equals(series, drivingLicenseId.series) &&
                Objects.equals(number, drivingLicenseId.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(series, number);
    }
}
