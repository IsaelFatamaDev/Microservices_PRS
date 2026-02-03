package pe.edu.vallegrande.vgmsusers.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsusers.application.dto.request.CreateUserRequest;
import pe.edu.vallegrande.vgmsusers.application.dto.request.UpdateUserRequest;
import pe.edu.vallegrande.vgmsusers.application.dto.response.UserResponse;
import pe.edu.vallegrande.vgmsusers.domain.models.User;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.DocumentType;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.Role;
import pe.edu.vallegrande.vgmsusers.infrastructure.persistence.entities.UserEntity;

@Component
public class UserMapper {

    public User toModel(CreateUserRequest request) {
        return User.builder()
            .organizationId(request.getOrganizationId())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .documentType(DocumentType.valueOf(request.getDocumentType()))
            .documentNumber(request.getDocumentNumber())
            .email(request.getEmail())
            .phone(request.getPhone())
            .address(request.getAddress())
            .zoneId(request.getZoneId())
            .streetId(request.getStreetId())
            .role(Role.valueOf(request.getRole()))
            .build();
    }

    public User toModel(UpdateUserRequest request) {
        return User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .address(request.getAddress())
            .build();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .organizationId(user.getOrganizationId())
            .recordStatus(user.getRecordStatus().name())
            .createdAt(user.getCreatedAt())
            .createdBy(user.getCreatedBy())
            .updatedAt(user.getUpdatedAt())
            .updatedBy(user.getUpdatedBy())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .fullName(user.getFullName())
            .documentType(user.getDocumentType().name())
            .documentNumber(user.getDocumentNumber())
            .email(user.getEmail())
            .phone(user.getPhone())
            .address(user.getAddress())
            .zoneId(user.getZoneId())
            .streetId(user.getStreetId())
            .role(user.getRole().name())
            .roleDisplayName(user.getRole().getDisplayName())
            .build();
    }

    public UserEntity toEntity(User user) {
        return UserEntity.builder()
            .id(user.getId())
            .organizationId(user.getOrganizationId())
            .recordStatus(user.getRecordStatus().name())
            .createdAt(user.getCreatedAt())
            .createdBy(user.getCreatedBy())
            .updatedAt(user.getUpdatedAt())
            .updatedBy(user.getUpdatedBy())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .documentType(user.getDocumentType().name())
            .documentNumber(user.getDocumentNumber())
            .email(user.getEmail())
            .phone(user.getPhone())
            .address(user.getAddress())
            .zoneId(user.getZoneId())
            .streetId(user.getStreetId())
            .role(user.getRole().name())
            .build();
    }

    public User toModel(UserEntity entity) {
        return User.builder()
            .id(entity.getId())
            .organizationId(entity.getOrganizationId())
            .recordStatus(pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.RecordStatus.valueOf(entity.getRecordStatus()))
            .createdAt(entity.getCreatedAt())
            .createdBy(entity.getCreatedBy())
            .updatedAt(entity.getUpdatedAt())
            .updatedBy(entity.getUpdatedBy())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .documentType(DocumentType.valueOf(entity.getDocumentType()))
            .documentNumber(entity.getDocumentNumber())
            .email(entity.getEmail())
            .phone(entity.getPhone())
            .address(entity.getAddress())
            .zoneId(entity.getZoneId())
            .streetId(entity.getStreetId())
            .role(Role.valueOf(entity.getRole()))
            .build();
    }
}
