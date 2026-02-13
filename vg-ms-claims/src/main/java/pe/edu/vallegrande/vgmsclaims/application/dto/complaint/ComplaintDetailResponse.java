package pe.edu.vallegrande.vgmsclaims.application.dto.complaint;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Detailed response DTO for complaints (includes responses).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComplaintDetailResponse {

    private String id;
    private String organizationId;
    private String complaintCode;
    private String userId;
    private String userName;
    private String categoryId;
    private String categoryName;
    private String waterBoxId;
    private Instant complaintDate;
    private String subject;
    private String description;
    private String priority;
    private String status;
    private String assignedToUserId;
    private String assignedToUserName;
    private Instant expectedResolutionDate;
    private Instant actualResolutionDate;
    private Integer satisfactionRating;
    private String recordStatus;
    private Instant createdAt;
    private Instant updatedAt;

    private List<ResponseItem> responses;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseItem {
        private String id;
        private Instant responseDate;
        private String responseType;
        private String message;
        private String respondedByUserId;
        private String respondedByUserName;
        private Instant createdAt;
    }
}
