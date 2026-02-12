package pe.edu.vallegrande.msdistribution.application.events.program;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evento publicado cuando se elimina (desactiva) un programa de distribución.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionProgramDeletedEvent {

    @Builder.Default
    private String eventType = "DISTRIBUTION_PROGRAM_DELETED";

    private String eventId;
    private LocalDateTime timestamp;
    private String correlationId;

    private String programId;
    private String organizationId;

    private String reason;

    private String deletedBy;
}
