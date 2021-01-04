package ru.mvd.driving.license.domain;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import ru.mvd.driving.license.AbstractTest;
import ru.mvd.driving.license.domain.model.DrivingLicense;
import ru.mvd.driving.license.domain.model.DrivingLicenseDisabled;
import ru.mvd.driving.license.domain.model.DrivingLicenseRevocationExpired;
import ru.mvd.driving.license.domain.model.ExpirationControlService;

import java.util.concurrent.TimeUnit;

@ActiveProfiles("test")
public class ExpirationControlServiceTest extends AbstractTest {
    @Autowired
    private TestDomainObjectsFactory testDomainObjectsFactory;
    @Autowired
    private ExpirationControlService expirationControlService;
    @Captor
    private ArgumentCaptor<DrivingLicense> drivingLicenseArgumentCaptor;

    @Test
    public void testCheckExpiredRevocation() {
        DrivingLicense revokedDrivingLicense = testDomainObjectsFactory.newDrivingLicenseWithExpiredRevocation();
        Mockito.when(drivingLicenseRepository.findNextRevokedDrivingLicense())
                .thenReturn(revokedDrivingLicense);

        Awaitility.await()
                .atMost(6, TimeUnit.SECONDS)
                .untilAsserted(() -> expirationControlService.checkRevocationExpiration());

        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseDisabled drivingLicenseDisabledDomainEvent = drivingLicense
                .getDomainEventByType(DrivingLicenseDisabled.class);
        DrivingLicenseRevocationExpired drivingLicenseRevocationExpiredDomainEvent = drivingLicense
                .getDomainEventByType(DrivingLicenseRevocationExpired.class);

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

        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseDisabled drivingLicenseDisabledDomainEvent = drivingLicense
                .getDomainEventByType(DrivingLicenseDisabled.class);
        DrivingLicenseRevocationExpired drivingLicenseRevocationExpiredDomainEvent = drivingLicense
                .getDomainEventByType(DrivingLicenseRevocationExpired.class);

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

        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseDisabled drivingLicenseDisabledDomainEvent = drivingLicense
                .getDomainEventByType(DrivingLicenseDisabled.class);

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

        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseDisabled drivingLicenseDisabledDomainEvent = drivingLicense
                .getDomainEventByType(DrivingLicenseDisabled.class);

        DrivingLicense.Status status = (DrivingLicense.Status) ReflectionTestUtils.getField(drivingLicense, "status");
        Assert.assertEquals(status, DrivingLicense.Status.VALID);
        Assert.assertNull(drivingLicenseDisabledDomainEvent);
    }
}
