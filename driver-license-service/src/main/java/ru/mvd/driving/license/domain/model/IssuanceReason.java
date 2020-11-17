package ru.mvd.driving.license.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum IssuanceReason {
    FIRST_ISSUANCE("FIRST_ISSUANCE"),
    PERSON_NAME_DETAILS_CHANGE("PERSON_NAME_DETAILS_CHANGE"),
    EXPIRATION("EXPIRATION"),
    NEW_CATEGORY_OPEN("NEW_CATEGORY_OPEN"),
    DAMAGE("DAMAGE"),
    UNKNOWN("UNKNOWN");

    @Getter(AccessLevel.PRIVATE)
    private final String name;

    static IssuanceReason fromName(String name){
        for(IssuanceReason issuanceReason: IssuanceReason.values()){
            if(issuanceReason.getName().equals(name))
                return issuanceReason;
        }
        return UNKNOWN;
    }
}
