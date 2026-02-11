package pe.edu.vallegrande.vgmsorganizations.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.CreateOrganizationRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.UpdateOrganizationRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.response.OrganizationResponse;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Organization;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.documents.OrganizationDocument;

@Component
public class OrganizationMapper {

    public Organization toModel(CreateOrganizationRequest request) {
        return Organization.builder()
                .organizationName(request.getOrganizationName())
                .district(request.getDistrict())
                .province(request.getProvince())
                .department(request.getDepartment())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .logoUrl(request.getLogoUrl())
                .build();
    }

    public Organization toModel(UpdateOrganizationRequest request) {
        return Organization.builder()
                .organizationName(request.getOrganizationName())
                .district(request.getDistrict())
                .province(request.getProvince())
                .department(request.getDepartment())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .logoUrl(request.getLogoUrl())
                .build();
    }

    public OrganizationResponse toResponse(Organization org) {
        return OrganizationResponse.builder()
                .id(org.getId())
                .organizationName(org.getOrganizationName())
                .district(org.getDistrict())
                .province(org.getProvince())
                .department(org.getDepartment())
                .fullLocation(org.getFullLocation())
                .address(org.getAddress())
                .phone(org.getPhone())
                .email(org.getEmail())
                .logoUrl(org.getLogoUrl())
                .recordStatus(org.getRecordStatus().name())
                .createdAt(org.getCreatedAt())
                .createdBy(org.getCreatedBy())
                .updatedAt(org.getUpdatedAt())
                .updatedBy(org.getUpdatedBy())
                .build();
    }

    public OrganizationDocument toDocument(Organization org) {
        return OrganizationDocument.builder()
                .id(org.getId())
                .organizationName(org.getOrganizationName())
                .district(org.getDistrict())
                .province(org.getProvince())
                .department(org.getDepartment())
                .address(org.getAddress())
                .phone(org.getPhone())
                .email(org.getEmail())
                .logoUrl(org.getLogoUrl())
                .recordStatus(org.getRecordStatus().name())
                .createdAt(org.getCreatedAt())
                .createdBy(org.getCreatedBy())
                .updatedAt(org.getUpdatedAt())
                .updatedBy(org.getUpdatedBy())
                .build();
    }

    public Organization toModel(OrganizationDocument doc) {
        return Organization.builder()
                .id(doc.getId())
                .organizationName(doc.getOrganizationName())
                .district(doc.getDistrict())
                .province(doc.getProvince())
                .department(doc.getDepartment())
                .address(doc.getAddress())
                .phone(doc.getPhone())
                .email(doc.getEmail())
                .logoUrl(doc.getLogoUrl())
                .recordStatus(RecordStatus.valueOf(doc.getRecordStatus()))
                .createdAt(doc.getCreatedAt())
                .createdBy(doc.getCreatedBy())
                .updatedAt(doc.getUpdatedAt())
                .updatedBy(doc.getUpdatedBy())
                .build();
    }
}