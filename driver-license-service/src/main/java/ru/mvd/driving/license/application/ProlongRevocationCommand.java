package ru.mvd.driving.license.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProlongRevocationCommand implements Command{
    @NotEmpty(message = "drivingLicenseId can't be null or empty")
    private String drivingLicenseId;
    @NotNull(message = "revocationEndDate can't be null")
    @Future(message = "revocationEndDate must be in the future tense")
    private LocalDateTime revocationEndDate;
    @NotEmpty(message = "judgmentFileId can't be null or empty")
    private String judgmentFileId;
}
