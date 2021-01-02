package ru.mvd.driving.license.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevokeDrivingLicenseCommand implements Command{
    @NotEmpty(message = "drivingLicenseId can't be null or empty")
    private String drivingLicenseId;
    @NotNull(message = "revocationEndDate can't be null")
    @Future(message = "revocationEndDate must be in the future tense")
    private LocalDate revocationEndDate;
    @NotEmpty(message = "judgmentFileId can't be null or empty")
    private String judgmentFileId;
}
