package ru.mvd.driving.license.infrastructure.events.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLicenseIssuedIntegrationEvent implements IntegrationEvent{
    private String drivingLicenseId;
    private String departmentId;
    private String personId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Set<Category> categories;
    private Set<String> specialMarks;
    private List<Attachment> attachments;
    private String issuanceReason;

    @Data
    @ToString
    @AllArgsConstructor
    public static class Category{
        private LocalDate startDate;
        private LocalDate endDate;
        private String type;
        private Set<String> specialMarks;
    }

    @Data
    @ToString
    @AllArgsConstructor
    public static class Attachment{
        private String type;
        private String fileId;
    }
}
