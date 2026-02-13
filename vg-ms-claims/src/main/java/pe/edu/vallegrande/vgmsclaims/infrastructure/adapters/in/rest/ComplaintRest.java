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
import pe.edu.vallegrande.vgmsclaims.application.dto.complaint.AddResponseRequest;
import pe.edu.vallegrande.vgmsclaims.application.dto.complaint.ComplaintResponse;
import pe.edu.vallegrande.vgmsclaims.application.dto.complaint.CreateComplaintRequest;
import pe.edu.vallegrande.vgmsclaims.application.dto.complaint.UpdateComplaintRequest;
import pe.edu.vallegrande.vgmsclaims.application.mappers.ComplaintMapper;
import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ComplaintPriority;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ComplaintStatus;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ResponseType;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST controller for complaints management.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
@Tag(name = "Complaints", description = "Complaints management API")
public class ComplaintRest {

     private final ICreateComplaintUseCase createComplaintUseCase;
     private final IGetComplaintUseCase getComplaintUseCase;
     private final IUpdateComplaintUseCase updateComplaintUseCase;
     private final ICloseComplaintUseCase closeComplaintUseCase;
     private final IDeleteComplaintUseCase deleteComplaintUseCase;
     private final IRestoreComplaintUseCase restoreComplaintUseCase;
     private final IAddResponseUseCase addResponseUseCase;
     private final ComplaintMapper complaintMapper;

     @Operation(summary = "List all complaints")
     @GetMapping
     public Mono<ResponseEntity<ApiResponse<List<ComplaintResponse>>>> findAll() {
          log.info("Fetching all complaints");
          return getComplaintUseCase.findAll()
                    .map(complaintMapper::toResponse)
                    .collectList()
                    .map(complaints -> ResponseEntity.ok(ApiResponse.success(complaints)));
     }

