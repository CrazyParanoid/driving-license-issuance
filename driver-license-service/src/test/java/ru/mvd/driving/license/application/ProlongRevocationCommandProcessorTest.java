package ru.mvd.driving.license.application;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.mvd.driving.license.AbstractTest;
import ru.mvd.driving.license.Application;
import ru.mvd.driving.license.domain.TestDomainObjectsFactory;
import ru.mvd.driving.license.domain.model.DomainEventPublisher;
import ru.mvd.driving.license.domain.model.DrivingLicense;
import ru.mvd.driving.license.domain.model.DrivingLicenseId;
import ru.mvd.driving.license.domain.model.DrivingLicenseRevocationProlonged;

@ActiveProfiles("test")
@ContextConfiguration(classes = {Application.class})
public class ProlongRevocationCommandProcessorTest extends AbstractTest {
    @Autowired
    private CommandProcessor<ProlongRevocationCommand, String> prolongRevocationCommandProcessor;
    @Autowired
    private TestDomainObjectsFactory testDomainObjectsFactory;
    @Autowired
    private TestCommandFactory testCommandFactory;
    @MockBean
    private DomainEventPublisher<DrivingLicenseRevocationProlonged> drivingLicenseRevocationProlongedDomainEventPublisher;
    @Captor
    private ArgumentCaptor<DrivingLicense> drivingLicenseArgumentCaptor;
    @Captor
    private ArgumentCaptor<DrivingLicenseRevocationProlonged> drivingLicenseRevocationProlongedArgumentCaptor;

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
        Mockito.verify(drivingLicenseRevocationProlongedDomainEventPublisher)
                .publish(drivingLicenseRevocationProlongedArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseRevocationProlonged drivingLicenseRevocationProlongedDomainEvent =
                drivingLicenseRevocationProlongedArgumentCaptor.getValue();
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
