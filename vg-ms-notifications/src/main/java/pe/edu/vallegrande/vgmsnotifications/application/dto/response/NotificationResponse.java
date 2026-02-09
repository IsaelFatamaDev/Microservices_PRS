package pe.edu.vallegrande.vgmsnotifications.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse {

    private String id;
    private String userId;
    private String phoneNumber;
    private String recipientName;
    private String type;
    private String channel;
    private String status;
    private String message;
    private String imageUrl;
    private String eventSource;
    private int retryCount;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}
