package ru.mvd.driving.license.application;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import ru.mvd.driving.license.AbstractTest;
import ru.mvd.driving.license.config.MongoCustomizationConfiguration;
import ru.mvd.driving.license.domain.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
@Import(MongoCustomizationConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IssueDrivingLicenseCommandProcessorTest extends AbstractTest {
    @Autowired
    private TestCommandFactory testCommandFactory;
    @Autowired
    private CommandProcessor<IssueDrivingLicenseCommand, String> issueDrivingLicenseCommandProcessor;
    @Captor
    private ArgumentCaptor<DrivingLicense> drivingLicenseArgumentCaptor;

    @Test
    public void testIssueDrivingLicense() {
        IssueDrivingLicenseCommand command = testCommandFactory.createIssueDrivingLicenseCommand();

        String id = issueDrivingLicenseCommandProcessor.process(command);

        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseIssued drivingLicenseIssuedDomainEvent = drivingLicense
                .getDomainEventByType(DrivingLicenseIssued.class);
        Assert.assertNotNull(id);
        assertNewDrivingLicense(drivingLicense);
        assertDrivingLicenseIssuedDomainEvent(drivingLicenseIssuedDomainEvent);
    }

    @Test
    public void testDeduplication() {
        IssueDrivingLicenseCommand command = testCommandFactory.createIssueDrivingLicenseCommand();
        issueDrivingLicenseCommandProcessor.process(command);
        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        Mockito.when(drivingLicenseRepository.findNotInvalidByPersonId(ArgumentMatchers.any(PersonId.class)))
                .thenReturn(drivingLicense);

        UnsupportedOperationException exception = Assert.assertThrows(UnsupportedOperationException.class,
                () -> issueDrivingLicenseCommandProcessor.process(command));

        String expectedMessage = "The person with id 258890 already has driving license";
        Assert.assertTrue(exception.getMessage().contains(expectedMessage));
    }

    private void assertNewDrivingLicense(DrivingLicense drivingLicense) {
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
        Assert.assertNotNull(personId);
        Assert.assertNotNull(startDate);
        Assert.assertNotNull(endDate);
        Assert.assertNotNull(anAttachments);
        Assert.assertNull(revocation);
        Assert.assertNotNull(issuanceReason);
        Assert.assertEquals(issuanceReason, IssuanceReason.FIRST_ISSUANCE);
    }

}
