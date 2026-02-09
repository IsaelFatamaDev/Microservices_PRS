package pe.edu.vallegrande.vgmsorganizations.application.dto.organization;

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
public class OrganizationResponse {
    private String id;
    private String organizationName;
    private String district;
    private String province;
    private String department;
    private String fullLocation;
    private String address;
    private String phone;
    private String email;
    private String recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
