package ru.mvd.driving.license.infrastructure.events;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import ru.mvd.driving.license.Application;
import ru.mvd.driving.license.config.MongoCustomizationConfiguration;
import ru.mvd.driving.license.domain.TestDomainObjectsFactory;
import ru.mvd.driving.license.domain.model.*;
import ru.mvd.driving.license.domain.supertype.DomainEvent;
import ru.mvd.driving.license.infrastructure.events.integration.*;
import ru.mvd.driving.license.infrastructure.events.integration.publisher.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static ru.mvd.driving.license.TestValues.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(MongoCustomizationConfiguration.class)
@EmbeddedKafka(brokerProperties = "log.dir=target/${random.uuid}/embedded-kafka")
public class IntegrationEventsPublishingTest {
    @Autowired
    private DomainEventPublisher domainEventPublisher;
    @Autowired
    private TestDomainObjectsFactory testDomainObjectsFactory;
    @Autowired
    private DrivingLicenseIssuedPublisher drivingLicenseIssuedPublisher;
    @Autowired
    private DrivingLicenseRevokedPublisher drivingLicenseRevokedPublisher;
    @Autowired
    private DrivingLicenseRevocationExpiredPublisher drivingLicenseRevocationExpiredPublisher;
    @Autowired
    private DrivingLicenseDisabledPublisher drivingLicenseDisabledPublisher;
    @Autowired
    private DrivingLicenseRevocationProlongedPublisher drivingLicenseRevocationProlongedPublisher;

    @MockBean
    private IntegrationEventHandler integrationEventHandler;

