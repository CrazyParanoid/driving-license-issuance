package ru.mvd.driving.license.infrastructure.events.integration.publisher;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import ru.mvd.driving.license.domain.supertype.DomainEvent;
import ru.mvd.driving.license.infrastructure.events.StoredDomainEvent;
import ru.mvd.driving.license.infrastructure.events.StoredDomainEventRepository;
import ru.mvd.driving.license.infrastructure.events.integration.IntegrationEvent;
import ru.mvd.driving.license.infrastructure.events.integration.OutputChannelBindings;

import java.lang.reflect.Type;
import java.util.Optional;

public abstract class AbstractIntegrationEventPublisher<T extends DomainEvent, R extends IntegrationEvent> {
    private final StoredDomainEventRepository storedDomainEventRepository;
    protected final OutputChannelBindings outputChannelBindings;

    protected AbstractIntegrationEventPublisher(StoredDomainEventRepository storedDomainEventRepository,
                                                OutputChannelBindings outputChannelBindings) {
        this.storedDomainEventRepository = storedDomainEventRepository;
        this.outputChannelBindings = outputChannelBindings;
    }

    protected abstract void publishIntegrationEvent(R integrationEvent);

    protected abstract R convertToIntegrationEvent(T domainEvent);

    protected abstract String currentDomainEventType();

    @SneakyThrows
    public void findAndPublishDomainEvent() {
        Optional<StoredDomainEvent> optionalStoredDomainEvent = storedDomainEventRepository
                .findFirstByType(currentDomainEventType());
        if (optionalStoredDomainEvent.isPresent()) {
            StoredDomainEvent storedDomainEvent = optionalStoredDomainEvent.get();
            Class<?> clazz = Class.forName(currentDomainEventType());
            T domainEvent = new Gson().fromJson(storedDomainEvent.getPayload(), (Type) clazz);
            R integrationEvent = convertToIntegrationEvent(domainEvent);
            publishIntegrationEvent(integrationEvent);
            storedDomainEventRepository.delete(storedDomainEvent);
        }
    }

}
