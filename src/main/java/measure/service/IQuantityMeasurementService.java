package measure.service;


import measure.entity.QuantityDTO;

public interface IQuantityMeasurementService {
	
	boolean compare(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO);
	
	QuantityDTO convert(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO);
	
	QuantityDTO add(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO);
	
	QuantityDTO add(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO,QuantityDTO targetQuantityDTO);
	
	QuantityDTO subtract(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO);
	
	QuantityDTO subtract(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO,QuantityDTO targetQuantityDTO);
	
	QuantityDTO divide(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO);
	
	QuantityDTO divide(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO,QuantityDTO targetQuantityDTO);
}