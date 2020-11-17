package ru.mvd.driving.license.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.mvd.driving.license.domain.supertype.Entity;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import static ru.mvd.driving.license.domain.model.Category.CategoryType.Unknown;

@Getter(AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Category implements Entity {
    private LocalDate startDate;
    private LocalDate endDate;
    private CategoryType type;
    private Set<DrivingLicense.SpecialMark> specialMarks;

    public static Category open(String aCategoryType, LocalDate startDate,
                                LocalDate endDate, Set<String> aSpecialMarks){
        Category.CategoryType categoryType = Category.CategoryType.fromName(aCategoryType);
        if(categoryType == Unknown)
            throw new IllegalArgumentException(String.format("Unknown category type %s", aCategoryType));
        Set<DrivingLicense.SpecialMark> specialMarks = DrivingLicense.SpecialMark.setFrom(aSpecialMarks);
        return new Category(startDate, endDate, categoryType, specialMarks);
    }

    public static Category open(CategoryType categoryType, LocalDate startDate,
                                LocalDate endDate, Set<DrivingLicense.SpecialMark> specialMarks){
        return new Category(startDate, endDate, categoryType, specialMarks);
    }

    @RequiredArgsConstructor
    public enum CategoryType{
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

        public static CategoryType fromName(String name){
            for(CategoryType categoryType: CategoryType.values()){
                if(categoryType.getName().equals(name))
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
