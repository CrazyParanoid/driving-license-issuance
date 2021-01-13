package ru.mvd.driving.license.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import ru.mvd.driving.license.domain.supertype.ValueObject;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.mvd.driving.license.domain.model.Category.CategoryType.Unknown;

@Getter(AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Category implements ValueObject {
    @Getter
    private LocalDate startDate;
    @Getter
    private LocalDate endDate;
    private CategoryType type;
    private Set<DrivingLicense.SpecialMark> specialMarks;

    public String typeToString(){
        return this.type.getName();
    }

    public Set<String> specialMarksToStrings() {
        if (CollectionUtils.isNotEmpty(this.specialMarks)) {
            return specialMarks.stream()
                    .map(DrivingLicense.SpecialMark::getName)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public static Category open(String aCategoryType, LocalDate startDate,
                                LocalDate endDate, Set<String> aSpecialMarks) {
        Category.CategoryType categoryType = Category.CategoryType.fromName(aCategoryType);
        if (categoryType == Unknown)
            throw new IllegalArgumentException(String.format("Unknown category type %s", aCategoryType));
        Set<DrivingLicense.SpecialMark> specialMarks = DrivingLicense.SpecialMark.setFrom(aSpecialMarks);
        return new Category(startDate, endDate, categoryType, specialMarks);
    }

    public static Category open(CategoryType categoryType, LocalDate startDate,
                                LocalDate endDate, Set<DrivingLicense.SpecialMark> specialMarks) {
        return new Category(startDate, endDate, categoryType, specialMarks);
    }

    @RequiredArgsConstructor
    public enum CategoryType {
        A("A"),
        A1("A1"),
        B("B"),
        BE("BE"),
        B1("B1"),
        C("C"),
        CE("CE"),
        C1("C1"),
        C1E("C1E"),
        D("D"),
        DE("DE"),
        D1("D1"),
        D1E("D1E"),
        M("M"),
        Tm("Tm"),
        Tb("Tb"),
        Unknown("Unknown");

        @Getter(AccessLevel.PACKAGE)
        private final String name;

        public static CategoryType fromName(String name) {
            for (CategoryType categoryType : CategoryType.values()) {
                if (categoryType.getName().equals(name))
                    return categoryType;
            }
            return Unknown;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Category category = (Category) object;
        return type == category.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
