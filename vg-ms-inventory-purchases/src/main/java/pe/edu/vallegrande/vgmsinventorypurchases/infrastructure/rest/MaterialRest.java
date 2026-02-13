package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.CreateMaterialRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.UpdateMaterialRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response.MaterialResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.application.mappers.MaterialMapper;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in.IMaterialUseCase;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.security.GatewayHeadersExtractor;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/materials")
@RequiredArgsConstructor
public class MaterialRest {

    private final IMaterialUseCase useCase;
    private final MaterialMapper mapper;
    private final GatewayHeadersExtractor headersExtractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<MaterialResponse>> create(@Valid @RequestBody CreateMaterialRequest request,
            ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("POST /materials - Creating material by {}", username);
        return useCase.create(mapper.toDomain(request), username)
                .map(mapper::toResponse)
                .map(ApiResponse::created);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<MaterialResponse>> findById(@PathVariable String id) {
        log.info("GET /materials/{}", id);
        return useCase.findById(id)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @GetMapping
    public Mono<ApiResponse<List<MaterialResponse>>> findAll() {
        log.info("GET /materials");
        return useCase.findAll()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/active")
    public Mono<ApiResponse<List<MaterialResponse>>> findAllActive() {
        log.info("GET /materials/active");
        return useCase.findAllActive()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/category/{categoryId}")
    public Mono<ApiResponse<List<MaterialResponse>>> findByCategoryId(@PathVariable String categoryId) {
        log.info("GET /materials/category/{}", categoryId);
        return useCase.findByCategoryId(categoryId)
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<MaterialResponse>> update(@PathVariable String id,
            @Valid @RequestBody UpdateMaterialRequest request,
            ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PUT /materials/{} by {}", id, username);
        return useCase.update(id, mapper.toDomain(request), username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> delete(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("DELETE /materials/{} by {}", id, username);
        return useCase.softDelete(id, username)
                .then(Mono.just(ApiResponse.<Void>ok(null)));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ApiResponse<MaterialResponse>> restore(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PATCH /materials/{}/restore by {}", id, username);
        return useCase.restore(id, username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }
}
