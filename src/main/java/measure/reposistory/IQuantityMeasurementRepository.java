package measure.reposistory;


import measure.model.QuantityMeasurementEntity;
import java.util.*;

public interface IQuantityMeasurementRepository {
      void save(QuantityMeasurementEntity enity);
      List<QuantityMeasurementEntity> getAllMeasurement();
}
