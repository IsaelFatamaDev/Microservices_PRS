package pe.edu.vallegrande.vgmswaterquality.domain.exceptions;

public class InvalidMeasurementException extends BusinessRuleException {
     public InvalidMeasurementException(String parameter, Double value) {
          super(String.format("Medición inválida para %s: %s", parameter, value), "INVALID_MEASUREMENT");
     }
}
