package ru.mvd.driving.license.application;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DisableDrivingLicenseCommand implements Command{
    private String drivingLicenseId;
}
