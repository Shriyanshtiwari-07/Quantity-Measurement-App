package com.app.measurementservice.service;

import com.app.measurementservice.dto.response.QuantityDTO;
import com.app.measurementservice.dto.request.QuantityMeasurementDTO;

import java.util.List;

public interface IQuantityMeasurementService {

    QuantityMeasurementDTO compare(QuantityDTO thisQ, QuantityDTO thatQ, Long userId);

    QuantityMeasurementDTO convert(QuantityDTO thisQ, QuantityDTO thatQ, Long userId);

    QuantityMeasurementDTO add(QuantityDTO thisQ, QuantityDTO thatQ, Long userId);

    QuantityMeasurementDTO add(QuantityDTO thisQ, QuantityDTO thatQ, QuantityDTO targetQ, Long userId);

    QuantityMeasurementDTO subtract(QuantityDTO thisQ, QuantityDTO thatQ, Long userId);

    QuantityMeasurementDTO subtract(QuantityDTO thisQ, QuantityDTO thatQ, QuantityDTO targetQ, Long userId);

    QuantityMeasurementDTO divide(QuantityDTO thisQ, QuantityDTO thatQ, Long userId);

    List<QuantityMeasurementDTO> getHistoryByOperation(String operation, Long userId);

    List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType, Long userId);

    long getOperationCount(String operation, Long userId);

    List<QuantityMeasurementDTO> getErrorHistory();
}
