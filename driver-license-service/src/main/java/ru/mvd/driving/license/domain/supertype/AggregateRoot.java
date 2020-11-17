package ru.mvd.driving.license.domain.supertype;

import java.util.List;

public abstract class AggregateRoot implements Entity{
    private List<DomainEvent> domainEvents;

    protected AggregateRoot(List<DomainEvent> domainEvents) {
        this.domainEvents = domainEvents;
    }

    public <T extends DomainEvent> void raiseDomainEvent(T domainEvent){
        this.domainEvents.add(domainEvent);
    }

    public <T extends DomainEvent> T getDomainEventByType(Class<T> clazz){
        return this.domainEvents.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findAny()
                .get();
    }

}
