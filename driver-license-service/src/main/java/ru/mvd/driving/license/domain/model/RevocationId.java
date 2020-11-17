package ru.mvd.driving.license.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.mvd.driving.license.domain.supertype.ValueObject;

import java.util.Objects;

@Getter(AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class RevocationId implements ValueObject {
    private String id;

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        RevocationId revocationId = (RevocationId) object;
        return Objects.equals(id, revocationId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
