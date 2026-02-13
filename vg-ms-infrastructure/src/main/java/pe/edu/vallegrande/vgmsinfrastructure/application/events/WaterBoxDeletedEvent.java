package pe.edu.vallegrande.vgmsinfrastructure.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterBoxDeletedEvent {
    private String waterBoxId;
    private String deletedBy;
    private LocalDateTime timestamp;
}
