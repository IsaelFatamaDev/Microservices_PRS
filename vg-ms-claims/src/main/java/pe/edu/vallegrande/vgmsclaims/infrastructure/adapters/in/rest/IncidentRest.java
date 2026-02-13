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
import pe.edu.vallegrande.vgmsclaims.application.dto.incident.AssignIncidentRequest;
import pe.edu.vallegrande.vgmsclaims.application.dto.incident.CreateIncidentRequest;
import pe.edu.vallegrande.vgmsclaims.application.dto.incident.IncidentResponse;
import pe.edu.vallegrande.vgmsclaims.application.dto.incident.ResolveIncidentRequest;
import pe.edu.vallegrande.vgmsclaims.application.dto.incident.UpdateIncidentRequest;
import pe.edu.vallegrande.vgmsclaims.application.mappers.IncidentMapper;
import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentResolution;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.IncidentSeverity;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.IncidentStatus;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.MaterialUsed;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ResolutionType;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for incidents management.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
@Tag(name = "Incidents", description = "Incidents management API")
public class IncidentRest {

     private final ICreateIncidentUseCase createIncidentUseCase;
     private final IGetIncidentUseCase getIncidentUseCase;
     private final IUpdateIncidentUseCase updateIncidentUseCase;
     private final IAssignIncidentUseCase assignIncidentUseCase;
     private final IResolveIncidentUseCase resolveIncidentUseCase;
     private final ICloseIncidentUseCase closeIncidentUseCase;
     private final IDeleteIncidentUseCase deleteIncidentUseCase;
     private final IRestoreIncidentUseCase restoreIncidentUseCase;
     private final IncidentMapper incidentMapper;

