package ru.mvd.driving.license.infrastructure.persistence;

import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mvd.driving.license.domain.model.*;

import java.util.Optional;

@Slf4j
@Component
public class DrivingLicenseRepositoryImpl implements DrivingLicenseRepository {
    private final DrivingLicenseMongoRepository drivingLicenseMongoRepository;

    @Autowired
    public DrivingLicenseRepositoryImpl(DrivingLicenseMongoRepository drivingLicenseMongoRepository) {
        this.drivingLicenseMongoRepository = drivingLicenseMongoRepository;
    }

    @Override
    public DrivingLicense findByDrivingLicenseId(DrivingLicenseId drivingLicenseId) {
        try {
            return drivingLicenseMongoRepository.findByDrivingLicenseId(drivingLicenseId)
                    .orElseThrow(() -> new DrivingLicenseNotFoundException(
                            String.format("DrivingLicense with id %s is not found", drivingLicenseId.toFullNumber()))
                    );
        } catch (MongoException ex) {
            String exceptionMessage = ex.getMessage();
            log.error(exceptionMessage, ex);
            throw new RepositoryAccessException("Service unavailable", ex);
        }
    }

    @Override
    public DrivingLicense findByPersonId(PersonId personId) {
        try {
            return drivingLicenseMongoRepository.findByPersonId(personId)
                    .orElseThrow(() -> new DrivingLicenseNotFoundException(
                            String.format("DrivingLicense for person with id %s is not found", personId.getId()))
                    );
        } catch (MongoException ex) {
            String exceptionMessage = ex.getMessage();
            log.error(exceptionMessage, ex);
            throw new RepositoryAccessException("Service unavailable", ex);
        }
    }

    @Override
    public DrivingLicense findNextRevokedDrivingLicense() {
        try {
            return drivingLicenseMongoRepository.findTopByStatusOrderByUpdatedAtAsc(DrivingLicense.Status.REVOKED)
                    .orElse(null);
        } catch (MongoException ex) {
            String exceptionMessage = ex.getMessage();
            log.error(exceptionMessage, ex);
            throw new RepositoryAccessException("Service unavailable", ex);
        }
    }

    @Override
    public DrivingLicense findNextValidDrivingLicense() {
        try {
            return drivingLicenseMongoRepository.findTopByStatusOrderByUpdatedAtAsc(DrivingLicense.Status.VALID)
                    .orElse(null);
        } catch (MongoException ex) {
            String exceptionMessage = ex.getMessage();
            log.error(exceptionMessage, ex);
            throw new RepositoryAccessException("Service unavailable", ex);
        }
    }

    @Override
    public void save(DrivingLicense drivingLicense) {
        try {
            drivingLicenseMongoRepository.save(drivingLicense);
        } catch (MongoException ex) {
            String exceptionMessage = ex.getMessage();
            log.error(exceptionMessage, ex);
            throw new RepositoryAccessException("Service unavailable", ex);
        }
    }

    @Override
    public DrivingLicenseId nextIdentity(AreaCode areaCode) {
        try {
            Optional<DrivingLicense> optionalDrivingLicense = drivingLicenseMongoRepository
                    .findByDrivingLicenseIdSeries(areaCode.formatCode());
            if(optionalDrivingLicense.isPresent()){
                DrivingLicense drivingLicense = optionalDrivingLicense.get();
                DrivingLicenseId drivingLicenseId = drivingLicense.getDrivingLicenseId();
                return drivingLicenseId.nextId();
            }
            return DrivingLicenseId.newDrivingLicenseId(areaCode);
        } catch (MongoException ex) {
            String exceptionMessage = ex.getMessage();
            log.error(exceptionMessage, ex);
            throw new RepositoryAccessException("Service unavailable", ex);
        }
    }

    @Override
    public void deleteAll() {
        try {
            drivingLicenseMongoRepository.deleteAll();
        } catch (MongoException ex) {
            String exceptionMessage = ex.getMessage();
            log.error(exceptionMessage, ex);
            throw new RepositoryAccessException("Service unavailable", ex);
        }
    }

}
