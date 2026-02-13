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
public class WaterBoxTransferredEvent {
    private String waterBoxId;
    private String fromUserId;
    private String toUserId;
    private String createdBy;
    private LocalDateTime timestamp;
}
