package ru.mvd.driving.license.application;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ProlongRevocationCommand implements Command{
    private String drivingLicenseId;
    private LocalDate revocationEndDate;
    private String judgmentFileId;
}