    @Test
    public void testPublishDrivingLicenseIssued() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        List<DomainEvent> domainEvents = drivingLicense.getDomainEvents();
        DrivingLicenseIssued domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseIssued.class);
        ArgumentCaptor<DrivingLicenseIssuedIntegrationEvent> argumentCaptor =
                ArgumentCaptor.forClass(DrivingLicenseIssuedIntegrationEvent.class);

        domainEventPublisher.publish(domainEvents);

        Awaitility.await()
                .atMost(6, TimeUnit.SECONDS)
                .untilAsserted(() -> drivingLicenseIssuedPublisher.findAndPublishDomainEvent());
        Mockito.verify(integrationEventHandler).handle(argumentCaptor.capture());
        DrivingLicenseIssuedIntegrationEvent integrationEvent = argumentCaptor.getValue();
        assertDrivingLicenseIssuedIntegrationEvent(integrationEvent, domainEvent);
    }

    @Test
    public void testPublishDrivingLicenceRevoked() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        drivingLicense.revoke(REVOCATION_END_DATE, JUDGMENT_FILE_ID);
        List<DomainEvent> domainEvents = drivingLicense.getDomainEvents();
        DrivingLicenseRevoked domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseRevoked.class);
        ArgumentCaptor<DrivingLicenseRevokedIntegrationEvent> argumentCaptor =
                ArgumentCaptor.forClass(DrivingLicenseRevokedIntegrationEvent.class);

        domainEventPublisher.publish(domainEvents);

        Awaitility.await()
                .atMost(6, TimeUnit.SECONDS)
                .untilAsserted(() -> drivingLicenseRevokedPublisher.findAndPublishDomainEvent());
        Mockito.verify(integrationEventHandler).handle(argumentCaptor.capture());
        DrivingLicenseRevokedIntegrationEvent integrationEvent = argumentCaptor.getValue();
        assertDrivingLicenseRevokedIntegrationEvent(integrationEvent, domainEvent);
    }

    @Test
    public void testPublishDrivingLicenseDisabledIntegrationEvent() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        drivingLicense.disable();
        List<DomainEvent> domainEvents = drivingLicense.getDomainEvents();
        DrivingLicenseDisabled domainEvent = drivingLicense.getDomainEventByType(DrivingLicenseDisabled.class);
        ArgumentCaptor<DrivingLicenseDisabledIntegrationEvent> argumentCaptor =
                ArgumentCaptor.forClass(DrivingLicenseDisabledIntegrationEvent.class);

        domainEventPublisher.publish(domainEvents);

        Awaitility.await()
                .atMost(6, TimeUnit.SECONDS)
                .untilAsserted(() -> drivingLicenseDisabledPublisher.findAndPublishDomainEvent());
        Mockito.verify(integrationEventHandler).handle(argumentCaptor.capture());
        DrivingLicenseDisabledIntegrationEvent integrationEvent = argumentCaptor.getValue();
        assertDrivingLicenseDisabledIntegrationEvent(integrationEvent, domainEvent);
    }

    @Test
    public void testPublishDrivingLicenseRevocationProlongedIntegrationEvent() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        drivingLicense.revoke(REVOCATION_END_DATE, JUDGMENT_FILE_ID);
        drivingLicense.prolongRevocation(PROLONGED_REVOCATION_END_DATE);
        List<DomainEvent> domainEvents = drivingLicense.getDomainEvents();
        DrivingLicenseRevocationProlonged domainEvent = drivingLicense
                .getDomainEventByType(DrivingLicenseRevocationProlonged.class);
        ArgumentCaptor<DrivingLicenseRevocationProlongedIntegrationEvent> argumentCaptor =
                ArgumentCaptor.forClass(DrivingLicenseRevocationProlongedIntegrationEvent.class);

        domainEventPublisher.publish(domainEvents);

        Awaitility.await()
                .atMost(6, TimeUnit.SECONDS)
                .untilAsserted(() -> drivingLicenseRevocationProlongedPublisher
                        .findAndPublishDomainEvent());
        Mockito.verify(integrationEventHandler).handle(argumentCaptor.capture());
        DrivingLicenseRevocationProlongedIntegrationEvent integrationEvent = argumentCaptor.getValue();
        assertDrivingLicenseRevocationProlongedIntegrationEvent(integrationEvent, domainEvent);
    }

    @Test
    public void testPublishDrivingLicenseRevocationExpiredIntegrationEvent() {
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        drivingLicense.revoke(REVOCATION_END_DATE, JUDGMENT_FILE_ID);
        Revocation revocation = (Revocation) ReflectionTestUtils.getField(drivingLicense, "revocation");
        ReflectionTestUtils.setField(Objects.requireNonNull(revocation), "endDate", LocalDateTime.of(2019, 2, 14, 22, 45));
        ReflectionTestUtils.setField(Objects.requireNonNull(drivingLicense), "revocation", revocation);
        drivingLicense.disableIfRevocationExpired();

        List<DomainEvent> domainEvents = drivingLicense.getDomainEvents();
        DrivingLicenseRevocationExpired domainEvent = drivingLicense
                .getDomainEventByType(DrivingLicenseRevocationExpired.class);
        ArgumentCaptor<DrivingLicenseRevocationExpiredIntegrationEvent> argumentCaptor =
                ArgumentCaptor.forClass(DrivingLicenseRevocationExpiredIntegrationEvent.class);

        domainEventPublisher.publish(domainEvents);

        Awaitility.await()
                .atMost(6, TimeUnit.SECONDS)
                .untilAsserted(() -> drivingLicenseRevocationExpiredPublisher
                        .findAndPublishDomainEvent());
        Mockito.verify(integrationEventHandler).handle(argumentCaptor.capture());
        DrivingLicenseRevocationExpiredIntegrationEvent integrationEvent = argumentCaptor.getValue();
        assertDrivingLicenseRevocationExpiredIntegrationEvent(integrationEvent, domainEvent);
    }

    private void assertDrivingLicenseIssuedIntegrationEvent(DrivingLicenseIssuedIntegrationEvent integrationEvent,
                                                            DrivingLicenseIssued domainEvent) {
        Assert.assertNotNull(integrationEvent);
        Assert.assertEquals(integrationEvent.getDrivingLicenseId(), domainEvent.getDrivingLicenseId());
        Assert.assertEquals(integrationEvent.getDepartmentId(), domainEvent.getDepartmentId());
        Assert.assertEquals(integrationEvent.getEndDate(), domainEvent.getEndDate());
        Assert.assertEquals(integrationEvent.getStartDate(), domainEvent.getStartDate());
        Assert.assertEquals(integrationEvent.getSpecialMarks(), domainEvent.getSpecialMarks());
        Assert.assertEquals(integrationEvent.getPersonId(), domainEvent.getPersonId());
        Assert.assertEquals(integrationEvent.getIssuanceReason(), domainEvent.getIssuanceReason());
        integrationEvent.getAttachments().forEach(integrationAttachment -> {
            long count = domainEvent.getAttachments().stream()
                    .filter(attachment -> attachment.getFileId().equals(integrationAttachment.getFileId())
                            && attachment.attachmentTypeToString().equals(integrationAttachment.getType())
                    )
                    .count();
            Assert.assertEquals(count, 1);
        });
        integrationEvent.getCategories().forEach(integrationCategory -> {
            long count = domainEvent.getCategories().stream()
                    .filter(category ->
                            category.typeToString().equals(integrationCategory.getType())
                                    && category.specialMarksToStrings().equals(integrationCategory.getSpecialMarks())
                                    && category.getEndDate().equals(integrationCategory.getEndDate())
                                    && category.getStartDate().equals(integrationCategory.getStartDate())
                    )
                    .count();
            Assert.assertEquals(count, 1);
        });
    }

    private void assertDrivingLicenseRevokedIntegrationEvent(DrivingLicenseRevokedIntegrationEvent integrationEvent,
                                                             DrivingLicenseRevoked domainEvent) {
        Assert.assertNotNull(integrationEvent);
        Assert.assertEquals(integrationEvent.getDrivingLicenseId(), domainEvent.getDrivingLicenseId());
        Assert.assertEquals(integrationEvent.getRevocationEndDate(), domainEvent.getRevocationEndDate());
        Assert.assertEquals(integrationEvent.getRevocationStartDate(), domainEvent.getRevocationStartDate());
        Assert.assertEquals(integrationEvent.getRevocationId(), domainEvent.getRevocationId());
    }

    private void assertDrivingLicenseDisabledIntegrationEvent(DrivingLicenseDisabledIntegrationEvent integrationEvent,
                                                              DrivingLicenseDisabled domainEvent) {
        Assert.assertNotNull(integrationEvent);
        Assert.assertEquals(integrationEvent.getDrivingLicenseId(), domainEvent.getDrivingLicenseId());
    }

    private void assertDrivingLicenseRevocationProlongedIntegrationEvent(DrivingLicenseRevocationProlongedIntegrationEvent integrationEvent,
                                                                         DrivingLicenseRevocationProlonged domainEvent){
        Assert.assertNotNull(integrationEvent);
        Assert.assertEquals(integrationEvent.getDrivingLicenseId(), domainEvent.getDrivingLicenseId());
        Assert.assertEquals(integrationEvent.getRevocationId(), domainEvent.getRevocationId());
        Assert.assertEquals(integrationEvent.getRevocationEndDate(), domainEvent.getRevocationEndDate());
    }

    private void assertDrivingLicenseRevocationExpiredIntegrationEvent(DrivingLicenseRevocationExpiredIntegrationEvent integrationEvent,
                                                                       DrivingLicenseRevocationExpired domainEvent){
        Assert.assertNotNull(integrationEvent);
        Assert.assertEquals(integrationEvent.getDrivingLicenseId(), domainEvent.getDrivingLicenseId());
        Assert.assertEquals(integrationEvent.getRevocationId(), domainEvent.getRevocationId());
    }
}
