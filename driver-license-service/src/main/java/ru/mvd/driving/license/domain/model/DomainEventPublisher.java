package ru.mvd.driving.license.domain.model;

import ru.mvd.driving.license.domain.supertype.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);

}
