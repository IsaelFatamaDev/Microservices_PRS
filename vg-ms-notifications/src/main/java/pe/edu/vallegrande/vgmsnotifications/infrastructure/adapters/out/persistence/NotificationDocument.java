package pe.edu.vallegrande.vgmsnotifications.infrastructure.adapters.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
@CompoundIndex(name = "idx_userId_createdAt", def = "{'userId': 1, 'createdAt': -1}")
public class NotificationDocument {
    @Id
    private String id;

    @Indexed
    private String userId;
    private String phoneNumber;
    private String recipientName;

    @Indexed
    private String type;

    private String channel;

    @Indexed
    private String status;

    private String message;
    private String imageUrl;
    private String eventSource;
    private String eventId;
    private int retryCount;
    private String failureReason;
    private String recordStatus;

    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime updatedAt;
}
