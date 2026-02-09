package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsorganizations.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.CreateParameterRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.UpdateParameterRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.response.ParameterResponse;
import pe.edu.vallegrande.vgmsorganizations.application.mappers.ParameterMapper;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.parameter.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/parameters")
@RequiredArgsConstructor
public class ParameterRest {

    private final ICreateParameterUseCase createUseCase;
    private final IGetParameterUseCase getUseCase;
    private final IUpdateParameterUseCase updateUseCase;
    private final IDeleteParameterUseCase deleteUseCase;
    private final IRestoreParameterUseCase restoreUseCase;
    private final ParameterMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<ParameterResponse>> create(
        @Valid @RequestBody CreateParameterRequest request,
        @RequestHeader("X-User-Id") String userId) {
        return createUseCase.execute(mapper.toModel(request), userId)
            .map(p -> ApiResponse.success(mapper.toResponse(p), "Parameter created successfully"));
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<ParameterResponse>> findById(@PathVariable String id) {
        return getUseCase.findById(id)
            .map(p -> ApiResponse.success(mapper.toResponse(p), "Parameter found"));
    }

    @GetMapping
    public Mono<ApiResponse<List<ParameterResponse>>> findAllActive() {
        return getUseCase.findAllActive()
            .map(mapper::toResponse)
            .collectList()
            .map(list -> ApiResponse.success(list, "Active parameters retrieved"));
    }

    @GetMapping("/all")
    public Mono<ApiResponse<List<ParameterResponse>>> findAll() {
        return getUseCase.findAll()
            .map(mapper::toResponse)
            .collectList()
            .map(list -> ApiResponse.success(list, "All parameters retrieved"));
    }

    @GetMapping("/organization/{organizationId}")
    public Mono<ApiResponse<List<ParameterResponse>>> findByOrganizationId(@PathVariable String organizationId) {
        return getUseCase.findByOrganizationId(organizationId)
            .map(mapper::toResponse)
            .collectList()
            .map(list -> ApiResponse.success(list, "Parameters by organization retrieved"));
    }

    @GetMapping("/organization/{organizationId}/active")
    public Mono<ApiResponse<List<ParameterResponse>>> findActiveByOrganizationId(@PathVariable String organizationId) {
        return getUseCase.findActiveByOrganizationId(organizationId)
            .map(mapper::toResponse)
            .collectList()
            .map(list -> ApiResponse.success(list, "Active parameters by organization retrieved"));
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<ParameterResponse>> update(
        @PathVariable String id,
        @Valid @RequestBody UpdateParameterRequest request,
        @RequestHeader("X-User-Id") String userId) {
        return updateUseCase.execute(id, mapper.toModel(request), userId)
            .map(p -> ApiResponse.success(mapper.toResponse(p), "Parameter updated successfully"));
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<ParameterResponse>> delete(
        @PathVariable String id,
        @RequestHeader("X-User-Id") String userId,
        @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "No reason provided") : "No reason provided";
        return deleteUseCase.execute(id, userId, reason)
            .map(p -> ApiResponse.success(mapper.toResponse(p), "Parameter deleted successfully"));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ApiResponse<ParameterResponse>> restore(
        @PathVariable String id,
        @RequestHeader("X-User-Id") String userId) {
        return restoreUseCase.execute(id, userId)
            .map(p -> ApiResponse.success(mapper.toResponse(p), "Parameter restored successfully"));
    }
}