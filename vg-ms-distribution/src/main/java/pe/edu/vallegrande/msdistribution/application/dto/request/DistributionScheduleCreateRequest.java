package pe.edu.vallegrande.msdistribution.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
public class DistributionScheduleCreateRequest {
    @NotBlank(message = "El ID de organización es obligatorio")
    private final String organizationId;
    @NotBlank(message = "El ID de zona es obligatorio")
    private final String zoneId;
    private final String streetId;
    @NotBlank(message = "El nombre del horario es obligatorio")
    private final String scheduleName;
    private final List<String> daysOfWeek;
    @NotBlank(message = "La hora de inicio es obligatoria")
    private final String startTime;
    @NotBlank(message = "La hora de fin es obligatoria")
    private final String endTime;
    private final Integer durationHours;
}
