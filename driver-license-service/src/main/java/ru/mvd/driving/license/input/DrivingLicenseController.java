package ru.mvd.driving.license.input;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mvd.driving.license.application.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/driving-license")
public class DrivingLicenseController {
    private final CommandProcessor<IssueDrivingLicenseCommand, String> issueDrivingLicenseCommandProcessor;
    private final CommandProcessor<DisableDrivingLicenseCommand, String> disableDrivingLicenseCommandProcessor;
    private final CommandProcessor<ProlongRevocationCommand, String> prolongRevocationCommandProcessor;
    private final CommandProcessor<RevokeDrivingLicenseCommand, String> revokeDrivingLicenseCommandProcessor;

    @Autowired
    public DrivingLicenseController(CommandProcessor<IssueDrivingLicenseCommand, String> issueDrivingLicenseCommandProcessor,
                                    CommandProcessor<DisableDrivingLicenseCommand, String> disableDrivingLicenseCommandProcessor,
                                    CommandProcessor<ProlongRevocationCommand, String> prolongRevocationCommandProcessor,
                                    CommandProcessor<RevokeDrivingLicenseCommand, String> revokeDrivingLicenseCommandProcessor) {
        this.issueDrivingLicenseCommandProcessor = issueDrivingLicenseCommandProcessor;
        this.disableDrivingLicenseCommandProcessor = disableDrivingLicenseCommandProcessor;
        this.prolongRevocationCommandProcessor = prolongRevocationCommandProcessor;
        this.revokeDrivingLicenseCommandProcessor = revokeDrivingLicenseCommandProcessor;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DrivingLicenseResponse postIssueDrivingLicenseCommand(@RequestBody @Valid IssueDrivingLicenseCommand command) {
        String id = issueDrivingLicenseCommandProcessor.process(command);
        return createResponse(id);
    }

    @PostMapping("/disable")
    @ResponseStatus(HttpStatus.CREATED)
    public DrivingLicenseResponse postDisableDrivingLicenseCommand(@RequestBody @Valid DisableDrivingLicenseCommand command) {
        String id = disableDrivingLicenseCommandProcessor.process(command);
        return createResponse(id);
    }

    @PostMapping("/revocation")
    @ResponseStatus(HttpStatus.CREATED)
    public DrivingLicenseResponse postRevokeDrivingLicenseCommand(@RequestBody @Valid RevokeDrivingLicenseCommand command) {
        String id = revokeDrivingLicenseCommandProcessor.process(command);
        return createResponse(id);
    }

    @PostMapping("/revocation/prolong")
    @ResponseStatus(HttpStatus.CREATED)
    public DrivingLicenseResponse postProlongRevocationCommand(@RequestBody @Valid ProlongRevocationCommand command) {
        String id = prolongRevocationCommandProcessor.process(command);
        return createResponse(id);
    }

    private DrivingLicenseResponse createResponse(String id) {
        DrivingLicenseResponse response = new DrivingLicenseResponse(id);
        HypermediaUtil.addLinks(response);
        return response;
    }

}
