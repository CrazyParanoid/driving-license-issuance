package ru.mvd.driving.license.infrastructure.persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import ru.mvd.driving.license.Application;
import ru.mvd.driving.license.config.MongoCustomizationConfiguration;
import ru.mvd.driving.license.domain.TestDomainObjectsFactory;
import ru.mvd.driving.license.domain.model.*;

import static ru.mvd.driving.license.TestValues.*;

@ActiveProfiles("it")
@RunWith(SpringRunner.class)
@Import(MongoCustomizationConfiguration.class)
@SpringBootTest(classes = Application.class)
public class DrivingLicenseRepositoryIT {
    @Autowired
    private TestDomainObjectsFactory testDomainObjectsFactory;
    @Autowired
    private DrivingLicenseRepository drivingLicenseRepository;

    private static final PersonId PERSON_ID = new PersonId("258890");
    private static final DrivingLicenseId DRIVING_LICENSE_ID = DrivingLicenseId.identifyFrom(FULL_NUMBER);
    private static final DrivingLicenseId SECOND_DRIVING_LICENSE_ID = DrivingLicenseId.identifyFrom("7700000002");
    private static final AreaCode areaCode = new AreaCode("77");

    @After
    public void after() {
        drivingLicenseRepository.deleteAll();
    }

    @Test
    public void testSaveAndFindByIdDrivingLicense() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        drivingLicenseRepository.save(drivingLicense);

        DrivingLicense foundDrivingLicense = drivingLicenseRepository.findByDrivingLicenseId(DRIVING_LICENSE_ID);
        Assert.assertNotNull(foundDrivingLicense);
    }

    @Test
    public void testFirstNextIdentity() {
        DrivingLicenseId drivingLicenseId = drivingLicenseRepository.nextIdentity(areaCode);

        Assert.assertNotNull(drivingLicenseId);
        Assert.assertEquals(drivingLicenseId, DRIVING_LICENSE_ID);
    }

    @Test
    public void testSecondNextIdentity() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        drivingLicenseRepository.save(drivingLicense);

        DrivingLicenseId drivingLicenseId = drivingLicenseRepository.nextIdentity(areaCode);

        Assert.assertNotNull(drivingLicenseId);
        Assert.assertEquals(drivingLicenseId, SECOND_DRIVING_LICENSE_ID);
    }

    @Test
    public void testFindDrivingLicenseByPersonId() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        drivingLicenseRepository.save(drivingLicense);

        DrivingLicense foundDrivingLicense = drivingLicenseRepository.findNotInvalidByPersonId(PERSON_ID);

        PersonId personId = (PersonId) ReflectionTestUtils.getField(drivingLicense, "personId");
        Assert.assertNotNull(foundDrivingLicense);
        Assert.assertEquals(personId, personId);
    }

    @Test
    public void testNotFoundDrivingLicenseById() {
        DrivingLicenseNotFoundException exception = Assert.assertThrows(DrivingLicenseNotFoundException.class, () ->
                drivingLicenseRepository.findByDrivingLicenseId(DRIVING_LICENSE_ID));

        String exceptionMessage = exception.getMessage();
        Assert.assertTrue(exceptionMessage.contains("DrivingLicense with id 7700000001 is not found"));
    }

    @Test
    public void testFindNextValidDrivingLicense() {
        DrivingLicense drivingLicense1 = testDomainObjectsFactory.newDrivingLicense();
        drivingLicenseRepository.save(drivingLicense1);
        DrivingLicense drivingLicense2 = testDomainObjectsFactory.newDrivingLicenseForNewPerson();
        drivingLicenseRepository.save(drivingLicense2);
        DrivingLicense foundDrivingLicense1 = drivingLicenseRepository.findNextValidDrivingLicense();
        drivingLicenseRepository.save(foundDrivingLicense1);

        DrivingLicense foundDrivingLicense2 = drivingLicenseRepository.findNextValidDrivingLicense();

        Assert.assertEquals(drivingLicense1, foundDrivingLicense1);
        Assert.assertEquals(drivingLicense2, foundDrivingLicense2);
    }

    @Test
    public void testFindNextRevokedDrivingLicense() {
        DrivingLicense drivingLicense1 = testDomainObjectsFactory.newDrivingLicense();
        drivingLicense1.revoke(REVOCATION_END_DATE, JUDGMENT_FILE_ID);
        drivingLicenseRepository.save(drivingLicense1);
        DrivingLicense drivingLicense2 = testDomainObjectsFactory.newDrivingLicenseForNewPerson();
        drivingLicense2.revoke(REVOCATION_END_DATE, JUDGMENT_FILE_ID);
        drivingLicenseRepository.save(drivingLicense2);
        DrivingLicense foundDrivingLicense1 = drivingLicenseRepository.findNextRevokedDrivingLicense();
        drivingLicenseRepository.save(foundDrivingLicense1);

        DrivingLicense foundDrivingLicense2 = drivingLicenseRepository.findNextRevokedDrivingLicense();

        Assert.assertEquals(drivingLicense1, foundDrivingLicense1);
        Assert.assertEquals(drivingLicense2, foundDrivingLicense2);
    }

    @Test
    public void testNotFoundNextValidDrivingLicense() {
        DrivingLicense drivingLicense = drivingLicenseRepository.findNextValidDrivingLicense();

        Assert.assertNull(drivingLicense);
    }

    @Test
    public void testNotFoundNextRevokedDrivingLicense() {
        DrivingLicense drivingLicense = drivingLicenseRepository.findNextRevokedDrivingLicense();

        Assert.assertNull(drivingLicense);
    }
}
