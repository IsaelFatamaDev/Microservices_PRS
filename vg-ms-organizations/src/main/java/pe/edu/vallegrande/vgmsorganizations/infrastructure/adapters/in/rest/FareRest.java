package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsorganizations.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.CreateFareRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.UpdateFareRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.response.FareResponse;
import pe.edu.vallegrande.vgmsorganizations.application.mappers.FareMapper;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.fare.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fares")
@RequiredArgsConstructor
public class FareRest {

    private final ICreateFareUseCase createUseCase;
    private final IGetFareUseCase getUseCase;
    private final IUpdateFareUseCase updateUseCase;
    private final IDeleteFareUseCase deleteUseCase;
    private final IRestoreFareUseCase restoreUseCase;
    private final FareMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<FareResponse>> create(
        @Valid @RequestBody CreateFareRequest request,
        @RequestHeader("X-User-Id") String userId) {
        return createUseCase.execute(mapper.toModel(request), userId)
            .map(f -> ApiResponse.success(mapper.toResponse(f), "Fare created successfully"));
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<FareResponse>> findById(@PathVariable String id) {
        return getUseCase.findById(id)
            .map(f -> ApiResponse.success(mapper.toResponse(f), "Fare found"));
    }

    @GetMapping
    public Mono<ApiResponse<List<FareResponse>>> findAllActive() {
        return getUseCase.findAllActive()
            .map(mapper::toResponse)
            .collectList()
            .map(list -> ApiResponse.success(list, "Active fares retrieved"));
    }

    @GetMapping("/all")
    public Mono<ApiResponse<List<FareResponse>>> findAll() {
        return getUseCase.findAll()
            .map(mapper::toResponse)
            .collectList()
            .map(list -> ApiResponse.success(list, "All fares retrieved"));
    }

    @GetMapping("/organization/{organizationId}")
    public Mono<ApiResponse<List<FareResponse>>> findByOrganizationId(@PathVariable String organizationId) {
        return getUseCase.findByOrganizationId(organizationId)
            .map(mapper::toResponse)
            .collectList()
            .map(list -> ApiResponse.success(list, "Fares by organization retrieved"));
    }

    @GetMapping("/organization/{organizationId}/active")
    public Mono<ApiResponse<List<FareResponse>>> findActiveByOrganizationId(@PathVariable String organizationId) {
        return getUseCase.findActiveByOrganizationId(organizationId)
            .map(mapper::toResponse)
            .collectList()
            .map(list -> ApiResponse.success(list, "Active fares by organization retrieved"));
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<FareResponse>> update(
        @PathVariable String id,
        @Valid @RequestBody UpdateFareRequest request,
        @RequestHeader("X-User-Id") String userId) {
        return updateUseCase.execute(id, mapper.toModel(request), userId)
            .map(f -> ApiResponse.success(mapper.toResponse(f), "Fare updated successfully"));
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<FareResponse>> delete(
        @PathVariable String id,
        @RequestHeader("X-User-Id") String userId,
        @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "No reason provided") : "No reason provided";
        return deleteUseCase.execute(id, userId, reason)
            .map(f -> ApiResponse.success(mapper.toResponse(f), "Fare deleted successfully"));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ApiResponse<FareResponse>> restore(
        @PathVariable String id,
        @RequestHeader("X-User-Id") String userId) {
        return restoreUseCase.execute(id, userId)
            .map(f -> ApiResponse.success(mapper.toResponse(f), "Fare restored successfully"));
    }
}