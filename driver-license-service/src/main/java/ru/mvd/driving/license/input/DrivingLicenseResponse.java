package ru.mvd.driving.license.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DrivingLicenseResponse extends RepresentationModel<DrivingLicenseResponse> {
    private String id;
}
