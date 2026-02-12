package pe.edu.vallegrande.msdistribution.infrastructure.adapters.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.msdistribution.application.dto.common.ApiResponse;
import pe.edu.vallegrande.msdistribution.application.dto.request.DistributionRouteCreateRequest;
import pe.edu.vallegrande.msdistribution.application.dto.response.DistributionRouteResponse;
import pe.edu.vallegrande.msdistribution.application.mappers.DistributionRouteMapper;
import pe.edu.vallegrande.msdistribution.domain.ports.in.route.*;
import pe.edu.vallegrande.msdistribution.infrastructure.security.AuthenticatedUser;
import pe.edu.vallegrande.msdistribution.infrastructure.security.GatewayHeadersExtractor;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class DistributionRouteRest {

    private final ICreateDistributionRouteUseCase createUseCase;
    private final IGetDistributionRouteUseCase getUseCase;
    private final IUpdateDistributionRouteUseCase updateUseCase;
    private final IDeleteDistributionRouteUseCase deleteUseCase;
    private final IRestoreDistributionRouteUseCase restoreUseCase;
    private final DistributionRouteMapper mapper;
    private final GatewayHeadersExtractor headersExtractor;

    @GetMapping
    public Mono<ApiResponse<List<DistributionRouteResponse>>> getAll() {
        return getUseCase.findAll().map(mapper::toResponse).collectList().map(ApiResponse::ok);
    }

    @GetMapping("/active")
    public Mono<ApiResponse<List<DistributionRouteResponse>>> getAllActive() {
        return getUseCase.findAllActive().map(mapper::toResponse).collectList().map(ApiResponse::ok);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<DistributionRouteResponse>> getById(@PathVariable String id) {
        return getUseCase.findById(id).map(mapper::toResponse).map(ApiResponse::ok);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<DistributionRouteResponse>> create(
            @Valid @RequestBody DistributionRouteCreateRequest request, ServerWebExchange exchange) {
        AuthenticatedUser user = headersExtractor.extract(exchange);
        return createUseCase.execute(mapper.toDomain(request), user.getUserId())
                .map(mapper::toResponse).map(ApiResponse::created);
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<DistributionRouteResponse>> update(
            @PathVariable String id, @Valid @RequestBody DistributionRouteCreateRequest request,
            ServerWebExchange exchange) {
        AuthenticatedUser user = headersExtractor.extract(exchange);
        return updateUseCase.execute(id, mapper.toDomain(request), user.getUserId())
                .map(mapper::toResponse).map(ApiResponse::ok);
    }

    @PatchMapping("/{id}/deactivate")
    public Mono<ApiResponse<Void>> deactivate(@PathVariable String id, ServerWebExchange exchange) {
        AuthenticatedUser user = headersExtractor.extract(exchange);
        return deleteUseCase.execute(id, user.getUserId(), "Deactivated")
                .then(Mono.just(ApiResponse.<Void>ok(null, "Route deactivated")));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ApiResponse<Void>> restore(@PathVariable String id, ServerWebExchange exchange) {
        AuthenticatedUser user = headersExtractor.extract(exchange);
        return restoreUseCase.execute(id, user.getUserId())
                .then(Mono.just(ApiResponse.<Void>ok(null, "Route restored")));
    }
}
