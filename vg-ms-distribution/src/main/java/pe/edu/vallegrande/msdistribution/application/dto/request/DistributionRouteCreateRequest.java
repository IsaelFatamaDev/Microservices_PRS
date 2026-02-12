package pe.edu.vallegrande.msdistribution.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
public class DistributionRouteCreateRequest {
    @NotBlank(message = "El ID de organización es obligatorio")
    private final String organizationId;
    @NotBlank(message = "El nombre de ruta es obligatorio")
    private final String routeName;
    @NotEmpty(message = "Debe incluir al menos una zona")
    @Valid
    private final List<ZoneEntry> zones;
    @NotNull(message = "La duración estimada total es obligatoria")
    private final Integer totalEstimatedDuration;
    @NotBlank(message = "El ID del usuario responsable es obligatorio")
    private final String responsibleUserId;

    @Getter
    @Builder
    @Jacksonized
    public static class ZoneEntry {
        @NotBlank(message = "El ID de zona es obligatorio")
        private final String zoneId;
        @NotNull(message = "El orden es obligatorio")
        private final Integer order;
        @NotNull(message = "La duración estimada es obligatoria")
        private final Integer estimatedDuration;
    }
}
