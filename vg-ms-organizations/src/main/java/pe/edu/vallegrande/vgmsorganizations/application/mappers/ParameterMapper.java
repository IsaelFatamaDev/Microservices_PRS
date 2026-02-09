package pe.edu.vallegrande.vgmsorganizations.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.CreateParameterRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.UpdateParameterRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.response.ParameterResponse;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Parameter;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.documents.ParameterDocument;

@Component
public class ParameterMapper {

    public Parameter toModel(CreateParameterRequest request) {
        return Parameter.builder()
            .organizationId(request.getOrganizationId())
            .parameterType(request.getParameterType())
            .parameterValue(request.getParameterValue())
            .description(request.getDescription())
            .build();
    }

    public Parameter toModel(UpdateParameterRequest request) {
        return Parameter.builder()
            .parameterValue(request.getParameterValue())
            .description(request.getDescription())
            .build();
    }

    public ParameterResponse toResponse(Parameter param) {
        return ParameterResponse.builder()
            .id(param.getId())
            .organizationId(param.getOrganizationId())
            .parameterType(param.getParameterType())
            .parameterTypeDisplayName(param.getParameterType())
            .parameterValue(param.getParameterValue())
            .description(param.getDescription())
            .recordStatus(param.getRecordStatus().name())
            .createdAt(param.getCreatedAt())
            .createdBy(param.getCreatedBy())
            .updatedAt(param.getUpdatedAt())
            .updatedBy(param.getUpdatedBy())
            .build();
    }

    public ParameterDocument toDocument(Parameter param) {
        return ParameterDocument.builder()
            .id(param.getId())
            .organizationId(param.getOrganizationId())
            .parameterType(param.getParameterType())
            .parameterValue(param.getParameterValue())
            .description(param.getDescription())
            .recordStatus(param.getRecordStatus().name())
            .createdAt(param.getCreatedAt())
            .createdBy(param.getCreatedBy())
            .updatedAt(param.getUpdatedAt())
            .updatedBy(param.getUpdatedBy())
            .build();
    }

    public Parameter toModel(ParameterDocument doc) {
        return Parameter.builder()
            .id(doc.getId())
            .organizationId(doc.getOrganizationId())
            .parameterType(doc.getParameterType())
            .parameterValue(doc.getParameterValue())
            .description(doc.getDescription())
            .recordStatus(RecordStatus.valueOf(doc.getRecordStatus()))
            .createdAt(doc.getCreatedAt())
            .createdBy(doc.getCreatedBy())
            .updatedAt(doc.getUpdatedAt())
            .updatedBy(doc.getUpdatedBy())
            .build();
    }
}