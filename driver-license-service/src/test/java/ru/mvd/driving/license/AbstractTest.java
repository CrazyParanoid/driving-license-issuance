package ru.mvd.driving.license;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import ru.mvd.driving.license.domain.model.*;

import java.time.LocalDateTime;
import java.util.Objects;

import static ru.mvd.driving.license.TestValues.*;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public abstract class AbstractTest {
    @MockBean
    protected DrivingLicenseRepository drivingLicenseRepository;
    @Autowired
    protected DrivingLicenseFactory drivingLicenseFactory;

    @Before
    public void init() {
        Mockito.when(drivingLicenseRepository.nextIdentity(
                ArgumentMatchers.any(AreaCode.class)))
                .thenReturn(new DrivingLicenseId(SERIES, NUMBER));
    }

    protected void assertDrivingLicenseRevokedDomainEvent(DrivingLicenseRevoked domainEvent) {
        Assert.assertNotNull(domainEvent.getDrivingLicenseId());
        Assert.assertNotNull(domainEvent.getRevocationEndDate());
        Assert.assertEquals(domainEvent.getRevocationEndDate(), REVOCATION_END_DATE);
        Assert.assertNotNull(domainEvent.getRevocationStartDate());
        Assert.assertNotNull(domainEvent.getRevocationId());
    }

    protected void assertDrivingLicenseId(DrivingLicenseId drivingLicenseId) {
        String series = (String) ReflectionTestUtils.getField(drivingLicenseId, "series");
        String number = (String) ReflectionTestUtils.getField(drivingLicenseId, "number");

        Assert.assertNotNull(drivingLicenseId);
        Assert.assertEquals(series, SERIES);
        Assert.assertEquals(number, NUMBER);
    }

    protected void assertDrivingLicenseIssuedDomainEvent(DrivingLicenseIssued domainEvent) {
        Assert.assertNotNull(domainEvent);
        Assert.assertNotNull(domainEvent.getCategories());
        Assert.assertNotNull(domainEvent.getDepartmentId());
        Assert.assertEquals(domainEvent.getDepartmentId(), DEPARTMENT_ID);
        Assert.assertNotNull(domainEvent.getPersonId());
        Assert.assertEquals(domainEvent.getPersonId(), PERSON_ID);
        Assert.assertNotNull(domainEvent.getStartDate());
        Assert.assertNotNull(domainEvent.getEndDate());
        Assert.assertNotNull(domainEvent.getAttachments());
        Assert.assertNotNull(domainEvent.getIssuanceReason());
        Assert.assertEquals(domainEvent.getIssuanceReason(), ISSUANCE_REASON);
    }

    protected void assertDrivingLicenseRevocationProlongedDomainEvent(DrivingLicenseRevocationProlonged domainEvent) {
        Assert.assertNotNull(domainEvent.getDrivingLicenseId());
        Assert.assertNotNull(domainEvent.getRevocationEndDate());
        Assert.assertEquals(domainEvent.getRevocationEndDate(), PROLONGED_REVOCATION_END_DATE);
        Assert.assertNotNull(domainEvent.getRevocationId());
    }

    protected void assertDrivingLicenseDisabledDomainEvent(DrivingLicenseDisabled domainEvent) {
        Assert.assertNotNull(domainEvent.getDrivingLicenseId());
    }

    protected void assertProlongedRevokeDrivingLicense(DrivingLicense drivingLicense) {
        Revocation revocation = (Revocation) ReflectionTestUtils.getField(drivingLicense, "revocation");
        LocalDateTime revocationEndDate = (LocalDateTime) ReflectionTestUtils.getField(Objects.requireNonNull(revocation), "endDate");

        Assert.assertNotNull(revocation);
        Assert.assertNotNull(revocationEndDate);
        Assert.assertEquals(revocationEndDate, PROLONGED_REVOCATION_END_DATE);
    }

    protected void assertRevokeDrivingLicense(DrivingLicense drivingLicense) {
        Revocation revocation = (Revocation) ReflectionTestUtils.getField(drivingLicense, "revocation");
        DrivingLicense.Status status = (DrivingLicense.Status) ReflectionTestUtils.getField(drivingLicense, "status");
        RevocationId revocationId = (RevocationId) ReflectionTestUtils.getField(Objects.requireNonNull(revocation), "revocationId");
        LocalDateTime revocationStartDate = (LocalDateTime) ReflectionTestUtils.getField(Objects.requireNonNull(revocation), "startDate");
        LocalDateTime revocationEndDate = (LocalDateTime) ReflectionTestUtils.getField(Objects.requireNonNull(revocation), "endDate");
        Boolean expired = (Boolean) ReflectionTestUtils.getField(Objects.requireNonNull(revocation), "expired");

        Assert.assertNotNull(status);
        Assert.assertEquals(status, DrivingLicense.Status.REVOKED);
        Assert.assertNotNull(revocation);
        Assert.assertNotNull(revocationId);
        Assert.assertNotNull(revocationStartDate);
        Assert.assertEquals(revocationEndDate, REVOCATION_END_DATE);
        Assert.assertFalse(expired);
    }

    protected void assertDisabledDrivingLicense(DrivingLicense drivingLicense) {
        DrivingLicense.Status status = (DrivingLicense.Status) ReflectionTestUtils.getField(drivingLicense, "status");

        Assert.assertNotNull(status);
        Assert.assertEquals(status, DrivingLicense.Status.INVALID);
    }
}
