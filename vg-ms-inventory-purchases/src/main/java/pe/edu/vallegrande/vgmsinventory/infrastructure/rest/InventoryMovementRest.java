package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response.InventoryMovementResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.application.mappers.InventoryMovementMapper;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.InventoryMovement;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.MovementType;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in.IInventoryMovementUseCase;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.security.GatewayHeadersExtractor;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/inventory-movements")
@RequiredArgsConstructor
public class InventoryMovementRest {

    private final IInventoryMovementUseCase useCase;
    private final InventoryMovementMapper mapper;
    private final GatewayHeadersExtractor headersExtractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<InventoryMovementResponse>> create(@RequestBody InventoryMovement movement,
            ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("POST /inventory-movements - Creating movement by {}", username);
        return useCase.create(movement, username)
                .map(mapper::toResponse)
                .map(ApiResponse::created);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<InventoryMovementResponse>> findById(@PathVariable String id) {
        log.info("GET /inventory-movements/{}", id);
        return useCase.findById(id)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @GetMapping
    public Mono<ApiResponse<List<InventoryMovementResponse>>> findAll() {
        log.info("GET /inventory-movements");
        return useCase.findAll()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/material/{materialId}")
    public Mono<ApiResponse<List<InventoryMovementResponse>>> findByMaterialId(@PathVariable String materialId) {
        log.info("GET /inventory-movements/material/{}", materialId);
        return useCase.findByMaterialId(materialId)
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }
}
