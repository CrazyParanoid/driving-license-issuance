package ru.mvd.driving.license.infrastructure.events;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "stored-events")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoredDomainEvent {
    @Id
    private String id;
    private String payload;
    private String type;

    StoredDomainEvent(String payload, String type) {
        this.payload = payload;
        this.type = type;
    }
}
