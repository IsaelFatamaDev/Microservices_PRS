package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsorganizations.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.CreateStreetRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.UpdateStreetRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.response.StreetResponse;
import pe.edu.vallegrande.vgmsorganizations.application.mappers.StreetMapper;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.street.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/streets")
@RequiredArgsConstructor
public class StreetRest {

    private final ICreateStreetUseCase createUseCase;
    private final IGetStreetUseCase getUseCase;
    private final IUpdateStreetUseCase updateUseCase;
    private final IDeleteStreetUseCase deleteUseCase;
    private final IRestoreStreetUseCase restoreUseCase;
    private final StreetMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<StreetResponse>> create(
        @Valid @RequestBody CreateStreetRequest request,
        @RequestHeader("X-User-Id") String userId) {
        return createUseCase.execute(mapper.toModel(request), userId)
            .map(s -> ApiResponse.success(mapper.toResponse(s), "Street created successfully"));
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<StreetResponse>> findById(@PathVariable String id) {
        return getUseCase.findById(id)
            .map(s -> ApiResponse.success(mapper.toResponse(s), "Street found"));
    }

    @GetMapping
    public Mono<ApiResponse<List<StreetResponse>>> findAllActive() {
        return getUseCase.findAllActive()
            .map(mapper::toResponse)
            .collectList()
            .map(list -> ApiResponse.success(list, "Active streets retrieved"));
    }

    @GetMapping("/all")
    public Mono<ApiResponse<List<StreetResponse>>> findAll() {
        return getUseCase.findAll()
            .map(mapper::toResponse)
            .collectList()
            .map(list -> ApiResponse.success(list, "All streets retrieved"));
    }

    @GetMapping("/zone/{zoneId}")
    public Mono<ApiResponse<List<StreetResponse>>> findByZoneId(@PathVariable String zoneId) {
        return getUseCase.findByZoneId(zoneId)
            .map(mapper::toResponse)
            .collectList()
            .map(list -> ApiResponse.success(list, "Streets by zone retrieved"));
    }

    @GetMapping("/zone/{zoneId}/active")
    public Mono<ApiResponse<List<StreetResponse>>> findActiveByZoneId(@PathVariable String zoneId) {
        return getUseCase.findActiveByZoneId(zoneId)
            .map(mapper::toResponse)
            .collectList()
            .map(list -> ApiResponse.success(list, "Active streets by zone retrieved"));
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<StreetResponse>> update(
        @PathVariable String id,
        @Valid @RequestBody UpdateStreetRequest request,
        @RequestHeader("X-User-Id") String userId) {
        return updateUseCase.execute(id, mapper.toModel(request), userId)
            .map(s -> ApiResponse.success(mapper.toResponse(s), "Street updated successfully"));
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<StreetResponse>> delete(
        @PathVariable String id,
        @RequestHeader("X-User-Id") String userId,
        @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "No reason provided") : "No reason provided";
        return deleteUseCase.execute(id, userId, reason)
            .map(s -> ApiResponse.success(mapper.toResponse(s), "Street deleted successfully"));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ApiResponse<StreetResponse>> restore(
        @PathVariable String id,
        @RequestHeader("X-User-Id") String userId) {
        return restoreUseCase.execute(id, userId)
            .map(s -> ApiResponse.success(mapper.toResponse(s), "Street restored successfully"));
    }
}