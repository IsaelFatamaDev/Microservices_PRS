package pe.edu.vallegrande.vgmsorganizations.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class Organization {

    private String id;
    private String organizationName;
    private String district;
    private String province;
    private String department;
    private String address;
    private String phone;
    private String email;

    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public String getFullLocation() {
        return String.format("%s, %s - %s", district, province, department);
    }

    public boolean isActive() {
        return recordStatus == RecordStatus.ACTIVE;
    }

    public boolean isInactive() {
        return recordStatus == RecordStatus.INACTIVE;
    }

    public Organization markAsDeleted(String deletedBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.INACTIVE)
            .updatedAt(LocalDateTime.now())
            .updatedBy(deletedBy)
            .build();
    }

    public Organization restore(String restoredBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.ACTIVE)
            .updatedAt(LocalDateTime.now())
            .updatedBy(restoredBy)
            .build();
    }

    public Organization updateWith(Organization changes, String updatedBy) {
        var builder = this.toBuilder()
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy);

        if (changes.getOrganizationName() != null) builder.organizationName(changes.getOrganizationName());
        if (changes.getDistrict() != null) builder.district(changes.getDistrict());
        if (changes.getProvince() != null) builder.province(changes.getProvince());
        if (changes.getDepartment() != null) builder.department(changes.getDepartment());
        if (changes.getAddress() != null) builder.address(changes.getAddress());
        if (changes.getPhone() != null) builder.phone(changes.getPhone());
        if (changes.getEmail() != null) builder.email(changes.getEmail());

        return builder.build();
    }

    public void validateContact() {
        if ((email == null || email.isBlank()) && (phone == null || phone.isBlank())) {
            throw new IllegalArgumentException("Organization must have at least email or phone");
        }
    }
}