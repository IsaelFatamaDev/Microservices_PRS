package pe.edu.vallegrande.msdistribution.infrastructure.adapters.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.msdistribution.application.dto.common.ApiResponse;
import pe.edu.vallegrande.msdistribution.application.dto.request.DistributionProgramCreateRequest;
import pe.edu.vallegrande.msdistribution.application.dto.response.DistributionProgramResponse;
import pe.edu.vallegrande.msdistribution.application.mappers.DistributionProgramMapper;
import pe.edu.vallegrande.msdistribution.domain.ports.in.program.*;
import pe.edu.vallegrande.msdistribution.infrastructure.security.AuthenticatedUser;
import pe.edu.vallegrande.msdistribution.infrastructure.security.GatewayHeadersExtractor;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/programs")
@RequiredArgsConstructor
public class DistributionProgramRest {

    private final ICreateDistributionProgramUseCase createUseCase;
    private final IGetDistributionProgramUseCase getUseCase;
    private final IUpdateDistributionProgramUseCase updateUseCase;
    private final IDeleteDistributionProgramUseCase deleteUseCase;
    private final IRestoreDistributionProgramUseCase restoreUseCase;
    private final DistributionProgramMapper mapper;
    private final GatewayHeadersExtractor headersExtractor;

    @GetMapping
    public Mono<ApiResponse<List<DistributionProgramResponse>>> getAll() {
        return getUseCase.findAll()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/active")
    public Mono<ApiResponse<List<DistributionProgramResponse>>> getAllActive() {
        return getUseCase.findAllActive()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<DistributionProgramResponse>> getById(@PathVariable String id) {
        return getUseCase.findById(id)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<DistributionProgramResponse>> create(
            @Valid @RequestBody DistributionProgramCreateRequest request,
            ServerWebExchange exchange) {
        AuthenticatedUser user = headersExtractor.extract(exchange);
        return createUseCase.execute(mapper.toDomain(request), user.getUserId())
                .map(mapper::toResponse)
                .map(ApiResponse::created);
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<DistributionProgramResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody DistributionProgramCreateRequest request,
            ServerWebExchange exchange) {
        AuthenticatedUser user = headersExtractor.extract(exchange);
        return updateUseCase.execute(id, mapper.toDomain(request), user.getUserId())
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @PatchMapping("/{id}/deactivate")
    public Mono<ApiResponse<Void>> deactivate(@PathVariable String id, ServerWebExchange exchange) {
        AuthenticatedUser user = headersExtractor.extract(exchange);
        return deleteUseCase.execute(id, user.getUserId(), "Deactivated by user")
                .then(Mono.just(ApiResponse.<Void>ok(null, "Program deactivated")));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ApiResponse<Void>> restore(@PathVariable String id, ServerWebExchange exchange) {
        AuthenticatedUser user = headersExtractor.extract(exchange);
        return restoreUseCase.execute(id, user.getUserId())
                .then(Mono.just(ApiResponse.<Void>ok(null, "Program restored")));
    }
}
