package com.app.measurementservice.repository;

import com.app.measurementservice.entity.QuantityMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, Long> {

    // User-scoped queries (used by service layer)
    List<QuantityMeasurementEntity> findByUserIdAndOperation(Long userId, String operation);
    List<QuantityMeasurementEntity> findByUserIdAndThisMeasurementType(Long userId, String measurementType);
    long countByUserIdAndOperationAndErrorFalse(Long userId, String operation);

    // Global queries (used by admin views and tests)
    List<QuantityMeasurementEntity> findByErrorTrue();
    List<QuantityMeasurementEntity> findByOperation(String operation);
    List<QuantityMeasurementEntity> findByThisMeasurementType(String measurementType);
    List<QuantityMeasurementEntity> findByCreatedAtAfter(LocalDateTime date);
    long countByOperationAndErrorFalse(String operation);

    @Query("SELECT q FROM QuantityMeasurementEntity q WHERE q.operation = :operation AND q.error = false")
    List<QuantityMeasurementEntity> findSuccessfulByOperation(@Param("operation") String operation);
}
