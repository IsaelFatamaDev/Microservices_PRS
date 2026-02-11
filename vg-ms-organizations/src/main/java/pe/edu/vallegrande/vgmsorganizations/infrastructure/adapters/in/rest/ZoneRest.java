package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsorganizations.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.CreateZoneRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.UpdateZoneRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.response.ZoneResponse;
import pe.edu.vallegrande.vgmsorganizations.application.mappers.ZoneMapper;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.street.IGetStreetUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.zone.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/zones")
@RequiredArgsConstructor
public class ZoneRest {

    private final ICreateZoneUseCase createUseCase;
    private final IGetZoneUseCase getUseCase;
    private final IUpdateZoneUseCase updateUseCase;
    private final IDeleteZoneUseCase deleteUseCase;
    private final IRestoreZoneUseCase restoreUseCase;
    private final ZoneMapper mapper;
    private final IGetStreetUseCase getStreetUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<ZoneResponse>> create(
            @Valid @RequestBody CreateZoneRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return createUseCase.execute(mapper.toModel(request), userId)
                .map(z -> ApiResponse.success(mapper.toResponse(z), "Zone created successfully"));
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<ZoneResponse>> findById(@PathVariable String id) {
        return getUseCase.findById(id)
                .map(z -> ApiResponse.success(mapper.toResponse(z), "Zone found"));
    }

    @GetMapping
    public Mono<ApiResponse<List<ZoneResponse>>> findAllActive() {
        return getUseCase.findAllActive()
                .map(mapper::toResponse)
                .collectList()
                .map(list -> ApiResponse.success(list, "Active zones retrieved"));
    }

    @GetMapping("/all")
    public Mono<ApiResponse<List<ZoneResponse>>> findAll() {
        return getUseCase.findAll()
                .map(mapper::toResponse)
                .collectList()
                .map(list -> ApiResponse.success(list, "All zones retrieved"));
    }

    @GetMapping("/organization/{organizationId}")
    public Mono<ApiResponse<List<ZoneResponse>>> findByOrganizationId(@PathVariable String organizationId) {
        return getUseCase.findByOrganizationId(organizationId)
                .map(mapper::toResponse)
                .collectList()
                .map(list -> ApiResponse.success(list, "Zones by organization retrieved"));
    }

    @GetMapping("/organization/{organizationId}/active")
    public Mono<ApiResponse<List<ZoneResponse>>> findActiveByOrganizationId(@PathVariable String organizationId) {
        return getUseCase.findActiveByOrganizationId(organizationId)
                .map(mapper::toResponse)
                .collectList()
                .map(list -> ApiResponse.success(list, "Active zones by organization retrieved"));
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<ZoneResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateZoneRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return updateUseCase.execute(id, mapper.toModel(request), userId)
                .map(z -> ApiResponse.success(mapper.toResponse(z), "Zone updated successfully"));
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<ZoneResponse>> delete(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "No reason provided") : "No reason provided";
        return deleteUseCase.execute(id, userId, reason)
                .map(z -> ApiResponse.success(mapper.toResponse(z), "Zone deleted successfully"));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ApiResponse<ZoneResponse>> restore(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {
        return restoreUseCase.execute(id, userId)
                .map(z -> ApiResponse.success(mapper.toResponse(z), "Zone restored successfully"));
    }

    @GetMapping("/{zoneId}/streets/{streetId}/exists")
    public Mono<Boolean> existsStreet(
            @PathVariable String zoneId,
            @PathVariable String streetId) {
        return getStreetUseCase.findById(streetId)
                .filter(street -> street.getZoneId().equals(zoneId))
                .map(street -> true)
                .defaultIfEmpty(false);
    }
}