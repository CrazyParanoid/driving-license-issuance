package ru.mvd.driving.license.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.mvd.driving.license.domain.supertype.Entity;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Revocation implements Entity {
    @Getter(AccessLevel.PACKAGE)
    private RevocationId revocationId;
    @Getter(AccessLevel.PACKAGE)
    private LocalDateTime startDate;
    @Getter(AccessLevel.PACKAGE)
    private LocalDateTime endDate;
    private boolean expired;

    boolean isExpired() {
        return this.expired;
    }

    void defineExpiration() {
        if (!isExpired()) {
            LocalDateTime currentDate = LocalDateTime.now();
            this.expired = this.endDate.isEqual(currentDate) || currentDate.isAfter(this.endDate);
        }
    }

    void calculateTerm(LocalDateTime endDate) {
        this.endDate = endDate;
        this.expired = false;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Revocation revocation = (Revocation) object;
        return Objects.equals(revocationId, revocation.revocationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(revocationId);
    }
}
