package pe.edu.vallegrande.msdistribution.application.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un horario de distribución.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionScheduleUpdateRequest {
    
    @Size(max = 100, message = "El ID de zona no puede exceder los 100 caracteres")
    private String zoneId;
    
    @Size(max = 20, message = "El día de la semana no puede exceder los 20 caracteres")
    private String dayOfWeek;
    
    @Size(max = 10, message = "La hora de inicio no puede exceder los 10 caracteres")
    private String startTime;
    
    @Size(max = 10, message = "La hora de fin no puede exceder los 10 caracteres")
    private String endTime;
    
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String description;
}
