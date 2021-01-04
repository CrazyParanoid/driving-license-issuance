package ru.mvd.driving.license.domain.model;

import ru.mvd.driving.license.domain.supertype.DomainEvent;

import java.util.List;

public interface DomainEventPublisher {

    void publish(List<DomainEvent> domainEvents);

}
