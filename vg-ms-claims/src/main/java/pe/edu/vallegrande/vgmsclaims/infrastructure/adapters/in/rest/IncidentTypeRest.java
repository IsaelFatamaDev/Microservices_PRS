package pe.edu.vallegrande.vgmsclaims.infrastructure.adapters.in.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsclaims.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsclaims.application.dto.incidenttype.CreateIncidentTypeRequest;
import pe.edu.vallegrande.vgmsclaims.application.dto.incidenttype.IncidentTypeResponse;
import pe.edu.vallegrande.vgmsclaims.application.dto.incidenttype.UpdateIncidentTypeRequest;
import pe.edu.vallegrande.vgmsclaims.application.mappers.IncidentTypeMapper;
import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentType;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.incidenttype.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST controller for incident types management.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/incident-types")
@RequiredArgsConstructor
@Tag(name = "Incident Types", description = "Incident types management API")
public class IncidentTypeRest {

     private final ICreateIncidentTypeUseCase createIncidentTypeUseCase;
     private final IGetIncidentTypeUseCase getIncidentTypeUseCase;
     private final IUpdateIncidentTypeUseCase updateIncidentTypeUseCase;
     private final IDeleteIncidentTypeUseCase deleteIncidentTypeUseCase;
     private final IRestoreIncidentTypeUseCase restoreIncidentTypeUseCase;
     private final IncidentTypeMapper incidentTypeMapper;

     @Operation(summary = "List all incident types")
     @GetMapping
     public Mono<ResponseEntity<ApiResponse<List<IncidentTypeResponse>>>> findAll() {
          log.info("Fetching all incident types");
          return getIncidentTypeUseCase.findAll()
                    .map(incidentTypeMapper::toResponse)
                    .collectList()
                    .map(types -> ResponseEntity.ok(ApiResponse.success(types)));
     }

     @Operation(summary = "Get incident type by ID")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Incident type found"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Incident type not found")
     })
     @GetMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<IncidentTypeResponse>>> findById(
               @Parameter(description = "Incident type ID") @PathVariable String id) {
          log.info("Finding incident type by ID: {}", id);
          return getIncidentTypeUseCase.findById(id)
                    .map(incidentTypeMapper::toResponse)
                    .map(type -> ResponseEntity.ok(ApiResponse.success(type)));
     }

     @Operation(summary = "Find incident types by organization")
     @GetMapping("/organization/{organizationId}")
     public Mono<ResponseEntity<ApiResponse<List<IncidentTypeResponse>>>> findByOrganization(
               @Parameter(description = "Organization ID") @PathVariable String organizationId) {
          log.info("Finding incident types by organization: {}", organizationId);
          return getIncidentTypeUseCase.findByOrganizationId(organizationId)
                    .map(incidentTypeMapper::toResponse)
                    .collectList()
                    .map(types -> ResponseEntity.ok(ApiResponse.success(types)));
     }

     @Operation(summary = "Find active incident types by organization")
     @GetMapping("/organization/{organizationId}/active")
     public Mono<ResponseEntity<ApiResponse<List<IncidentTypeResponse>>>> findActiveByOrganization(
               @Parameter(description = "Organization ID") @PathVariable String organizationId) {
          log.info("Finding active incident types by organization: {}", organizationId);
          return getIncidentTypeUseCase.findActiveByOrganizationId(organizationId)
                    .map(incidentTypeMapper::toResponse)
                    .collectList()
                    .map(types -> ResponseEntity.ok(ApiResponse.success(types)));
     }

     @Operation(summary = "Create new incident type")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Incident type created successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data")
     })
     @PostMapping
     public Mono<ResponseEntity<ApiResponse<IncidentTypeResponse>>> create(
               @Valid @RequestBody CreateIncidentTypeRequest request) {
          log.info("Creating new incident type: {}", request.getTypeName());
          IncidentType incidentType = incidentTypeMapper.toDomain(request);
          return createIncidentTypeUseCase.execute(incidentType)
                    .map(incidentTypeMapper::toResponse)
                    .map(created -> ResponseEntity.status(HttpStatus.CREATED)
                              .body(ApiResponse.success(created, "Incident type created successfully")));
     }

     @Operation(summary = "Update incident type")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Incident type updated successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Incident type not found")
     })
     @PutMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<IncidentTypeResponse>>> update(
               @Parameter(description = "Incident type ID") @PathVariable String id,
               @Valid @RequestBody UpdateIncidentTypeRequest request) {
          log.info("Updating incident type with ID: {}", id);
          IncidentType updatedData = IncidentType.builder()
                    .typeName(request.getTypeName())
                    .description(request.getDescription())
                    .priorityLevel(request.getPriorityLevel())
                    .estimatedResolutionTime(request.getEstimatedResolutionTime())
                    .requiresExternalService(request.getRequiresExternalService())
                    .build();
          return updateIncidentTypeUseCase.execute(id, updatedData)
                    .map(incidentTypeMapper::toResponse)
                    .map(updated -> ResponseEntity
                              .ok(ApiResponse.success(updated, "Incident type updated successfully")));
     }

     @Operation(summary = "Soft delete incident type")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Incident type deleted successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Incident type not found")
     })
     @DeleteMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<IncidentTypeResponse>>> delete(
               @Parameter(description = "Incident type ID") @PathVariable String id) {
          log.info("Soft deleting incident type with ID: {}", id);
          return deleteIncidentTypeUseCase.execute(id)
                    .map(incidentTypeMapper::toResponse)
                    .map(deleted -> ResponseEntity
                              .ok(ApiResponse.success(deleted, "Incident type deleted successfully")));
     }

     @Operation(summary = "Restore incident type")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Incident type restored successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Incident type not found")
     })
     @PatchMapping("/{id}/restore")
     public Mono<ResponseEntity<ApiResponse<IncidentTypeResponse>>> restore(
               @Parameter(description = "Incident type ID") @PathVariable String id) {
          log.info("Restoring incident type with ID: {}", id);
          return restoreIncidentTypeUseCase.execute(id)
                    .map(incidentTypeMapper::toResponse)
                    .map(restored -> ResponseEntity
                              .ok(ApiResponse.success(restored, "Incident type restored successfully")));
     }
}
