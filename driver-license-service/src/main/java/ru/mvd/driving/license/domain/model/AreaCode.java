package ru.mvd.driving.license.domain.model;

import lombok.AllArgsConstructor;
import ru.mvd.driving.license.domain.supertype.ValueObject;

import java.util.Objects;

@AllArgsConstructor
public class AreaCode implements ValueObject {
    private String code;

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        AreaCode areaCode = (AreaCode) object;
        return Objects.equals(code, areaCode.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
