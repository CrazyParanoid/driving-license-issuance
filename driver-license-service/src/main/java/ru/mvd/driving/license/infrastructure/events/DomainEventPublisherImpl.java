package ru.mvd.driving.license.infrastructure.events;

import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.mvd.driving.license.domain.model.DomainEventPublisher;
import ru.mvd.driving.license.domain.supertype.DomainEvent;

import java.util.List;

@Service
public class DomainEventPublisherImpl implements DomainEventPublisher {
    private final StoredDomainEventRepository storedDomainEventRepository;

    public DomainEventPublisherImpl(StoredDomainEventRepository storedDomainEventRepository) {
        this.storedDomainEventRepository = storedDomainEventRepository;
    }

    @Override
    public void publish(List<DomainEvent> domainEvents) {
        if (CollectionUtils.isNotEmpty(domainEvents)) {
            domainEvents.forEach(domainEvent -> {
                Gson gson = new Gson();
                StoredDomainEvent storedDomainEvent = new StoredDomainEvent(
                        gson.toJson(domainEvent),
                        domainEvent.getClass().getName());
                storedDomainEventRepository.save(storedDomainEvent);
            });
        }
    }
}
