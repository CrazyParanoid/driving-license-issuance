package ru.mvd.driving.license.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.mvd.driving.license.domain.model.DrivingLicense;
import ru.mvd.driving.license.domain.model.DrivingLicenseId;
import ru.mvd.driving.license.domain.model.PersonId;

import java.util.List;
import java.util.Optional;

interface DrivingLicenseMongoRepository extends MongoRepository<DrivingLicense, Long> {

    Optional<DrivingLicense> findByDrivingLicenseId(DrivingLicenseId drivingLicenseId);

    Optional<DrivingLicense> findByStatusInAndPersonId(List<DrivingLicense.Status> statuses, PersonId personId);

    Optional<DrivingLicense> findTopByStatusOrderByUpdatedAtAsc(DrivingLicense.Status status);

    Optional<DrivingLicense> findFirstByDrivingLicenseIdSeriesOrderByDrivingLicenseIdNumberDesc(String series);

}
