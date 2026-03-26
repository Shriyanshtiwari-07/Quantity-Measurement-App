package com.app.quantity_measurement_app.repository;

import java.time.LocalDateTime;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.quantity_measurement_app.model.QuantityMeasurementEntity;

@Repository  // marks this class as a Spring data repository
public interface QuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, Long> {
	
	// method will automatically generate a query to find all QuantityMeasurementEntity records
	List<QuantityMeasurementEntity> findByOperation(String operation);
	
	// method will generate a query to find all records where the thisMeasurementType field matches the provided value
	List<QuantityMeasurementEntity> findByThisMeasurementType(String measurementType);
	
	// method will generate a query to find all records where the createdAt field is after the specified date
	List<QuantityMeasurementEntity> findByCreatedAtAfter(LocalDateTime date);
	
	// Custom JPL Query for complex operations
	@Query("SELECT e FROM QuantityMeasurementEntity e WHERE e.operation = :operation " + "AND e.isError = false")
	List<QuantityMeasurementEntity> findSuccessfullOperations(
			@Param("operation") String operation		
	);
	
	
	// method will generate a query to count the number of records where the operation matches the provided value and isError is false
	long countByOperationAndIsErrorFalse(String operation);
	
	// method will generate a query to find all records where the isError field is true.
	List<QuantityMeasurementEntity> findByIsErrorTrue();
}
