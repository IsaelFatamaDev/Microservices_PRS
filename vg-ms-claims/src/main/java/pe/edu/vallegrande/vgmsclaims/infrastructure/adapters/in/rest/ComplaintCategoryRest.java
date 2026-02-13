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
import pe.edu.vallegrande.vgmsclaims.application.dto.complaintcategory.ComplaintCategoryResponse;
import pe.edu.vallegrande.vgmsclaims.application.dto.complaintcategory.CreateComplaintCategoryRequest;
import pe.edu.vallegrande.vgmsclaims.application.dto.complaintcategory.UpdateComplaintCategoryRequest;
import pe.edu.vallegrande.vgmsclaims.application.mappers.ComplaintCategoryMapper;
import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaintcategory.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST controller for complaint categories management.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/complaint-categories")
@RequiredArgsConstructor
@Tag(name = "Complaint Categories", description = "Complaint categories management API")
public class ComplaintCategoryRest {

     private final ICreateComplaintCategoryUseCase createComplaintCategoryUseCase;
     private final IGetComplaintCategoryUseCase getComplaintCategoryUseCase;
     private final IUpdateComplaintCategoryUseCase updateComplaintCategoryUseCase;
     private final IDeleteComplaintCategoryUseCase deleteComplaintCategoryUseCase;
     private final IRestoreComplaintCategoryUseCase restoreComplaintCategoryUseCase;
     private final ComplaintCategoryMapper complaintCategoryMapper;

     @Operation(summary = "List all complaint categories")
     @GetMapping
     public Mono<ResponseEntity<ApiResponse<List<ComplaintCategoryResponse>>>> findAll() {
          log.info("Fetching all complaint categories");
          return getComplaintCategoryUseCase.findAll()
                    .map(complaintCategoryMapper::toResponse)
                    .collectList()
                    .map(categories -> ResponseEntity.ok(ApiResponse.success(categories)));
     }

     @Operation(summary = "Get complaint category by ID")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category found"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
     })
     @GetMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<ComplaintCategoryResponse>>> findById(
               @Parameter(description = "Category ID") @PathVariable String id) {
          log.info("Finding complaint category by ID: {}", id);
          return getComplaintCategoryUseCase.findById(id)
                    .map(complaintCategoryMapper::toResponse)
                    .map(category -> ResponseEntity.ok(ApiResponse.success(category)));
     }

     @Operation(summary = "Find complaint categories by organization")
     @GetMapping("/organization/{organizationId}")
     public Mono<ResponseEntity<ApiResponse<List<ComplaintCategoryResponse>>>> findByOrganization(
               @Parameter(description = "Organization ID") @PathVariable String organizationId) {
          log.info("Finding complaint categories by organization: {}", organizationId);
          return getComplaintCategoryUseCase.findByOrganizationId(organizationId)
                    .map(complaintCategoryMapper::toResponse)
                    .collectList()
                    .map(categories -> ResponseEntity.ok(ApiResponse.success(categories)));
     }

     @Operation(summary = "Find active complaint categories by organization")
     @GetMapping("/organization/{organizationId}/active")
     public Mono<ResponseEntity<ApiResponse<List<ComplaintCategoryResponse>>>> findActiveByOrganization(
               @Parameter(description = "Organization ID") @PathVariable String organizationId) {
          log.info("Finding active complaint categories by organization: {}", organizationId);
          return getComplaintCategoryUseCase.findActiveByOrganizationId(organizationId)
                    .map(complaintCategoryMapper::toResponse)
                    .collectList()
                    .map(categories -> ResponseEntity.ok(ApiResponse.success(categories)));
     }

     @Operation(summary = "Create new complaint category")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Category created successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data")
     })
     @PostMapping
     public Mono<ResponseEntity<ApiResponse<ComplaintCategoryResponse>>> create(
               @Valid @RequestBody CreateComplaintCategoryRequest request) {
          log.info("Creating new complaint category: {}", request.getCategoryName());
          ComplaintCategory category = complaintCategoryMapper.toDomain(request);
          return createComplaintCategoryUseCase.execute(category)
                    .map(complaintCategoryMapper::toResponse)
                    .map(created -> ResponseEntity.status(HttpStatus.CREATED)
                              .body(ApiResponse.success(created, "Complaint category created successfully")));
     }

     @Operation(summary = "Update complaint category")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category updated successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
     })
     @PutMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<ComplaintCategoryResponse>>> update(
               @Parameter(description = "Category ID") @PathVariable String id,
               @Valid @RequestBody UpdateComplaintCategoryRequest request) {
          log.info("Updating complaint category with ID: {}", id);
          ComplaintCategory updatedData = ComplaintCategory.builder()
                    .categoryName(request.getCategoryName())
                    .description(request.getDescription())
                    .priorityLevel(request.getPriorityLevel())
                    .maxResponseTime(request.getMaxResponseTime())
                    .build();
          return updateComplaintCategoryUseCase.execute(id, updatedData)
                    .map(complaintCategoryMapper::toResponse)
                    .map(updated -> ResponseEntity
                              .ok(ApiResponse.success(updated, "Complaint category updated successfully")));
     }

     @Operation(summary = "Soft delete complaint category")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category deleted successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
     })
     @DeleteMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<ComplaintCategoryResponse>>> delete(
               @Parameter(description = "Category ID") @PathVariable String id) {
          log.info("Soft deleting complaint category with ID: {}", id);
          return deleteComplaintCategoryUseCase.execute(id)
                    .map(complaintCategoryMapper::toResponse)
                    .map(deleted -> ResponseEntity
                              .ok(ApiResponse.success(deleted, "Complaint category deleted successfully")));
     }

     @Operation(summary = "Restore complaint category")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category restored successfully"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
     })
     @PatchMapping("/{id}/restore")
     public Mono<ResponseEntity<ApiResponse<ComplaintCategoryResponse>>> restore(
               @Parameter(description = "Category ID") @PathVariable String id) {
          log.info("Restoring complaint category with ID: {}", id);
          return restoreComplaintCategoryUseCase.execute(id)
                    .map(complaintCategoryMapper::toResponse)
                    .map(restored -> ResponseEntity
                              .ok(ApiResponse.success(restored, "Complaint category restored successfully")));
     }
}
