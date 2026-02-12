package pe.edu.vallegrande.msdistribution.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class DistributionProgramCreateRequest {
    @NotBlank(message = "El ID de organización es obligatorio")
    private final String organizationId;
    @NotBlank(message = "El ID de horario es obligatorio")
    private final String scheduleId;
    @NotBlank(message = "El ID de ruta es obligatorio")
    private final String routeId;
    @NotBlank(message = "El ID de zona es obligatorio")
    private final String zoneId;
    private final String streetId;
    @NotBlank(message = "La fecha del programa es obligatoria")
    private final String programDate;
    @NotBlank(message = "La hora de inicio planificada es obligatoria")
    private final String plannedStartTime;
    @NotBlank(message = "La hora de fin planificada es obligatoria")
    private final String plannedEndTime;
    private final String actualStartTime;
    private final String actualEndTime;
    @NotBlank(message = "El ID del usuario responsable es obligatorio")
    private final String responsibleUserId;
    private final String observations;
}
