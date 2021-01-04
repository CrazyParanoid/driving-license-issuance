package ru.mvd.driving.license.domain.supertype;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class IdentifiedDomainObject extends Auditable{
    @Id
    private String id;
}
