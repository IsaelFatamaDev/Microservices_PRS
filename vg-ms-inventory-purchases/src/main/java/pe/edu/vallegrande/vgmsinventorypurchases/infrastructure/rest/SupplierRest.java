package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.CreateSupplierRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.UpdateSupplierRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response.SupplierResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.application.mappers.SupplierMapper;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in.ISupplierUseCase;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.security.GatewayHeadersExtractor;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierRest {

    private final ISupplierUseCase useCase;
    private final SupplierMapper mapper;
    private final GatewayHeadersExtractor headersExtractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<SupplierResponse>> create(@Valid @RequestBody CreateSupplierRequest request,
            ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("POST /suppliers - Creating supplier by {}", username);
        return useCase.create(mapper.toDomain(request), username)
                .map(mapper::toResponse)
                .map(ApiResponse::created);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<SupplierResponse>> findById(@PathVariable String id) {
        log.info("GET /suppliers/{}", id);
        return useCase.findById(id)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @GetMapping
    public Mono<ApiResponse<List<SupplierResponse>>> findAll() {
        log.info("GET /suppliers");
        return useCase.findAll()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/active")
    public Mono<ApiResponse<List<SupplierResponse>>> findAllActive() {
        log.info("GET /suppliers/active");
        return useCase.findAllActive()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<SupplierResponse>> update(@PathVariable String id,
            @Valid @RequestBody UpdateSupplierRequest request,
            ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PUT /suppliers/{} by {}", id, username);
        return useCase.update(id, mapper.toDomain(request), username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> delete(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("DELETE /suppliers/{} by {}", id, username);
        return useCase.softDelete(id, username)
                .then(Mono.just(ApiResponse.<Void>ok(null)));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ApiResponse<SupplierResponse>> restore(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PATCH /suppliers/{}/restore by {}", id, username);
        return useCase.restore(id, username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }
}
