package pe.edu.vallegrande.vgmsclaims.infrastructure.adapters.in.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsclaims.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentResolution;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentResolutionRepository;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST controller for incident resolutions (read-only queries).
 * Resolution creation is handled through the Resolve Incident use case.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/incident-resolutions")
@RequiredArgsConstructor
@Tag(name = "Incident Resolutions", description = "Incident resolutions query API")
public class IncidentResolutionRest {

     private final IIncidentResolutionRepository incidentResolutionRepository;

     @Operation(summary = "Get resolution by ID")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resolution found"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Resolution not found")
     })
     @GetMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<IncidentResolution>>> findById(
               @Parameter(description = "Resolution ID") @PathVariable String id) {
          log.info("Finding resolution by ID: {}", id);
          return incidentResolutionRepository.findById(id)
                    .map(resolution -> ResponseEntity.ok(ApiResponse.success(resolution)));
     }

     @Operation(summary = "Get resolution by incident ID")
     @ApiResponses(value = {
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resolution found"),
               @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Resolution not found")
     })
     @GetMapping("/incident/{incidentId}")
     public Mono<ResponseEntity<ApiResponse<IncidentResolution>>> findByIncidentId(
               @Parameter(description = "Incident ID") @PathVariable String incidentId) {
          log.info("Finding resolution by incident ID: {}", incidentId);
          return incidentResolutionRepository.findByIncidentId(incidentId)
                    .map(resolution -> ResponseEntity.ok(ApiResponse.success(resolution)));
     }

     @Operation(summary = "Find resolutions by technician")
     @GetMapping("/technician/{technicianId}")
     public Mono<ResponseEntity<ApiResponse<List<IncidentResolution>>>> findByTechnician(
               @Parameter(description = "Technician user ID") @PathVariable String technicianId) {
          log.info("Finding resolutions by technician: {}", technicianId);
          return incidentResolutionRepository.findByTechnicianId(technicianId)
                    .collectList()
                    .map(resolutions -> ResponseEntity.ok(ApiResponse.success(resolutions)));
     }

     @Operation(summary = "Find resolutions by type")
     @GetMapping("/type/{resolutionType}")
     public Mono<ResponseEntity<ApiResponse<List<IncidentResolution>>>> findByResolutionType(
               @Parameter(description = "Resolution type") @PathVariable String resolutionType) {
          log.info("Finding resolutions by type: {}", resolutionType);
          return incidentResolutionRepository.findByResolutionType(resolutionType)
                    .collectList()
                    .map(resolutions -> ResponseEntity.ok(ApiResponse.success(resolutions)));
     }
}
