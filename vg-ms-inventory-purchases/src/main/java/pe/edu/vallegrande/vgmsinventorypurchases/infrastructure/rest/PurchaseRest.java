package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.CreatePurchaseRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response.PurchaseResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.application.mappers.PurchaseMapper;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in.IPurchaseUseCase;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.security.GatewayHeadersExtractor;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseRest {

    private final IPurchaseUseCase useCase;
    private final PurchaseMapper mapper;
    private final GatewayHeadersExtractor headersExtractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<PurchaseResponse>> create(@Valid @RequestBody CreatePurchaseRequest request,
            ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("POST /purchases - Creating purchase by {}", username);
        return useCase.create(mapper.toDomain(request), username)
                .map(mapper::toResponse)
                .map(ApiResponse::created);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<PurchaseResponse>> findById(@PathVariable String id) {
        log.info("GET /purchases/{}", id);
        return useCase.findById(id)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @GetMapping
    public Mono<ApiResponse<List<PurchaseResponse>>> findAll() {
        log.info("GET /purchases");
        return useCase.findAll()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/active")
    public Mono<ApiResponse<List<PurchaseResponse>>> findAllActive() {
        log.info("GET /purchases/active");
        return useCase.findAllActive()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/supplier/{supplierId}")
    public Mono<ApiResponse<List<PurchaseResponse>>> findBySupplierId(@PathVariable String supplierId) {
        log.info("GET /purchases/supplier/{}", supplierId);
        return useCase.findBySupplierId(supplierId)
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<PurchaseResponse>> update(@PathVariable String id,
            @Valid @RequestBody CreatePurchaseRequest request,
            ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PUT /purchases/{} by {}", id, username);
        return useCase.update(id, mapper.toDomain(request), username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> delete(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("DELETE /purchases/{} by {}", id, username);
        return useCase.softDelete(id, username)
                .then(Mono.just(ApiResponse.<Void>ok(null)));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ApiResponse<PurchaseResponse>> restore(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PATCH /purchases/{}/restore by {}", id, username);
        return useCase.restore(id, username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @PatchMapping("/{id}/receive")
    public Mono<ApiResponse<PurchaseResponse>> receive(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PATCH /purchases/{}/receive by {}", id, username);
        return useCase.receive(id, username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @PatchMapping("/{id}/cancel")
    public Mono<ApiResponse<PurchaseResponse>> cancel(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PATCH /purchases/{}/cancel by {}", id, username);
        return useCase.cancel(id, username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }
}
