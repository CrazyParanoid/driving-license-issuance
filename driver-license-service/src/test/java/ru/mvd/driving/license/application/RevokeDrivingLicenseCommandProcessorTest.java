package ru.mvd.driving.license.application;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.mvd.driving.license.AbstractTest;
import ru.mvd.driving.license.config.MongoCustomizationConfiguration;
import ru.mvd.driving.license.domain.TestDomainObjectsFactory;
import ru.mvd.driving.license.domain.model.DrivingLicense;
import ru.mvd.driving.license.domain.model.DrivingLicenseId;
import ru.mvd.driving.license.domain.model.DrivingLicenseRevoked;

@ActiveProfiles("test")
@Import(MongoCustomizationConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RevokeDrivingLicenseCommandProcessorTest extends AbstractTest {
    @Autowired
    private CommandProcessor<RevokeDrivingLicenseCommand, String> revokeDrivingLicenseCommandProcessor;
    @Autowired
    private TestDomainObjectsFactory testDomainObjectsFactory;
    @Autowired
    private TestCommandFactory testCommandFactory;
    @Captor
    private ArgumentCaptor<DrivingLicense> drivingLicenseArgumentCaptor;

    @Before
    public void setup() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        Mockito.when(drivingLicenseRepository.findByDrivingLicenseId(ArgumentMatchers.any(DrivingLicenseId.class)))
                .thenReturn(drivingLicense);
    }

    @Test
    public void testRevokeDrivingLicense() {
        RevokeDrivingLicenseCommand command = testCommandFactory.createRevokeDrivingLicenseCommand();

        String id = revokeDrivingLicenseCommandProcessor.process(command);

        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseRevoked drivingLicenseRevokedDomainEvent = drivingLicense
                .getDomainEventByType(DrivingLicenseRevoked.class);
        Assert.assertNotNull(id);
        assertRevokeDrivingLicense(drivingLicense);
        assertDrivingLicenseRevokedDomainEvent(drivingLicenseRevokedDomainEvent);
    }

    @Test
    public void testRevokeInvalidDrivingLicense() {
        DrivingLicense invalidDrivingLicense = testDomainObjectsFactory.newInvalidDrivingLicense();
        Mockito.when(drivingLicenseRepository.findByDrivingLicenseId(ArgumentMatchers.any(DrivingLicenseId.class)))
                .thenReturn(invalidDrivingLicense);
        RevokeDrivingLicenseCommand command = testCommandFactory.createRevokeDrivingLicenseCommand();

        IllegalStateException exception = Assert.assertThrows(IllegalStateException.class, () ->
                revokeDrivingLicenseCommandProcessor.process(command));

        String expectedMessage = "Wrong invocation for current state";
        Assert.assertTrue(exception.getMessage().contains(expectedMessage));
    }

}
