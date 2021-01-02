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
import ru.mvd.driving.license.domain.model.DrivingLicenseDisabled;
import ru.mvd.driving.license.domain.model.DrivingLicenseId;

@ActiveProfiles("test")
@ContextConfiguration(classes = {Application.class})
public class DisableDrivingLicenseCommandProcessorTest extends AbstractTest {
    @Autowired
    private TestDomainObjectsFactory testDomainObjectsFactory;
    @Autowired
    private TestCommandFactory testCommandFactory;
    @Autowired
    private CommandProcessor<DisableDrivingLicenseCommand, String> disableDrivingLicenseCommandProcessor;
    @MockBean
    private DomainEventPublisher<DrivingLicenseDisabled> drivingLicenseDisabledDomainEventPublisher;
    @Captor
    private ArgumentCaptor<DrivingLicense> drivingLicenseArgumentCaptor;
    @Captor
    private ArgumentCaptor<DrivingLicenseDisabled> drivingLicenseDisabledArgumentCaptor;

    @Before
    public void setup() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        Mockito.when(drivingLicenseRepository.findByDrivingLicenseId(ArgumentMatchers.any(DrivingLicenseId.class)))
                .thenReturn(drivingLicense);
    }

    @Test
    public void testDisableDrivingLicense() {
        DisableDrivingLicenseCommand command = testCommandFactory.createDisableDrivingLicenseCommand();

        String id = disableDrivingLicenseCommandProcessor.process(command);

        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        Mockito.verify(drivingLicenseDisabledDomainEventPublisher).publish(drivingLicenseDisabledArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseDisabled drivingLicenseDisabledDomainEvent = drivingLicenseDisabledArgumentCaptor.getValue();
        Assert.assertNotNull(id);
        assertDisabledDrivingLicense(drivingLicense);
        assertDrivingLicenseDisabledDomainEvent(drivingLicenseDisabledDomainEvent);
    }

}
