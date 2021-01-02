package ru.mvd.driving.license.application;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
@ContextConfiguration(classes = {Application.class})
public class IssueDrivingLicenseCommandProcessorTest extends AbstractTest {
    @Autowired
    private TestCommandFactory testCommandFactory;
    @Autowired
    private CommandProcessor<IssueDrivingLicenseCommand, String> issueDrivingLicenseCommandProcessor;
    @MockBean
    private DomainEventPublisher<DrivingLicenseIssued> drivingLicenseIssuedDomainEventPublisher;
    @Captor
    private ArgumentCaptor<DrivingLicense> drivingLicenseArgumentCaptor;
    @Captor
    private ArgumentCaptor<DrivingLicenseIssued> drivingLicenseIssuedArgumentCaptor;

    @Test
    public void testIssueDrivingLicense() {
        IssueDrivingLicenseCommand command = testCommandFactory.createIssueDrivingLicenseCommand();

        String id = issueDrivingLicenseCommandProcessor.process(command);

        Mockito.verify(drivingLicenseRepository).save(drivingLicenseArgumentCaptor.capture());
        Mockito.verify(drivingLicenseIssuedDomainEventPublisher).publish(drivingLicenseIssuedArgumentCaptor.capture());
        DrivingLicense drivingLicense = drivingLicenseArgumentCaptor.getValue();
        DrivingLicenseIssued drivingLicenseIssuedDomainEvent = drivingLicenseIssuedArgumentCaptor.getValue();
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
        Mockito.when(drivingLicenseRepository.findByPersonId(ArgumentMatchers.any(PersonId.class)))
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
        LocalDate startDate = (LocalDate) ReflectionTestUtils.getField(drivingLicense, "startDate");
        LocalDate endDate = (LocalDate) ReflectionTestUtils.getField(drivingLicense, "endDate");
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
        Assert.assertEquals(endDate, startDate.plusYears(DrivingLicense.DRIVING_LICENSE_VALID_YEAR_PERIOD));
        Assert.assertNotNull(anAttachments);
        Assert.assertNull(revocation);
        Assert.assertNotNull(issuanceReason);
        Assert.assertEquals(issuanceReason, IssuanceReason.FIRST_ISSUANCE);
    }

}
