package ru.mvd.driving.license.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.mvd.driving.license.domain.supertype.ValueObject;

import java.util.Objects;

@Getter(AccessLevel.PACKAGE)
@AllArgsConstructor
public class DepartmentId implements ValueObject {
    private String id;

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        DepartmentId departmentId = (DepartmentId) object;
        return Objects.equals(id, departmentId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
