package pe.edu.vallegrande.vgmsusers.application.dto.response;

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
public class UserResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String documentType;
    private String documentNumber;
    private String email;
    private String phone;
    private String address;

    private String organizationId;
    private String zoneId;
    private String streetId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String recordStatus;

    private String role;
    private String roleDisplayName;

}
