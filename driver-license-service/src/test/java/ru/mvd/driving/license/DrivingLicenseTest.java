package ru.mvd.driving.license;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import ru.mvd.driving.license.domain.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@ActiveProfiles("test")
@ContextConfiguration(classes = {Application.class})
public class DrivingLicenseTest extends AbstractTest{

    @Test
    public void testCreateDrivingLicense(){
        Set<Category> categories = createCategories();
        List<Attachment> attachments = createAttachments();
        DrivingLicense drivingLicense = newDrivingLicense(categories, attachments);
        DrivingLicenseCreated domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseCreated.class);


        assertNewDrivingLicense(drivingLicense, categories, attachments);
        assertDrivingLicenseCreatedDomainEvent(domainEvent);
    }

    @Test
    public void testRevokeDrivingLicense(){
        Set<Category> categories = createCategories();
        List<Attachment> attachments = createAttachments();
        DrivingLicense drivingLicense = newDrivingLicense(categories, attachments);

        drivingLicense.revoke(REVOCATION_END_DATE, JUDGMENT_NUMBER);
        DrivingLicenseRevoked domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseRevoked.class);

        assertRevokeDrivingLicense(drivingLicense);
        assertDrivingLicenseRevokedDomainEvent(domainEvent);
    }

    @Test
    public void testProlongDrivingLicenseRevocation(){
        Set<Category> categories = createCategories();
        List<Attachment> attachments = createAttachments();
        DrivingLicense drivingLicense = newDrivingLicense(categories, attachments);
        drivingLicense.revoke(REVOCATION_END_DATE, JUDGMENT_NUMBER);

        drivingLicense.prolongRevocation(PROLONGED_REVOCATION_END_DATE);
        DrivingLicenseRevocationProlonged domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseRevocationProlonged.class);

        assertProlongedRevokeDrivingLicense(drivingLicense);
        assertDrivingLicenseRevocationProlongedDomainEvent(domainEvent);
    }

    @Test
    public void testDisableDrivingLicense(){
        Set<Category> categories = createCategories();
        List<Attachment> attachments = createAttachments();
        DrivingLicense drivingLicense = newDrivingLicense(categories, attachments);

        drivingLicense.disable();
        DrivingLicenseDisabled domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseDisabled.class);

        assertDisabledDrivingLicense(drivingLicense);
        assertDrivingLicenseDisabledDomainEvent(domainEvent);
    }

    @Test
    public void testDisableDrivingLicenseIfExpired(){
        Set<Category> categories = createCategories();
        List<Attachment> attachments = createAttachments();
        DrivingLicense drivingLicense = newDrivingLicense(categories, attachments);
        ReflectionTestUtils.setField(drivingLicense, "endDate", LocalDate.of(2019, 2, 14));

        drivingLicense.disableIfExpired();
        DrivingLicenseDisabled domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseDisabled.class);

        assertDisabledDrivingLicense(drivingLicense);
        assertDrivingLicenseDisabledDomainEvent(domainEvent);
    }

    @Test
    public void testDisableDrivingLicenseIfRevocationExpired(){
        Set<Category> categories = createCategories();
        List<Attachment> attachments = createAttachments();
        DrivingLicense drivingLicense = newDrivingLicense(categories, attachments);
        drivingLicense.revoke(REVOCATION_END_DATE, JUDGMENT_NUMBER);
        Revocation revocation = (Revocation) ReflectionTestUtils.getField(drivingLicense, "revocation");
        ReflectionTestUtils.setField(Objects.requireNonNull(revocation), "endDate", LocalDate.of(2019, 2, 14));
        ReflectionTestUtils.setField(Objects.requireNonNull(drivingLicense), "revocation", revocation);

        drivingLicense.disableIfRevocationExpired();
        DrivingLicenseRevocationExpired domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseRevocationExpired.class);

        assertDisabledDrivingLicense(drivingLicense);
        assertDrivingLicenseRevocationExpiredDomainEvent(domainEvent);
    }

    private void assertDisabledDrivingLicense(DrivingLicense drivingLicense){
        DrivingLicense.Status status = (DrivingLicense.Status) ReflectionTestUtils.getField(drivingLicense, "status");

        Assert.assertNotNull(status);
        Assert.assertEquals(status, DrivingLicense.Status.INVALID);
    }

    private void assertRevokeDrivingLicense(DrivingLicense drivingLicense){
        Revocation revocation = (Revocation) ReflectionTestUtils.getField(drivingLicense, "revocation");
        DrivingLicense.Status status = (DrivingLicense.Status) ReflectionTestUtils.getField(drivingLicense, "status");
        RevocationId revocationId = (RevocationId) ReflectionTestUtils.getField(Objects.requireNonNull(revocation), "revocationId");
        LocalDate revocationStartDate = (LocalDate) ReflectionTestUtils.getField(Objects.requireNonNull(revocation), "startDate");
        LocalDate revocationEndDate = (LocalDate) ReflectionTestUtils.getField(Objects.requireNonNull(revocation), "endDate");
        Boolean expired = (Boolean) ReflectionTestUtils.getField(Objects.requireNonNull(revocation), "expired");

        Assert.assertNotNull(status);
        Assert.assertEquals(status, DrivingLicense.Status.REVOKED);
        Assert.assertNotNull(revocation);
        Assert.assertNotNull(revocationId);
        Assert.assertNotNull(revocationStartDate);
        Assert.assertEquals(revocationStartDate, LocalDate.now());
        Assert.assertEquals(revocationEndDate, REVOCATION_END_DATE);
        Assert.assertFalse(expired);
    }

    private void assertProlongedRevokeDrivingLicense(DrivingLicense drivingLicense){
        Revocation revocation = (Revocation) ReflectionTestUtils.getField(drivingLicense, "revocation");
        LocalDate revocationEndDate = (LocalDate) ReflectionTestUtils.getField(Objects.requireNonNull(revocation), "endDate");

        Assert.assertNotNull(revocation);
        Assert.assertNotNull(revocationEndDate);
        Assert.assertEquals(revocationEndDate, PROLONGED_REVOCATION_END_DATE);
    }

    private void assertDrivingLicenseRevokedDomainEvent(DrivingLicenseRevoked domainEvent){
        Assert.assertNotNull(domainEvent.getDrivingLicenseId());
        Assert.assertNotNull(domainEvent.getRevocationEndDate());
        Assert.assertEquals(domainEvent.getRevocationEndDate(), REVOCATION_END_DATE);
        Assert.assertNotNull(domainEvent.getRevocationStartDate());
        Assert.assertEquals(domainEvent.getRevocationStartDate(), LocalDate.now());
        Assert.assertNotNull(domainEvent.getRevocationId());
    }

    private void  assertNewDrivingLicense(DrivingLicense drivingLicense, Set<Category> categories, List<Attachment> attachments){
        DrivingLicense.Status status = (DrivingLicense.Status) ReflectionTestUtils.getField(drivingLicense, "status");
        DrivingLicenseId drivingLicenseId = (DrivingLicenseId) ReflectionTestUtils.getField(drivingLicense, "drivingLicenseId");
        PersonId personId = (PersonId) ReflectionTestUtils.getField(drivingLicense, "personId");
        LocalDate startDate = (LocalDate) ReflectionTestUtils.getField(drivingLicense, "startDate");
        LocalDate endDate = (LocalDate) ReflectionTestUtils.getField(drivingLicense, "endDate");
        Set<Category> aCategories = (Set<Category>) ReflectionTestUtils.getField(drivingLicense, "categories");
        List<Attachment> anAttachments = (List<Attachment>) ReflectionTestUtils.getField(drivingLicense, "attachments");
        Revocation revocation = (Revocation) ReflectionTestUtils.getField(drivingLicense, "revocation");
        IssuanceReason issuanceReason = (IssuanceReason) ReflectionTestUtils.getField(drivingLicense, "issuanceReason");

        Assert.assertNotNull(status);
        Assert.assertEquals(status, DrivingLicense.Status.VALID);
        Assert.assertNotNull(aCategories);
        Assert.assertEquals(categories, aCategories);
        Assert.assertNotNull(drivingLicenseId);
        Assert.assertNotNull(personId);
        Assert.assertNotNull(startDate);
        Assert.assertNotNull(endDate);
        Assert.assertEquals(endDate, startDate.plusYears(DrivingLicense.DRIVING_LICENSE_VALID_YEAR_PERIOD));
        Assert.assertNotNull(anAttachments);
        Assert.assertEquals(attachments, anAttachments);
        Assert.assertNull(revocation);
        Assert.assertNotNull(issuanceReason);
        Assert.assertEquals(issuanceReason, IssuanceReason.FIRST_ISSUANCE);
    }

    private void assertDrivingLicenseCreatedDomainEvent(DrivingLicenseCreated domainEvent){
        Assert.assertNotNull(domainEvent);
        Assert.assertNotNull(domainEvent.getCategories());
        Assert.assertNotNull(domainEvent.getDepartmentId());
        Assert.assertEquals(domainEvent.getDepartmentId(), DEPARTMENT_ID);
        Assert.assertNotNull(domainEvent.getPersonId());
        Assert.assertEquals(domainEvent.getPersonId(), PERSON_ID);
        Assert.assertNotNull(domainEvent.getStartDate());
        Assert.assertNotNull(domainEvent.getEndDate());
    }

    private void assertDrivingLicenseRevocationProlongedDomainEvent(DrivingLicenseRevocationProlonged domainEvent){
        Assert.assertNotNull(domainEvent.getDrivingLicenseId());
        Assert.assertNotNull(domainEvent.getRevocationEndDate());
        Assert.assertEquals(domainEvent.getRevocationEndDate(), PROLONGED_REVOCATION_END_DATE);
        Assert.assertNotNull(domainEvent.getRevocationId());
    }

    private void assertDrivingLicenseDisabledDomainEvent(DrivingLicenseDisabled domainEvent){
        Assert.assertNotNull(domainEvent.getDrivingLicenseId());
    }

    private void assertDrivingLicenseRevocationExpiredDomainEvent(DrivingLicenseRevocationExpired domainEvent){
        Assert.assertNotNull(domainEvent.getDrivingLicenseId());
        Assert.assertNotNull(domainEvent.getRevocationId());
    }

}
