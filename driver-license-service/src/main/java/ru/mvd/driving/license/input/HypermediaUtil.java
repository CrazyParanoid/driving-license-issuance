package ru.mvd.driving.license.input;

import lombok.experimental.UtilityClass;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import ru.mvd.driving.license.application.DisableDrivingLicenseCommand;
import ru.mvd.driving.license.application.ProlongRevocationCommand;
import ru.mvd.driving.license.application.RevokeDrivingLicenseCommand;

@UtilityClass
public class HypermediaUtil {

    void addLinks(DrivingLicenseResponse response) {
        addDisableLink(response);
        addProlongRevocationLink(response);
        addRevokeLink(response);
    }

    private void addDisableLink(DrivingLicenseResponse response) {
        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DrivingLicenseController.class)
                .postDisableDrivingLicenseCommand(new DisableDrivingLicenseCommand()))
                .withRel("disable"));
    }

    private void addRevokeLink(DrivingLicenseResponse response) {
        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DrivingLicenseController.class)
                .postRevokeDrivingLicenseCommand(new RevokeDrivingLicenseCommand()))
                .withRel("revoke"));
    }

    private void addProlongRevocationLink(DrivingLicenseResponse response) {
        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DrivingLicenseController.class)
                .postProlongRevocationCommand(new ProlongRevocationCommand()))
                .withRel("prolong-revocation"));
    }

}
