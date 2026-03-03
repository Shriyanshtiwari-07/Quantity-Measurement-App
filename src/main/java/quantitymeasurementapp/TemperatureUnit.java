package quantitymeasurementapp;


import java.util.function.Function;

public enum TemperatureUnit implements IMeasurable {
    CELSIUS(val -> val, val -> val),
    FAHRENHEIT(val -> (val - 32) * 5/9, val -> (val * 9/5) + 32),
    KELVIN(val -> val - 273.15, val -> val + 273.15);

    private final Function<Double, Double> toBase;
    private final Function<Double, Double> fromBase;
    
    // Explicitly disabling arithmetic for Temperature
    private final SupportsArithmetic arithmeticSupport = () -> false;

    TemperatureUnit(Function<Double, Double> toBase, Function<Double, Double> fromBase) {
        this.toBase = toBase;
        this.fromBase = fromBase;
    }

    @Override
    public double convertToBaseUnit(double value) { return toBase.apply(value); }

    @Override
    public double convertFromBaseUnit(double value) { return fromBase.apply(value); }

    @Override
    public boolean supportsArithmetic() { return arithmeticSupport.isSupported(); }

    @Override
    public void validateOperationSupport(String operation) {
        throw new UnsupportedOperationException("Temperature units do not support " + operation);
    }

	@Override
	public double getConversionFactor() {
		return  0;
	}

	@Override
	public String getUnitName() {
		// TODO Auto-generated method stub
		return null;
	}
}