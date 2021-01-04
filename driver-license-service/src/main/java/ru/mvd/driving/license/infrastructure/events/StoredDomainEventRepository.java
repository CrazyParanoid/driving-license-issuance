package ru.mvd.driving.license.infrastructure.events;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StoredDomainEventRepository extends MongoRepository<StoredDomainEvent, Long> {

    Optional<StoredDomainEvent> findFirstByType(String type);

}
