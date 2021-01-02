package ru.mvd.driving.license.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisableDrivingLicenseCommand implements Command{
    @NotEmpty(message = "drivingLicenseId can't be null or empty")
    private String drivingLicenseId;
}
