package ru.mvd.driving.license.domain;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import ru.mvd.driving.license.AbstractTest;
import ru.mvd.driving.license.Application;
import ru.mvd.driving.license.domain.model.*;

import java.util.concurrent.TimeUnit;

@ActiveProfiles("test")
@ContextConfiguration(classes = {Application.class})
public class ExpirationControlServiceTest extends AbstractTest {
    @Autowired
    private TestDomainObjectsFactory testDomainObjectsFactory;
    @Autowired
    private ExpirationControlService expirationControlService;
    @MockBean
    private DomainEventPublisher<DrivingLicenseRevocationExpired> drivingLicenseRevocationExpiredDomainEventPublisher;
    @MockBean
    private DomainEventPublisher<DrivingLicenseDisabled> drivingLicenseDisabledDomainEventPublisher;
    @Captor
    private ArgumentCaptor<DrivingLicense> drivingLicenseArgumentCaptor;
    @Captor
    private ArgumentCaptor<DrivingLicenseRevocationExpired> drivingLicenseRevocationExpiredArgumentCaptor;
    @Captor
    private ArgumentCaptor<DrivingLicenseDisabled> drivingLicenseDisabledArgumentCaptor;

    @Test
    public void testCheckExpiredRevocation() {
        DrivingLicense revokedDrivingLicense = testDomainObjectsFactory.newDrivingLicenseWithExpiredRevocation();
        Mockito.when(drivingLicenseRepository.findNextRevokedDrivingLicense())
                .thenReturn(revokedDrivingLicense);

        Awaitility.await()
                .atMost(6, TimeUnit.SECONDS)
                .untilAsserted(() -> expirationControlService.checkRevocationExpiration());

        Mockito.verify(drivingLicenseDisabledDomainEventPublisher)
                .publish(drivingLicenseDisabledArgumentCaptor.capture());
        Mockito.verify(drivingLicenseRevocationExpiredDomainEventPublisher)
                .publish(drivingLicenseRevocationExpiredArgumentCaptor.capture());
        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseDisabled drivingLicenseDisabledDomainEvent =
                drivingLicenseDisabledArgumentCaptor.getValue();
        DrivingLicenseRevocationExpired drivingLicenseRevocationExpiredDomainEvent =
                drivingLicenseRevocationExpiredArgumentCaptor.getValue();

        DrivingLicense.Status status = (DrivingLicense.Status) ReflectionTestUtils.getField(drivingLicense, "status");
        Assert.assertEquals(status, DrivingLicense.Status.INVALID);
        Assert.assertNotNull(drivingLicenseRevocationExpiredDomainEvent);
        Assert.assertNotNull(drivingLicenseRevocationExpiredDomainEvent.getDrivingLicenseId());
        Assert.assertNotNull(drivingLicenseDisabledDomainEvent);
        Assert.assertNotNull(drivingLicenseDisabledDomainEvent.getDrivingLicenseId());
    }

    @Test
    public void testCheckNotExpiredRevocation() {
        DrivingLicense revokedDrivingLicense = testDomainObjectsFactory.newDrivingLicenseWithNotExpiredRevocation();
        Mockito.when(drivingLicenseRepository.findNextRevokedDrivingLicense())
                .thenReturn(revokedDrivingLicense);

        Awaitility.await()
                .atMost(6, TimeUnit.SECONDS)
                .untilAsserted(() -> expirationControlService.checkRevocationExpiration());

        Mockito.verify(drivingLicenseDisabledDomainEventPublisher)
                .publish(drivingLicenseDisabledArgumentCaptor.capture());
        Mockito.verify(drivingLicenseRevocationExpiredDomainEventPublisher)
                .publish(drivingLicenseRevocationExpiredArgumentCaptor.capture());
        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseDisabled drivingLicenseDisabledDomainEvent =
                drivingLicenseDisabledArgumentCaptor.getValue();
        DrivingLicenseRevocationExpired drivingLicenseRevocationExpiredDomainEvent =
                drivingLicenseRevocationExpiredArgumentCaptor.getValue();

        DrivingLicense.Status status = (DrivingLicense.Status) ReflectionTestUtils.getField(drivingLicense, "status");
        Assert.assertEquals(status, DrivingLicense.Status.REVOKED);
        Assert.assertNull(drivingLicenseRevocationExpiredDomainEvent);
        Assert.assertNull(drivingLicenseDisabledDomainEvent);
    }

    @Test
    public void testCheckExpiredDrivingLicense() {
        DrivingLicense expiredDrivingLicense = testDomainObjectsFactory.newExpiredDrivingLicense();
        Mockito.when(drivingLicenseRepository.findNextValidDrivingLicense())
                .thenReturn(expiredDrivingLicense);

        Awaitility.await()
                .atMost(6, TimeUnit.SECONDS)
                .untilAsserted(() -> expirationControlService.checkDrivingLicenseExpiration());

        Mockito.verify(drivingLicenseDisabledDomainEventPublisher)
                .publish(drivingLicenseDisabledArgumentCaptor.capture());
        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseDisabled drivingLicenseDisabledDomainEvent =
                drivingLicenseDisabledArgumentCaptor.getValue();

        DrivingLicense.Status status = (DrivingLicense.Status) ReflectionTestUtils.getField(drivingLicense, "status");
        Assert.assertEquals(status, DrivingLicense.Status.INVALID);
        Assert.assertNotNull(drivingLicenseDisabledDomainEvent);
        Assert.assertNotNull(drivingLicenseDisabledDomainEvent.getDrivingLicenseId());
    }

    @Test
    public void testCheckNotExpiredDrivingLicense() {
        DrivingLicense notExpiredDrivingLicense = testDomainObjectsFactory.newDrivingLicense();
        Mockito.when(drivingLicenseRepository.findNextValidDrivingLicense())
                .thenReturn(notExpiredDrivingLicense);

        Awaitility.await()
                .atMost(6, TimeUnit.SECONDS)
                .untilAsserted(() -> expirationControlService.checkDrivingLicenseExpiration());

        Mockito.verify(drivingLicenseDisabledDomainEventPublisher)
                .publish(drivingLicenseDisabledArgumentCaptor.capture());
        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseDisabled drivingLicenseDisabledDomainEvent =
                drivingLicenseDisabledArgumentCaptor.getValue();

        DrivingLicense.Status status = (DrivingLicense.Status) ReflectionTestUtils.getField(drivingLicense, "status");
        Assert.assertEquals(status, DrivingLicense.Status.VALID);
        Assert.assertNull(drivingLicenseDisabledDomainEvent);
    }
}
