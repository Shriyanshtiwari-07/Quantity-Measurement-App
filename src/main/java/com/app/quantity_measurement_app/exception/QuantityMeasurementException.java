package com.app.quantity_measurement_app.exception;

public class QuantityMeasurementException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public QuantityMeasurementException(String msg) {
		super(msg);
	}
	
	public QuantityMeasurementException(String message, Throwable cause) {
		super(message, cause) ;
	}
}
