package com.app.measurementservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.app.measurementservice.dto.request.QuantityInputDTO;
import com.app.measurementservice.dto.request.QuantityMeasurementDTO;
import com.app.measurementservice.dto.response.QuantityDTO;
import com.app.measurementservice.service.IQuantityMeasurementService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * QuantityMeasurementControllerTest
 *
 * Tests the measurement-service REST controller in isolation.
 * No Spring Security — measurement-service trusts X-User-Id headers from the gateway.
 */
@WebMvcTest(QuantityMeasurementController.class)
class QuantityMeasurementControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private IQuantityMeasurementService quantityMeasurementService;

    private QuantityDTO twoFeet, twentyFourInches, zeroYards;
    private QuantityMeasurementDTO equalResult, notEqualResult;

    @BeforeEach
    void setUp() {
        twoFeet          = new QuantityDTO(2.0,  QuantityDTO.LengthUnit.FEET);
        twentyFourInches = new QuantityDTO(24.0, QuantityDTO.LengthUnit.INCHES);
        zeroYards        = new QuantityDTO(0.0,  QuantityDTO.LengthUnit.YARDS);
        equalResult    = QuantityMeasurementDTO.builder().operation("compare").resultString("true").error(false).build();
        notEqualResult = QuantityMeasurementDTO.builder().operation("compare").resultString("false").error(false).build();
    }

    private ResultActions doPost(String ep, QuantityInputDTO input) throws Exception {
        return mockMvc.perform(post("/api/v1/quantities/" + ep)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-User-Id", "1")
            .content(objectMapper.writeValueAsString(input)));
    }

    private QuantityMeasurementDTO buildResult(String op, Double val, String unit, String mType) {
        return QuantityMeasurementDTO.builder()
            .operation(op).resultValue(val).resultUnit(unit).resultMeasurementType(mType).error(false).build();
    }

    @Test void testLayerSeparation_ControllerIndependence_StubService() throws Exception {
        when(quantityMeasurementService.compare(any(), any(), any())).thenReturn(equalResult);
        doPost("compare", new QuantityInputDTO(twoFeet, twentyFourInches, null))
            .andExpect(status().isOk()).andExpect(jsonPath("$.resultString").value("true"));
        Mockito.verify(quantityMeasurementService, Mockito.times(1)).compare(any(QuantityDTO.class), any(QuantityDTO.class), any());
    }

    @Test void testController_NullBody_Returns400() throws Exception {
        mockMvc.perform(post("/api/v1/quantities/compare").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test void testPerformComparison_Equal_ReturnsTrue() throws Exception {
        when(quantityMeasurementService.compare(any(), any(), any())).thenReturn(equalResult);
        doPost("compare", new QuantityInputDTO(twoFeet, twentyFourInches, null))
            .andExpect(status().isOk()).andExpect(jsonPath("$.resultString").value("true"));
    }

    @Test void testPerformConversion_InchesToYards() throws Exception {
        when(quantityMeasurementService.convert(any(), any(), any())).thenReturn(
            QuantityMeasurementDTO.builder().operation("convert").resultValue(0.666667).resultUnit("YARDS").error(false).build());
        doPost("convert", new QuantityInputDTO(twentyFourInches, zeroYards, null))
            .andExpect(status().isOk()).andExpect(jsonPath("$.resultUnit").value("YARDS"));
    }

    @Test void testPerformAddition_TwoOperands() throws Exception {
        when(quantityMeasurementService.add(any(QuantityDTO.class), any(QuantityDTO.class), any())).thenReturn(buildResult("add", 4.0, "FEET", "LengthUnit"));
        doPost("add", new QuantityInputDTO(twoFeet, twentyFourInches, null))
            .andExpect(status().isOk()).andExpect(jsonPath("$.resultValue").value(4.0));
    }

    @Test void testPerformAddition_ThreeOperands() throws Exception {
        when(quantityMeasurementService.add(any(QuantityDTO.class), any(QuantityDTO.class), any(QuantityDTO.class), any())).thenReturn(buildResult("add", 1.333333, "YARDS", "LengthUnit"));
        doPost("add", new QuantityInputDTO(twoFeet, twentyFourInches, zeroYards))
            .andExpect(status().isOk()).andExpect(jsonPath("$.resultUnit").value("YARDS"));
    }

    @Test void testPerformSubtraction_TwoOperands() throws Exception {
        when(quantityMeasurementService.subtract(any(QuantityDTO.class), any(QuantityDTO.class), any())).thenReturn(buildResult("subtract", 0.0, "FEET", "LengthUnit"));
        doPost("subtract", new QuantityInputDTO(twoFeet, twentyFourInches, null))
            .andExpect(status().isOk()).andExpect(jsonPath("$.resultValue").value(0.0));
    }

    @Test void testPerformDivision_EqualQuantities() throws Exception {
        when(quantityMeasurementService.divide(any(), any(), any())).thenReturn(
            QuantityMeasurementDTO.builder().operation("divide").resultValue(1.0).error(false).build());
        doPost("divide", new QuantityInputDTO(twoFeet, twentyFourInches, null))
            .andExpect(status().isOk()).andExpect(jsonPath("$.resultValue").value(1.0));
    }

    @Test void testGetOperationHistory_ReturnsList() throws Exception {
        when(quantityMeasurementService.getHistoryByOperation(any(String.class), any())).thenReturn(List.of(equalResult));
        mockMvc.perform(get("/api/v1/quantities/history/operation/compare").header("X-User-Id", "1"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].operation").value("compare"));
    }

    @Test void testGetMeasurementHistory_ByType() throws Exception {
        when(quantityMeasurementService.getHistoryByMeasurementType(any(String.class), any())).thenReturn(List.of(equalResult));
        mockMvc.perform(get("/api/v1/quantities/history/type/LengthUnit").header("X-User-Id", "1"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test void testGetErrorHistory_ReturnsErrorRecords() throws Exception {
        when(quantityMeasurementService.getErrorHistory()).thenReturn(
            List.of(QuantityMeasurementDTO.builder().operation("add").error(true).errorMessage("Err").build()));
        mockMvc.perform(get("/api/v1/quantities/history/errored"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].error").value(true));
    }

    @Test void testGetOperationCount_ReturnsCount() throws Exception {
        when(quantityMeasurementService.getOperationCount(any(String.class), any())).thenReturn(5L);
        mockMvc.perform(get("/api/v1/quantities/count/COMPARE").header("X-User-Id", "1"))
            .andExpect(status().isOk()).andExpect(content().string("5"));
    }

    @Test void testCompareQuantities_InvalidInput_Returns400() throws Exception {
        mockMvc.perform(post("/api/v1/quantities/compare").contentType(MediaType.APPLICATION_JSON)
            .content("{\"thisQuantityDTO\":null,\"thatQuantityDTO\":null}")).andExpect(status().isBadRequest());
    }
}
