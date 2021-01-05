package ru.mvd.driving.license.domain.supertype;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AggregateRoot extends IdentifiedDomainObject implements Entity {
    @Getter
    @Transient
    protected List<DomainEvent> domainEvents = new ArrayList<>();

    public <R extends DomainEvent> void registerDomainEvent(R domainEvent) {
        this.domainEvents.add(domainEvent);
    }

    public <R extends DomainEvent> R getDomainEventByType(Class<R> clazz) {
        return this.domainEvents.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findAny()
                .orElse(null);
    }

}
