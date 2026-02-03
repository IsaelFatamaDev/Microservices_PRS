package pe.edu.vallegrande.vgmsusers.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.DocumentType;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.Role;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;
    private String organizationId;
    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    private String firstName;
    private String lastName;
    private DocumentType documentType;
    private String documentNumber;
    private String email;
    private String phone;
    private String address;

    private String zoneId;
    private String streetId;

    private Role role;

    public void validateContact() {
        boolean hasEmail = email != null && !email.isBlank();
        boolean hasPhone = phone != null && !phone.isBlank();

        if (!hasEmail && !hasPhone) {
            throw new IllegalArgumentException("User must have at least one contact method");
        }
    }

    public String getFullName() {
        return String.format("%s %s", firstName, lastName).trim();
    }

    public boolean isActive() {
        return RecordStatus.ACTIVE.equals(recordStatus);
    }

    public boolean isInactive() {
        return RecordStatus.INACTIVE.equals(recordStatus);
    }

    public boolean isAdmin() {
        return Role.ADMIN.equals(role) || Role.SUPER_ADMIN.equals(role);
    }

    public boolean isSuperAdmin() {
        return Role.SUPER_ADMIN.equals(role);
    }

    public User markAsDeleted(String deletedBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.INACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(deletedBy)
                .build();
    }

    public User markAsActive(String restoreBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(restoreBy)
                .build();
    }

    public User updateWith(String firstName, String lastName, String email, String phone, String address, String updatedBy) {
        User updated = this.toBuilder()
                .firstName(firstName != null ? firstName : this.firstName)
                .lastName(lastName != null ? lastName : this.lastName)
                .email(email != null ? email : this.email)
                .phone(phone != null ? phone : this.phone)
                .address(address != null ? address : this.address)
                .updatedAt(LocalDateTime.now())
                .updatedBy(updatedBy)
                .build();

        updated.validateContact();
        return updated;
    }
}
