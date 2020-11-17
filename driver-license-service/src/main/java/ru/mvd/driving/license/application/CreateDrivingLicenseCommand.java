package ru.mvd.driving.license.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDrivingLicenseCommand implements Command{
    private String departmentId;
    private String personId;
    private String areaCode;
    private Set<AttachmentDTO> attachments;
    private Set<CategoryDTO> categories;
    private Set<String> specialMarks;
    private String issuanceReason;

    @Data
    @AllArgsConstructor
    public static class AttachmentDTO{
        private String attachmentType;
        private String fileId;
    }

    @Data
    @AllArgsConstructor
    public static class CategoryDTO{
        private LocalDate startDate;
        private LocalDate endDate;
        private String categoryType;
        private Set<String> specialMarks;
    }

}
