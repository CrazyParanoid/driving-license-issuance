package ru.mvd.driving.license;

import java.time.LocalDateTime;

public abstract class TestValues {
    public static final String SERIES = "9902";
    public static final String NUMBER = "789067";
    public static final String DEPARTMENT_ID = "113667";
    public static final String PERSON_ID = "258890";
    public static final String ISSUANCE_REASON = "FIRST_ISSUANCE";
    public static final String AREA_CODE = "77";
    public static final LocalDateTime REVOCATION_END_DATE = LocalDateTime.of(2030, 3, 12, 22, 45);
    public static final LocalDateTime PROLONGED_REVOCATION_END_DATE = LocalDateTime.of(2032, 6, 24, 22, 45);
    public static final String JUDGMENT_FILE_ID = "43367812";
}
