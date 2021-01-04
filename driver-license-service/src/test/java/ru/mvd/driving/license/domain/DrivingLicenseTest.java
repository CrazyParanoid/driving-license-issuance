package ru.mvd.driving.license.domain;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import ru.mvd.driving.license.AbstractTest;
import ru.mvd.driving.license.domain.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static ru.mvd.driving.license.TestValues.*;

@ActiveProfiles("test")
public class DrivingLicenseTest extends AbstractTest {
    @Autowired
    private TestDomainObjectsFactory testDomainObjectsFactory;

    @Test
    public void testIssueDrivingLicense() {
        Set<Category> categories = testDomainObjectsFactory.createCategories();
        List<Attachment> attachments = testDomainObjectsFactory.createAttachments();
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense(categories, attachments, null);
        DrivingLicenseIssued domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseIssued.class);

        assertNewDrivingLicense(drivingLicense, categories, attachments);
        assertDrivingLicenseIssuedDomainEvent(domainEvent);
    }

    @Test
    public void testIssueDrivingLicenseWhenPersonNameDetailsChanged() {
        DrivingLicense firstDrivingLicense = testDomainObjectsFactory.newDrivingLicense();
        Mockito.when(drivingLicenseRepository.findByDrivingLicenseId(
                ArgumentMatchers.any(DrivingLicenseId.class)))
                .thenReturn(firstDrivingLicense);
        DrivingLicenseId firstDrivingLicenseId = (DrivingLicenseId) ReflectionTestUtils.getField(firstDrivingLicense, "drivingLicenseId");

        Set<Category> categories = testDomainObjectsFactory.createCategories();
        List<Attachment> attachments = testDomainObjectsFactory.createAttachments();
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense(categories, attachments, firstDrivingLicenseId);
        DrivingLicenseIssued domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseIssued.class);
        DrivingLicenseId previousDrivingLicenseId = (DrivingLicenseId) ReflectionTestUtils.getField(drivingLicense, "drivingLicenseId");

        Assert.assertNotNull(previousDrivingLicenseId);
        Assert.assertEquals(firstDrivingLicenseId, previousDrivingLicenseId);
        assertNewDrivingLicense(drivingLicense, categories, attachments);
        assertDrivingLicenseIssuedDomainEvent(domainEvent);
    }

    @Test
    public void testIssueDrivingLicenseWithIncorrectAttachments() {
        Set<Category> categories = testDomainObjectsFactory.createCategories();
        List<Attachment> attachments = testDomainObjectsFactory.createIncorrectAttachments();

        IllegalStateException exception = Assert.assertThrows(IllegalStateException.class, () -> {
            DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense(categories, attachments, null);
        });

        String expectedMessage = "Illegal document completeness for issuance";
        Assert.assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void testIssueDrivingLicenseWhenPersonNameDetailsChangedAndNullPreviousDrivingLicenseId() {
        Mockito.when(drivingLicenseRepository.nextIdentity(
                ArgumentMatchers.any(AreaCode.class)))
                .thenReturn(new DrivingLicenseId(SERIES, NUMBER));

        IllegalArgumentException exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicenseForPersonNameDetailsChanging();
        });

        String expectedMessage = "PreviousDrivingLicenseId can't be null";
        Assert.assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void testRevokeDrivingLicense() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();

        drivingLicense.revoke(REVOCATION_END_DATE, JUDGMENT_FILE_ID);
        DrivingLicenseRevoked domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseRevoked.class);

        assertRevokeDrivingLicense(drivingLicense);
        assertDrivingLicenseRevokedDomainEvent(domainEvent);
    }

    @Test
    public void testDisabledRevokeDrivingLicense() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();

        drivingLicense.disable();
        IllegalStateException exception = Assert.assertThrows(IllegalStateException.class, () -> {
            drivingLicense.revoke(REVOCATION_END_DATE, JUDGMENT_FILE_ID);
        });

        String expectedMessage = "Wrong invocation for current state";
        Assert.assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void testProlongDrivingLicenseRevocation() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        drivingLicense.revoke(REVOCATION_END_DATE, JUDGMENT_FILE_ID);

        drivingLicense.prolongRevocation(PROLONGED_REVOCATION_END_DATE);
        DrivingLicenseRevocationProlonged domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseRevocationProlonged.class);

        assertProlongedRevokeDrivingLicense(drivingLicense);
        assertDrivingLicenseRevocationProlongedDomainEvent(domainEvent);
    }

    @Test
    public void testProlongNotRevokedDrivingLicenseRevocation() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();

        IllegalStateException exception = Assert.assertThrows(IllegalStateException.class,
                () -> drivingLicense.prolongRevocation(REVOCATION_END_DATE));

        String expectedMessage = "Wrong invocation for current state";
        Assert.assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDisableDrivingLicense() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();

        drivingLicense.disable();
        DrivingLicenseDisabled domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseDisabled.class);

        assertDisabledDrivingLicense(drivingLicense);
        assertDrivingLicenseDisabledDomainEvent(domainEvent);
    }

    @Test
    public void testDisableDrivingLicenseIfExpired() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        ReflectionTestUtils.setField(drivingLicense, "endDate", LocalDateTime.of(2019, 2, 14, 22, 45));

        drivingLicense.disableIfExpired();
        DrivingLicenseDisabled domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseDisabled.class);

        assertDisabledDrivingLicense(drivingLicense);
        assertDrivingLicenseDisabledDomainEvent(domainEvent);
    }

    @Test
    public void testDisableDrivingLicenseIfRevocationExpired() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        drivingLicense.revoke(REVOCATION_END_DATE, JUDGMENT_FILE_ID);
        Revocation revocation = (Revocation) ReflectionTestUtils.getField(drivingLicense, "revocation");
        ReflectionTestUtils.setField(Objects.requireNonNull(revocation), "endDate", LocalDateTime.of(2019, 2, 14, 22, 45));
        ReflectionTestUtils.setField(Objects.requireNonNull(drivingLicense), "revocation", revocation);

        drivingLicense.disableIfRevocationExpired();
        DrivingLicenseRevocationExpired domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseRevocationExpired.class);

        assertDisabledDrivingLicense(drivingLicense);
        assertDrivingLicenseRevocationExpiredDomainEvent(domainEvent);
    }

    private void assertNewDrivingLicense(DrivingLicense drivingLicense, Set<Category> categories, List<Attachment> attachments) {
        DrivingLicense.Status status = (DrivingLicense.Status) ReflectionTestUtils.getField(drivingLicense, "status");
        DrivingLicenseId drivingLicenseId = (DrivingLicenseId) ReflectionTestUtils.getField(drivingLicense, "drivingLicenseId");
        PersonId personId = (PersonId) ReflectionTestUtils.getField(drivingLicense, "personId");
        LocalDateTime startDate = (LocalDateTime) ReflectionTestUtils.getField(drivingLicense, "startDate");
        LocalDateTime endDate = (LocalDateTime) ReflectionTestUtils.getField(drivingLicense, "endDate");
        Set<Category> aCategories = (Set<Category>) ReflectionTestUtils.getField(drivingLicense, "categories");
        List<Attachment> anAttachments = (List<Attachment>) ReflectionTestUtils.getField(drivingLicense, "attachments");
        Revocation revocation = (Revocation) ReflectionTestUtils.getField(drivingLicense, "revocation");
        IssuanceReason issuanceReason = (IssuanceReason) ReflectionTestUtils.getField(drivingLicense, "issuanceReason");

        assertDrivingLicenseId(drivingLicenseId);
        Assert.assertNotNull(status);
        Assert.assertEquals(status, DrivingLicense.Status.VALID);
        Assert.assertNotNull(aCategories);
        Assert.assertEquals(categories, aCategories);
        Assert.assertNotNull(personId);
        Assert.assertNotNull(startDate);
        Assert.assertNotNull(endDate);
        Assert.assertNotNull(anAttachments);
        Assert.assertEquals(attachments, anAttachments);
        Assert.assertNull(revocation);
        Assert.assertNotNull(issuanceReason);
        Assert.assertEquals(issuanceReason, IssuanceReason.FIRST_ISSUANCE);
    }

    private void assertDrivingLicenseRevocationExpiredDomainEvent(DrivingLicenseRevocationExpired domainEvent) {
        Assert.assertNotNull(domainEvent.getDrivingLicenseId());
        Assert.assertNotNull(domainEvent.getRevocationId());
    }

}
