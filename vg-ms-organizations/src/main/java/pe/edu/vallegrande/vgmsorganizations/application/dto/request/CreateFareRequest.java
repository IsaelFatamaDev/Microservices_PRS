package pe.edu.vallegrande.vgmsorganizations.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFareRequest {

    @NotBlank(message = "El ID de la organización es obligatorio")
    private String organizationId;

    @NotBlank(message = "El tipo de tarifa es obligatorio")
    @Pattern(regexp = "^[A-Z0-9_]{2,50}$", message = "Formato de tipo de tarifa inválido")
    private String fareType;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    private Double amount;

    @Size(max = 250, message = "La descripción no puede exceder 250 caracteres")
    private String description;

    private LocalDateTime validFrom;
    private LocalDateTime validTo;
}
