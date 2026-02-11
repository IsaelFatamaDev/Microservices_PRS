package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsorganizations.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.CreateOrganizationRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.UpdateOrganizationRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.response.OrganizationResponse;
import pe.edu.vallegrande.vgmsorganizations.application.mappers.OrganizationMapper;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.organization.*;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.street.IGetStreetUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.zone.IGetZoneUseCase;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationRest {

        private final ICreateOrganizationUseCase createUseCase;
        private final IGetOrganizationUseCase getUseCase;
        private final IUpdateOrganizationUseCase updateUseCase;
        private final IDeleteOrganizationUseCase deleteUseCase;
        private final IRestoreOrganizationUseCase restoreUseCase;
        private final OrganizationMapper mapper;
        private final IGetStreetUseCase getStreetUseCase;
        private final IGetZoneUseCase getZoneUseCase;

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public Mono<ApiResponse<OrganizationResponse>> create(
                        @Valid @RequestBody CreateOrganizationRequest request,
                        @RequestHeader("X-User-Id") String userId) {
                return createUseCase.execute(mapper.toModel(request), userId)
                                .map(org -> ApiResponse.success(mapper.toResponse(org),
                                                "Organization created successfully"));
        }

        @GetMapping("/{id}")
        public Mono<ApiResponse<OrganizationResponse>> findById(@PathVariable String id) {
                return getUseCase.findById(id)
                                .map(org -> ApiResponse.success(mapper.toResponse(org), "Organization found"));
        }

        @GetMapping("/{id}/exists")
        public Mono<Boolean> existsOrganization(@PathVariable String id) {
                return getUseCase.findById(id)
                                .map(org -> true)
                                .defaultIfEmpty(false);
        }

        @GetMapping
        public Mono<ApiResponse<List<OrganizationResponse>>> findAllActive() {
                return getUseCase.findAllActive()
                                .map(mapper::toResponse)
                                .collectList()
                                .map(list -> ApiResponse.success(list, "Active organizations retrieved"));
        }

        @GetMapping("/all")
        public Mono<ApiResponse<List<OrganizationResponse>>> findAll() {
                return getUseCase.findAll()
                                .map(mapper::toResponse)
                                .collectList()
                                .map(list -> ApiResponse.success(list, "All organizations retrieved"));
        }

        @PutMapping("/{id}")
        public Mono<ApiResponse<OrganizationResponse>> update(
                        @PathVariable String id,
                        @Valid @RequestBody UpdateOrganizationRequest request,
                        @RequestHeader("X-User-Id") String userId) {
                return updateUseCase.execute(id, mapper.toModel(request), userId)
                                .map(org -> ApiResponse.success(mapper.toResponse(org),
                                                "Organization updated successfully"));
        }

        @DeleteMapping("/{id}")
        public Mono<ApiResponse<OrganizationResponse>> delete(
                        @PathVariable String id,
                        @RequestHeader("X-User-Id") String userId,
                        @RequestBody(required = false) Map<String, String> body) {
                String reason = body != null ? body.getOrDefault("reason", "No reason provided") : "No reason provided";
                return deleteUseCase.execute(id, userId, reason)
                                .map(org -> ApiResponse.success(mapper.toResponse(org),
                                                "Organization deleted successfully"));
        }

        @PatchMapping("/{id}/restore")
        public Mono<ApiResponse<OrganizationResponse>> restore(
                        @PathVariable String id,
                        @RequestHeader("X-User-Id") String userId) {
                return restoreUseCase.execute(id, userId)
                                .map(org -> ApiResponse.success(mapper.toResponse(org),
                                                "Organization restored successfully"));
        }

        @GetMapping("/{orgId}/zones/{zoneId}/exists")
        public Mono<Boolean> existsZone(
                        @PathVariable String orgId,
                        @PathVariable String zoneId) {
                return getZoneUseCase.findById(zoneId)
                                .filter(zone -> zone.getOrganizationId().equals(orgId))
                                .map(zone -> true)
                                .defaultIfEmpty(false);
        }

        @GetMapping("/{orgId}/zones/{zoneId}/streets/{streetId}/validate")
        public Mono<Boolean> validateStreet(
                        @PathVariable String orgId,
                        @PathVariable String zoneId,
                        @PathVariable String streetId) {
                return getStreetUseCase.findById(streetId)
                                .filter(street -> street.getOrganizationId().equals(orgId)
                                                && street.getZoneId().equals(zoneId))
                                .map(street -> true)
                                .defaultIfEmpty(false);
        }
}