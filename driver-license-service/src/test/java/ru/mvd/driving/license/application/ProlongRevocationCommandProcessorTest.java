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
import ru.mvd.driving.license.domain.model.DrivingLicenseRevocationProlonged;

@ActiveProfiles("test")
@Import(MongoCustomizationConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProlongRevocationCommandProcessorTest extends AbstractTest {
    @Autowired
    private CommandProcessor<ProlongRevocationCommand, String> prolongRevocationCommandProcessor;
    @Autowired
    private TestDomainObjectsFactory testDomainObjectsFactory;
    @Autowired
    private TestCommandFactory testCommandFactory;
    @Captor
    private ArgumentCaptor<DrivingLicense> drivingLicenseArgumentCaptor;

    @Before
    public void setup() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newRevokedDrivingLicense();
        Mockito.when(drivingLicenseRepository.findByDrivingLicenseId(ArgumentMatchers.any(DrivingLicenseId.class)))
                .thenReturn(drivingLicense);
    }

    @Test
    public void testProlongRevocation() {
        ProlongRevocationCommand command = testCommandFactory.createProlongRevocationCommand();

        String id = prolongRevocationCommandProcessor.process(command);

        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseRevocationProlonged drivingLicenseRevocationProlongedDomainEvent = drivingLicense
                .getDomainEventByType(DrivingLicenseRevocationProlonged.class);
        Assert.assertNotNull(id);
        assertProlongedRevokeDrivingLicense(drivingLicense);
        assertDrivingLicenseRevocationProlongedDomainEvent(drivingLicenseRevocationProlongedDomainEvent);
    }

    @Test
    public void testProlongRevocationForValidDrivingLicense() {
        ProlongRevocationCommand command = testCommandFactory.createProlongRevocationCommand();
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        Mockito.when(drivingLicenseRepository.findByDrivingLicenseId(ArgumentMatchers.any(DrivingLicenseId.class)))
                .thenReturn(drivingLicense);

        IllegalStateException exception = Assert.assertThrows(IllegalStateException.class, () ->
                prolongRevocationCommandProcessor.process(command));

        String expectedMessage = "Wrong invocation for current state";
        Assert.assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