     @Operation(summary = "Get complaint by ID")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Complaint found"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Complaint not found")
     })
     @GetMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<ComplaintResponse>>> findById(
               @Parameter(description = "Complaint ID") @PathVariable String id) {
          log.info("Finding complaint by ID: {}", id);
          return getComplaintUseCase.findById(id)
                    .map(complaintMapper::toResponse)
                    .map(complaint -> ResponseEntity.ok(ApiResponse.success(complaint)));
     }

     @Operation(summary = "Create new complaint")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Complaint created successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data")
     })
     @PostMapping
     public Mono<ResponseEntity<ApiResponse<ComplaintResponse>>> create(
               @Valid @RequestBody CreateComplaintRequest request) {
          log.info("Creating new complaint: {}", request.getSubject());
          Complaint complaint = complaintMapper.toDomain(request);
          return createComplaintUseCase.execute(complaint)
                    .map(complaintMapper::toResponse)
                    .map(created -> ResponseEntity.status(HttpStatus.CREATED)
                              .body(ApiResponse.success(created, "Complaint created successfully")));
     }

     @Operation(summary = "Update complaint")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Complaint updated successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Complaint not found")
     })
     @PutMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<ComplaintResponse>>> update(
               @Parameter(description = "Complaint ID") @PathVariable String id,
               @Valid @RequestBody UpdateComplaintRequest request) {
          log.info("Updating complaint with ID: {}", id);
          Complaint updatedData = Complaint.builder()
                    .subject(request.getSubject())
                    .description(request.getDescription())
                    .priority(request.getPriority() != null
                              ? ComplaintPriority.valueOf(request.getPriority().toUpperCase())
                              : null)
                    .status(request.getStatus() != null ? ComplaintStatus.valueOf(request.getStatus().toUpperCase())
                              : null)
                    .assignedToUserId(request.getAssignedToUserId())
                    .expectedResolutionDate(request.getExpectedResolutionDate())
                    .build();
          return updateComplaintUseCase.execute(id, updatedData)
                    .map(complaintMapper::toResponse)
                    .map(updated -> ResponseEntity.ok(ApiResponse.success(updated, "Complaint updated successfully")));
     }

     @Operation(summary = "Add response to complaint")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Response added successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Complaint not found")
     })
     @PostMapping("/{id}/responses")
     public Mono<ResponseEntity<ApiResponse<ComplaintResponse>>> addResponse(
               @Parameter(description = "Complaint ID") @PathVariable String id,
               @Valid @RequestBody AddResponseRequest request) {
          log.info("Adding response to complaint: {}", id);
          pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintResponse response = pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintResponse
                    .builder()
                    .responseType(request.getResponseType() != null
                              ? ResponseType.valueOf(request.getResponseType().toUpperCase())
                              : null)
                    .message(request.getMessage())
                    .internalNotes(request.getInternalNotes())
                    .build();
          return addResponseUseCase.execute(id, response)
                    .map(complaintMapper::toResponse)
                    .map(updated -> ResponseEntity.ok(ApiResponse.success(updated, "Response added successfully")));
     }

     @Operation(summary = "Close complaint")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Complaint closed successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Complaint not found")
     })
     @PostMapping("/{id}/close")
     public Mono<ResponseEntity<ApiResponse<ComplaintResponse>>> close(
               @Parameter(description = "Complaint ID") @PathVariable String id,
               @RequestParam(required = false) Integer satisfactionRating) {
          log.info("Closing complaint with ID: {}", id);
          return closeComplaintUseCase.execute(id, satisfactionRating)
                    .map(complaintMapper::toResponse)
                    .map(closed -> ResponseEntity.ok(ApiResponse.success(closed, "Complaint closed successfully")));
     }

     @Operation(summary = "Find complaints by organization")
     @GetMapping("/organization/{organizationId}")
     public Mono<ResponseEntity<ApiResponse<List<ComplaintResponse>>>> findByOrganization(
               @Parameter(description = "Organization ID") @PathVariable String organizationId) {
          log.info("Finding complaints by organization: {}", organizationId);
          return getComplaintUseCase.findByOrganizationId(organizationId)
                    .map(complaintMapper::toResponse)
                    .collectList()
                    .map(complaints -> ResponseEntity.ok(ApiResponse.success(complaints)));
     }

     @Operation(summary = "Find complaints by user")
     @GetMapping("/user/{userId}")
     public Mono<ResponseEntity<ApiResponse<List<ComplaintResponse>>>> findByUser(
               @Parameter(description = "User ID") @PathVariable String userId) {
          log.info("Finding complaints by user: {}", userId);
          return getComplaintUseCase.findByUserId(userId)
                    .map(complaintMapper::toResponse)
                    .collectList()
                    .map(complaints -> ResponseEntity.ok(ApiResponse.success(complaints)));
     }

     @Operation(summary = "Find complaints by status")
     @GetMapping("/status/{status}")
     public Mono<ResponseEntity<ApiResponse<List<ComplaintResponse>>>> findByStatus(
               @Parameter(description = "Complaint status") @PathVariable String status) {
          log.info("Finding complaints by status: {}", status);
          return getComplaintUseCase.findByStatus(status)
                    .map(complaintMapper::toResponse)
                    .collectList()
                    .map(complaints -> ResponseEntity.ok(ApiResponse.success(complaints)));
     }

     @Operation(summary = "Soft delete complaint")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Complaint deleted successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Complaint not found")
     })
     @DeleteMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<ComplaintResponse>>> delete(
               @Parameter(description = "Complaint ID") @PathVariable String id) {
          log.info("Soft deleting complaint with ID: {}", id);
          return deleteComplaintUseCase.execute(id)
                    .map(complaintMapper::toResponse)
                    .map(deleted -> ResponseEntity.ok(ApiResponse.success(deleted, "Complaint deleted successfully")));
     }

     @Operation(summary = "Restore complaint")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Complaint restored successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Complaint not found")
     })
     @PatchMapping("/{id}/restore")
     public Mono<ResponseEntity<ApiResponse<ComplaintResponse>>> restore(
               @Parameter(description = "Complaint ID") @PathVariable String id) {
          log.info("Restoring complaint with ID: {}", id);
          return restoreComplaintUseCase.execute(id)
                    .map(complaintMapper::toResponse)
                    .map(restored -> ResponseEntity
                              .ok(ApiResponse.success(restored, "Complaint restored successfully")));
     }
}
