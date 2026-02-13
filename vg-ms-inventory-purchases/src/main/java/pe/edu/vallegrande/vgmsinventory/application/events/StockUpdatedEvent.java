package pe.edu.vallegrande.vgmsinventorypurchases.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdatedEvent {
    private String materialId;
    private Double previousStock;
    private Double newStock;
    private String updatedBy;
    private LocalDateTime timestamp;
}
