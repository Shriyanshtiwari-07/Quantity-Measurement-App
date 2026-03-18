package measure.service;

import measure.entity.QuantityDTO;

public interface IQuantityMeasurementService {
	
	public boolean compare(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO);
	
	public QuantityDTO convert(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO);
	
	public QuantityDTO add(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO);
	
	public QuantityDTO add(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO,QuantityDTO targetQuantityDTO);
	
	public QuantityDTO subtract(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO);
	
	public QuantityDTO subtract(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO,QuantityDTO targetQuantityDTO);
	
	public QuantityDTO divide(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO);
	
	public QuantityDTO divide(QuantityDTO thisQuantityDTO,QuantityDTO thatQuantityDTO,QuantityDTO targetQuantityDTO);
}