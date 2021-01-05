package ru.mvd.driving.license.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.mvd.driving.license.domain.supertype.ValueObject;

import java.util.Objects;

@AllArgsConstructor
public class DrivingLicenseId implements ValueObject {
    private String series;
    private String number;
    @Getter
    private String fullNumber;

    public DrivingLicenseId nextId() {
        long newNumber = Long.parseLong(this.number) + 1;
        String formattedNewNumber = String.format("%06d", newNumber);
        String fullNumber = makeFullNumber(series, formattedNewNumber);
        return new DrivingLicenseId(this.series, formattedNewNumber, fullNumber);
    }

    public static DrivingLicenseId newDrivingLicenseId(AreaCode areaCode) {
        String series = StringUtils.rightPad("" + areaCode.getCode(), 4, "0");
        String number = String.format("%06d", 1);
        String fullNumber = makeFullNumber(series, number);
        return new DrivingLicenseId(series, number, fullNumber);
    }

    public static DrivingLicenseId identifyFrom(String fullId) {
        if (StringUtils.isEmpty(fullId))
            return null;
        String number = fullId.substring(0, 4);
        String series = fullId.substring(4);
        return new DrivingLicenseId(number, series, fullId);
    }

    private static String makeFullNumber(String series, String number) {
        return series + number;
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
