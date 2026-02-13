package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.request.CreateWaterBoxRequest;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.request.UpdateWaterBoxRequest;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.response.WaterBoxResponse;
import pe.edu.vallegrande.vgmsinfrastructure.application.mappers.WaterBoxMapper;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.ICreateWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IDeleteWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IGetWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IReconnectWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IRestoreWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.ISuspendWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IUpdateWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.security.GatewayHeadersExtractor;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/water-boxes")
@RequiredArgsConstructor
public class WaterBoxRest {

    private final ICreateWaterBoxUseCase createUseCase;
    private final IGetWaterBoxUseCase getUseCase;
    private final IUpdateWaterBoxUseCase updateUseCase;
    private final IDeleteWaterBoxUseCase deleteUseCase;
    private final IRestoreWaterBoxUseCase restoreUseCase;
    private final ISuspendWaterBoxUseCase suspendUseCase;
    private final IReconnectWaterBoxUseCase reconnectUseCase;
    private final WaterBoxMapper mapper;
    private final GatewayHeadersExtractor headersExtractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<WaterBoxResponse>> create(@Valid @RequestBody CreateWaterBoxRequest request,
                                                       ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("POST /water-boxes - Creating water box by {}", username);
        return createUseCase.execute(mapper.toDomain(request), username)
                .map(mapper::toResponse)
                .map(ApiResponse::created);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<WaterBoxResponse>> findById(@PathVariable String id) {
        log.info("GET /water-boxes/{}", id);
        return getUseCase.findById(id)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @GetMapping
    public Mono<ApiResponse<List<WaterBoxResponse>>> findAll() {
        log.info("GET /water-boxes");
        return getUseCase.findAll()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/active")
    public Mono<ApiResponse<List<WaterBoxResponse>>> findAllActive() {
        log.info("GET /water-boxes/active");
        return getUseCase.findAllActive()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/zone/{zoneId}")
    public Mono<ApiResponse<List<WaterBoxResponse>>> findByZoneId(@PathVariable String zoneId) {
        log.info("GET /water-boxes/zone/{}", zoneId);
        return getUseCase.findByZoneId(zoneId)
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<WaterBoxResponse>> update(@PathVariable String id,
                                                       @Valid @RequestBody UpdateWaterBoxRequest request,
                                                       ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PUT /water-boxes/{} by {}", id, username);
        return updateUseCase.execute(id, mapper.toDomain(request), username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> delete(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("DELETE /water-boxes/{} by {}", id, username);
        return deleteUseCase.execute(id, username)
                .then(Mono.just(ApiResponse.<Void>ok(null)));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ApiResponse<WaterBoxResponse>> restore(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PATCH /water-boxes/{}/restore by {}", id, username);
        return restoreUseCase.execute(id, username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @PatchMapping("/{id}/suspend")
    public Mono<ApiResponse<WaterBoxResponse>> suspend(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PATCH /water-boxes/{}/suspend by {}", id, username);
        return suspendUseCase.execute(id, username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @PatchMapping("/{id}/reconnect")
    public Mono<ApiResponse<WaterBoxResponse>> reconnect(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PATCH /water-boxes/{}/reconnect by {}", id, username);
        return reconnectUseCase.execute(id, username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }
}
