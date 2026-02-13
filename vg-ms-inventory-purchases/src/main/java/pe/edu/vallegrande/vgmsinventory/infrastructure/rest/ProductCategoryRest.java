package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.CreateProductCategoryRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.UpdateProductCategoryRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response.ProductCategoryResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.application.mappers.ProductCategoryMapper;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in.IProductCategoryUseCase;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.security.GatewayHeadersExtractor;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product-categories")
@RequiredArgsConstructor
public class ProductCategoryRest {

    private final IProductCategoryUseCase useCase;
    private final ProductCategoryMapper mapper;
    private final GatewayHeadersExtractor headersExtractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<ProductCategoryResponse>> create(@Valid @RequestBody CreateProductCategoryRequest request,
            ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("POST /product-categories - Creating category by {}", username);
        return useCase.create(mapper.toDomain(request), username)
                .map(mapper::toResponse)
                .map(ApiResponse::created);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<ProductCategoryResponse>> findById(@PathVariable String id) {
        log.info("GET /product-categories/{}", id);
        return useCase.findById(id)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @GetMapping
    public Mono<ApiResponse<List<ProductCategoryResponse>>> findAll() {
        log.info("GET /product-categories");
        return useCase.findAll()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/active")
    public Mono<ApiResponse<List<ProductCategoryResponse>>> findAllActive() {
        log.info("GET /product-categories/active");
        return useCase.findAllActive()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<ProductCategoryResponse>> update(@PathVariable String id,
            @Valid @RequestBody UpdateProductCategoryRequest request,
            ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PUT /product-categories/{} by {}", id, username);
        return useCase.update(id, mapper.toDomain(request), username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> delete(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("DELETE /product-categories/{} by {}", id, username);
        return useCase.softDelete(id, username)
                .then(Mono.just(ApiResponse.<Void>ok(null)));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ApiResponse<ProductCategoryResponse>> restore(@PathVariable String id, ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("PATCH /product-categories/{}/restore by {}", id, username);
        return useCase.restore(id, username)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }
}
