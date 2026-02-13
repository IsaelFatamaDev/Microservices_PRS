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
public class LowStockAlertEvent {
    private String materialId;
    private String materialCode;
    private Double currentStock;
    private Double minStock;
    private LocalDateTime timestamp;
}
