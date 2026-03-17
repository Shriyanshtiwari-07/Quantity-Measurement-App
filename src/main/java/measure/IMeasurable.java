package quantitymeasurementapp;

public interface IMeasurable {
     double getConversionFactor();
     
     double convertToBaseUnit(double value);
     
     double convertFromBaseUnit(double value);
     
     String getUnitName();
     
     SupportsArithmetic supportsArithmetic = () -> true;

     default boolean supportsArithmetic() {
         return supportsArithmetic.isSupported();
     }

     default void validateOperationSupport(String operation) {
         if (!supportsArithmetic()) {
             throw new UnsupportedOperationException(
                 this.getClass().getSimpleName() + " does not support " + operation
             );
         }
     }
}