     @Operation(summary = "List all incidents")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Incidents list retrieved successfully")
     })
     @GetMapping
     public Mono<ResponseEntity<ApiResponse<List<IncidentResponse>>>> findAll() {
          log.info("Fetching all incidents");
          return getIncidentUseCase.findAll()
                    .map(incidentMapper::toResponse)
                    .collectList()
                    .map(incidents -> ResponseEntity.ok(ApiResponse.success(incidents)));
     }

     @Operation(summary = "Get incident by ID")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Incident found"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Incident not found")
     })
     @GetMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<IncidentResponse>>> findById(
               @Parameter(description = "Incident ID") @PathVariable String id) {
          log.info("Finding incident by ID: {}", id);
          return getIncidentUseCase.findById(id)
                    .map(incidentMapper::toResponse)
                    .map(incident -> ResponseEntity.ok(ApiResponse.success(incident)));
     }

     @Operation(summary = "Create new incident")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Incident created successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data")
     })
     @PostMapping
     public Mono<ResponseEntity<ApiResponse<IncidentResponse>>> create(
               @Valid @RequestBody CreateIncidentRequest request) {
          log.info("Creating new incident: {}", request.getTitle());
          Incident incident = incidentMapper.toDomain(request);
          return createIncidentUseCase.execute(incident)
                    .map(incidentMapper::toResponse)
                    .map(created -> ResponseEntity.status(HttpStatus.CREATED)
                              .body(ApiResponse.success(created, "Incident created successfully")));
     }

     @Operation(summary = "Update incident")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Incident updated successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Incident not found")
     })
     @PutMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<IncidentResponse>>> update(
               @Parameter(description = "Incident ID") @PathVariable String id,
               @Valid @RequestBody UpdateIncidentRequest request) {
          log.info("Updating incident with ID: {}", id);
          Incident updatedData = Incident.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .severity(request.getSeverity() != null
                              ? IncidentSeverity.valueOf(request.getSeverity().toUpperCase())
                              : null)
                    .status(request.getStatus() != null ? IncidentStatus.valueOf(request.getStatus().toUpperCase())
                              : null)
                    .affectedBoxesCount(request.getAffectedBoxesCount())
                    .resolutionNotes(request.getResolutionNotes())
                    .build();
          return updateIncidentUseCase.execute(id, updatedData)
                    .map(incidentMapper::toResponse)
                    .map(updated -> ResponseEntity.ok(ApiResponse.success(updated, "Incident updated successfully")));
     }

     @Operation(summary = "Assign incident to technician")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Incident assigned successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Incident not found")
     })
     @PostMapping("/{id}/assign")
     public Mono<ResponseEntity<ApiResponse<IncidentResponse>>> assign(
               @Parameter(description = "Incident ID") @PathVariable String id,
               @Valid @RequestBody AssignIncidentRequest request) {
          log.info("Assigning incident {} to user {}", id, request.getUserId());
          return assignIncidentUseCase.execute(id, request.getUserId())
                    .map(incidentMapper::toResponse)
                    .map(assigned -> ResponseEntity
                              .ok(ApiResponse.success(assigned, "Incident assigned successfully")));
     }

     @Operation(summary = "Resolve incident")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Incident resolved successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Incident not found"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Incident already resolved")
     })
     @PostMapping("/{id}/resolve")
     public Mono<ResponseEntity<ApiResponse<IncidentResponse>>> resolve(
               @Parameter(description = "Incident ID") @PathVariable String id,
               @Valid @RequestBody ResolveIncidentRequest request) {
          log.info("Resolving incident: {}", id);

          List<MaterialUsed> materials = null;
          if (request.getMaterialsUsed() != null) {
               materials = request.getMaterialsUsed().stream()
                         .map(m -> new MaterialUsed(m.getProductId(), m.getQuantity(), m.getUnit(), m.getUnitCost()))
                         .collect(Collectors.toList());
          }

          IncidentResolution resolution = IncidentResolution.builder()
                    .resolutionType(request.getResolutionType() != null
                              ? ResolutionType.valueOf(request.getResolutionType().toUpperCase())
                              : null)
                    .actionsTaken(request.getActionsTaken())
                    .materialsUsed(materials)
                    .laborHours(request.getLaborHours())
                    .totalCost(request.getTotalCost())
                    .qualityCheck(request.getQualityCheck())
                    .followUpRequired(request.getFollowUpRequired())
                    .resolutionNotes(request.getResolutionNotes())
                    .build();

          return resolveIncidentUseCase.execute(id, resolution)
                    .map(incidentMapper::toResponse)
                    .map(resolved -> ResponseEntity
                              .ok(ApiResponse.success(resolved, "Incident resolved successfully")));
     }

     @Operation(summary = "Close incident")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Incident closed successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Incident not found")
     })
     @PostMapping("/{id}/close")
     public Mono<ResponseEntity<ApiResponse<IncidentResponse>>> close(
               @Parameter(description = "Incident ID") @PathVariable String id) {
          log.info("Closing incident with ID: {}", id);
          return closeIncidentUseCase.execute(id)
                    .map(incidentMapper::toResponse)
                    .map(closed -> ResponseEntity.ok(ApiResponse.success(closed, "Incident closed successfully")));
     }

     @Operation(summary = "Find incidents by organization")
     @GetMapping("/organization/{organizationId}")
     public Mono<ResponseEntity<ApiResponse<List<IncidentResponse>>>> findByOrganization(
               @Parameter(description = "Organization ID") @PathVariable String organizationId) {
          log.info("Finding incidents by organization: {}", organizationId);
          return getIncidentUseCase.findByOrganizationId(organizationId)
                    .map(incidentMapper::toResponse)
                    .collectList()
                    .map(incidents -> ResponseEntity.ok(ApiResponse.success(incidents)));
     }

     @Operation(summary = "Find incidents by zone")
     @GetMapping("/zone/{zoneId}")
     public Mono<ResponseEntity<ApiResponse<List<IncidentResponse>>>> findByZone(
               @Parameter(description = "Zone ID") @PathVariable String zoneId) {
          log.info("Finding incidents by zone: {}", zoneId);
          return getIncidentUseCase.findByZoneId(zoneId)
                    .map(incidentMapper::toResponse)
                    .collectList()
                    .map(incidents -> ResponseEntity.ok(ApiResponse.success(incidents)));
     }

     @Operation(summary = "Find incidents by severity")
     @GetMapping("/severity/{severity}")
     public Mono<ResponseEntity<ApiResponse<List<IncidentResponse>>>> findBySeverity(
               @Parameter(description = "Incident severity") @PathVariable String severity) {
          log.info("Finding incidents by severity: {}", severity);
          return getIncidentUseCase.findBySeverity(severity)
                    .map(incidentMapper::toResponse)
                    .collectList()
                    .map(incidents -> ResponseEntity.ok(ApiResponse.success(incidents)));
     }

     @Operation(summary = "Find incidents by status")
     @GetMapping("/status/{status}")
     public Mono<ResponseEntity<ApiResponse<List<IncidentResponse>>>> findByStatus(
               @Parameter(description = "Incident status") @PathVariable String status) {
          log.info("Finding incidents by status: {}", status);
          return getIncidentUseCase.findByStatus(status)
                    .map(incidentMapper::toResponse)
                    .collectList()
                    .map(incidents -> ResponseEntity.ok(ApiResponse.success(incidents)));
     }

     @Operation(summary = "Soft delete incident")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Incident deleted successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Incident not found")
     })
     @DeleteMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<IncidentResponse>>> delete(
               @Parameter(description = "Incident ID") @PathVariable String id) {
          log.info("Soft deleting incident with ID: {}", id);
          return deleteIncidentUseCase.execute(id)
                    .map(incidentMapper::toResponse)
                    .map(deleted -> ResponseEntity.ok(ApiResponse.success(deleted, "Incident deleted successfully")));
     }

     @Operation(summary = "Restore incident")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Incident restored successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Incident not found")
     })
     @PatchMapping("/{id}/restore")
     public Mono<ResponseEntity<ApiResponse<IncidentResponse>>> restore(
               @Parameter(description = "Incident ID") @PathVariable String id) {
          log.info("Restoring incident with ID: {}", id);
          return restoreIncidentUseCase.execute(id)
                    .map(incidentMapper::toResponse)
                    .map(restored -> ResponseEntity
                              .ok(ApiResponse.success(restored, "Incident restored successfully")));
     }
}
