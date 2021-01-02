package ru.mvd.driving.license.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueDrivingLicenseCommand implements Command{
    @NotEmpty(message = "drivingLicenseId can't be null or empty")
    private String departmentId;
    @NotEmpty(message = "personId can't be null or empty")
    private String personId;
    @NotEmpty(message = "areaCode can't be null or empty")
    private String areaCode;
    @NotEmpty(message = "attachments can't be empty")
    private Set<@Valid AttachmentDTO> attachments;
    @NotEmpty(message = "categories can't be empty")
    private Set<@Valid CategoryDTO> categories;
    @NotEmpty(message = "specialMarks can't be empty")
    private Set<String> specialMarks;
    @NotEmpty(message = "issuanceReason can't be null or empty")
    private String issuanceReason;
    private String previousDrivingLicenseId;

    @Data
    @AllArgsConstructor
    public static class AttachmentDTO{
        @NotEmpty(message = "attachmentType can't be null or empty")
        private String attachmentType;
        @NotEmpty(message = "fileId can't be null or empty")
        private String fileId;
    }

    @Data
    @AllArgsConstructor
    public static class CategoryDTO{
        @NotNull(message = "startDate can't be null")
        private LocalDate startDate;
        @NotNull(message = "endDate can't be null")
        @Future(message = "endDate must be in the future tense")
        private LocalDate endDate;
        @NotEmpty(message = "categoryType can't be null or empty")
        private String categoryType;
        @NotEmpty(message = "specialMarks can't be empty")
        private Set<String> specialMarks;
    }

}
