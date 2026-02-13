package pe.edu.vallegrande.vgmscommercial.infrastructure.rest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pe.edu.vallegrande.vgmscommercial.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.pettycash.CreatePettyCashRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.pettycash.RegisterMovementRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.pettycash.UpdatePettyCashRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.PettyCashMovementResponse;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.PettyCashResponse;
import pe.edu.vallegrande.vgmscommercial.application.mappers.PettyCashMapper;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.pettycash.*;
import pe.edu.vallegrande.vgmscommercial.infrastructure.config.GatewayHeadersExtractor;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/petty-cash")
@RequiredArgsConstructor
public class PettyCashRest {

     private final ICreatePettyCashUseCase createPettyCashUseCase;
     private final IGetPettyCashUseCase getPettyCashUseCase;
     private final IUpdatePettyCashUseCase updatePettyCashUseCase;
     private final IDeletePettyCashUseCase deletePettyCashUseCase;
     private final IRestorePettyCashUseCase restorePettyCashUseCase;
     private final IRegisterMovementUseCase registerMovementUseCase;
     private final PettyCashMapper pettyCashMapper;
     private final GatewayHeadersExtractor headersExtractor;

     @PostMapping
     public Mono<ResponseEntity<ApiResponse<PettyCashResponse>>> create(
               @Valid @RequestBody CreatePettyCashRequest request,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> {
                         var pettyCash = pettyCashMapper.toDomain(request, headers.getOrganizationId(),
                                   headers.getUserId());
                         return createPettyCashUseCase.execute(pettyCash);
                    })
                    .map(saved -> ResponseEntity.status(HttpStatus.CREATED)
                              .body(ApiResponse.success(pettyCashMapper.toResponse(saved), "Created successfully")));
     }

     @GetMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<PettyCashResponse>>> findById(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getPettyCashUseCase.findById(id, headers.getOrganizationId()))
                    .map(pc -> ResponseEntity
                              .ok(ApiResponse.success(pettyCashMapper.toResponse(pc), "Found successfully")));
     }

     @GetMapping
     public Mono<ResponseEntity<ApiResponse<List<PettyCashResponse>>>> findAll(
               @RequestParam(required = false) String status,
               @RequestParam(required = false) Integer page,
               @RequestParam(required = false) Integer size,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getPettyCashUseCase.findAll(headers.getOrganizationId(), status, page, size)
                              .map(pettyCashMapper::toResponse)
                              .collectList())
                    .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Retrieved successfully")));
     }

     @GetMapping("/active")
     public Mono<ResponseEntity<ApiResponse<PettyCashResponse>>> findActive(ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getPettyCashUseCase.findActiveByOrganization(headers.getOrganizationId()))
                    .map(pc -> ResponseEntity
                              .ok(ApiResponse.success(pettyCashMapper.toResponse(pc), "Retrieved successfully")));
     }

     @GetMapping("/{id}/movements")
     public Mono<ResponseEntity<ApiResponse<List<PettyCashMovementResponse>>>> findMovements(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getPettyCashUseCase.findMovements(id, headers.getOrganizationId())
                              .map(pettyCashMapper::toMovementResponse)
                              .collectList())
                    .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Retrieved successfully")));
     }

     @PostMapping("/movements")
     public Mono<ResponseEntity<ApiResponse<PettyCashMovementResponse>>> registerMovement(
               @Valid @RequestBody RegisterMovementRequest request,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> {
                         var movement = pettyCashMapper.toMovementDomain(request, headers.getOrganizationId(),
                                   headers.getUserId(), 0.0, 0.0);
                         return registerMovementUseCase.execute(movement);
                    })
                    .map(saved -> ResponseEntity.status(HttpStatus.CREATED)
                              .body(ApiResponse.success(pettyCashMapper.toMovementResponse(saved),
                                        "Movement registered")));
     }

     @PutMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<PettyCashResponse>>> update(
               @PathVariable String id,
               @Valid @RequestBody UpdatePettyCashRequest request,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> {
                         var pc = pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash.builder()
                                   .maxAmountLimit(request.getMaxAmountLimit())
                                   .responsibleUserId(request.getResponsibleUserId())
                                   .updatedBy(headers.getUserId())
                                   .build();
                         return updatePettyCashUseCase.execute(id, pc);
                    })
                    .map(saved -> ResponseEntity
                              .ok(ApiResponse.success(pettyCashMapper.toResponse(saved), "Updated successfully")));
     }

     @DeleteMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<Void>>> delete(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> deletePettyCashUseCase.execute(id, headers.getOrganizationId()))
                    .then(Mono.just(ResponseEntity.ok(ApiResponse.<Void>success("Deleted successfully"))));
     }

     @PatchMapping("/{id}/restore")
     public Mono<ResponseEntity<ApiResponse<PettyCashResponse>>> restore(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> restorePettyCashUseCase.execute(id, headers.getOrganizationId()))
                    .map(saved -> ResponseEntity
                              .ok(ApiResponse.success(pettyCashMapper.toResponse(saved), "Restored successfully")));
     }

     @GetMapping("/count")
     public Mono<ResponseEntity<ApiResponse<Long>>> count(
               @RequestParam(required = false) String status,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getPettyCashUseCase.count(headers.getOrganizationId(), status))
                    .map(count -> ResponseEntity.ok(ApiResponse.success(count, "Count retrieved")));
     }
}
