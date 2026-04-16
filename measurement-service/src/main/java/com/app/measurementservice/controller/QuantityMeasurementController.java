package com.app.measurementservice.controller;

import lombok.extern.slf4j.Slf4j;

import com.app.measurementservice.dto.request.*;
import com.app.measurementservice.service.IQuantityMeasurementService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * QuantityMeasurementController
 *
 * REST controller for all quantity measurement operations.
 *
 * Authentication is handled centrally by the API Gateway. The gateway validates
 * the JWT and injects X-User-Id, X-User-Email, and X-User-Role headers into
 * the forwarded request. This controller simply reads X-User-Id to associate
 * operations with a user. No JWT parsing or Feign calls needed here.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurements")
public class QuantityMeasurementController {

    private final IQuantityMeasurementService svc;

    public QuantityMeasurementController(IQuantityMeasurementService svc) {
        this.svc = svc;
    }

    /**
     * Reads the userId injected by the API Gateway's JwtAuthenticationFilter.
     * Returns null for anonymous (unauthenticated) requests.
     */
    private Long extractUserId(String userIdHeader) {
        if (userIdHeader == null || userIdHeader.isBlank()) return null;
        try {
            return Long.parseLong(userIdHeader);
        } catch (NumberFormatException ex) {
            log.warn("Invalid X-User-Id header: {}", userIdHeader);
            return null;
        }
    }

    @PostMapping("/compare")
    public ResponseEntity<QuantityMeasurementDTO> compare(
            @Valid @RequestBody QuantityInputDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        return ResponseEntity.ok(svc.compare(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), extractUserId(userIdHeader)));
    }

    @PostMapping("/convert")
    public ResponseEntity<QuantityMeasurementDTO> convert(
            @Valid @RequestBody QuantityInputDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        return ResponseEntity.ok(svc.convert(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), extractUserId(userIdHeader)));
    }

    @PostMapping("/add")
    public ResponseEntity<QuantityMeasurementDTO> add(
            @Valid @RequestBody QuantityInputDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        Long userId = extractUserId(userIdHeader);
        return ResponseEntity.ok(dto.getTargetUnitDTO() != null
                ? svc.add(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), dto.getTargetUnitDTO(), userId)
                : svc.add(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), userId));
    }

    @PostMapping("/subtract")
    public ResponseEntity<QuantityMeasurementDTO> subtract(
            @Valid @RequestBody QuantityInputDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        Long userId = extractUserId(userIdHeader);
        return ResponseEntity.ok(dto.getTargetUnitDTO() != null
                ? svc.subtract(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), dto.getTargetUnitDTO(), userId)
                : svc.subtract(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), userId));
    }

    @PostMapping("/divide")
    public ResponseEntity<QuantityMeasurementDTO> divide(
            @Valid @RequestBody QuantityInputDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        return ResponseEntity.ok(svc.divide(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), extractUserId(userIdHeader)));
    }

    @GetMapping("/history/operation/{operation}")
    public ResponseEntity<List<QuantityMeasurementDTO>> getOpHistory(
            @PathVariable String operation,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        return ResponseEntity.ok(svc.getHistoryByOperation(operation, extractUserId(userIdHeader)));
    }

    @GetMapping("/history/type/{measurementType}")
    public ResponseEntity<List<QuantityMeasurementDTO>> getTypeHistory(
            @PathVariable String measurementType,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        return ResponseEntity.ok(svc.getHistoryByMeasurementType(measurementType, extractUserId(userIdHeader)));
    }

    @GetMapping("/history/errored")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErrorHistory() {
        return ResponseEntity.ok(svc.getErrorHistory());
    }

    @GetMapping("/count/{operation}")
    public ResponseEntity<Long> getCount(
            @PathVariable String operation,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        return ResponseEntity.ok(svc.getOperationCount(operation, extractUserId(userIdHeader)));
    }
}
