package pe.edu.vallegrande.vgmscommercial.infrastructure.rest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pe.edu.vallegrande.vgmscommercial.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.debt.CreateDebtRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.debt.UpdateDebtRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.DebtResponse;
import pe.edu.vallegrande.vgmscommercial.application.mappers.DebtMapper;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.debt.*;
import pe.edu.vallegrande.vgmscommercial.infrastructure.config.GatewayHeadersExtractor;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/debts")
@RequiredArgsConstructor
public class DebtRest {

     private final ICreateDebtUseCase createDebtUseCase;
     private final IGetDebtUseCase getDebtUseCase;
     private final IUpdateDebtUseCase updateDebtUseCase;
     private final IDeleteDebtUseCase deleteDebtUseCase;
     private final IRestoreDebtUseCase restoreDebtUseCase;
     private final DebtMapper debtMapper;
     private final GatewayHeadersExtractor headersExtractor;

     @PostMapping
     public Mono<ResponseEntity<ApiResponse<DebtResponse>>> create(
               @Valid @RequestBody CreateDebtRequest request,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> {
                         LocalDateTime dueDate = LocalDateTime.of(request.getPeriodYear(), request.getPeriodMonth(), 28,
                                   23, 59);
                         var debt = debtMapper.toDomain(request, headers.getOrganizationId(), headers.getUserId(),
                                   dueDate);
                         return createDebtUseCase.execute(debt);
                    })
                    .map(saved -> ResponseEntity.status(HttpStatus.CREATED)
                              .body(ApiResponse.success(debtMapper.toResponse(saved), "Created successfully")));
     }

     @GetMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<DebtResponse>>> findById(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getDebtUseCase.findById(id, headers.getOrganizationId()))
                    .map(debt -> ResponseEntity
                              .ok(ApiResponse.success(debtMapper.toResponse(debt), "Found successfully")));
     }

     @GetMapping
     public Mono<ResponseEntity<ApiResponse<List<DebtResponse>>>> findAll(
               @RequestParam(required = false) String status,
               @RequestParam(required = false) String userId,
               @RequestParam(required = false) Integer page,
               @RequestParam(required = false) Integer size,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getDebtUseCase.findAll(headers.getOrganizationId(), status, userId, page, size)
                              .map(debtMapper::toResponse)
                              .collectList())
                    .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Retrieved successfully")));
     }

     @GetMapping("/user/{userId}")
     public Mono<ResponseEntity<ApiResponse<List<DebtResponse>>>> findByUserId(
               @PathVariable String userId,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getDebtUseCase.findByUserId(userId, headers.getOrganizationId())
                              .map(debtMapper::toResponse)
                              .collectList())
                    .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Retrieved successfully")));
     }

     @GetMapping("/pending")
     public Mono<ResponseEntity<ApiResponse<List<DebtResponse>>>> findPending(ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getDebtUseCase.findPendingDebts(headers.getOrganizationId())
                              .map(debtMapper::toResponse)
                              .collectList())
                    .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Retrieved successfully")));
     }

     @GetMapping("/user/{userId}/total")
     public Mono<ResponseEntity<ApiResponse<Double>>> getTotalDebtByUser(
               @PathVariable String userId,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getDebtUseCase.getTotalDebtByUser(userId, headers.getOrganizationId()))
                    .map(total -> ResponseEntity.ok(ApiResponse.success(total, "Total retrieved")));
     }

     @PutMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<DebtResponse>>> update(
               @PathVariable String id,
               @Valid @RequestBody UpdateDebtRequest request,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> {
                         var debtBuilder = pe.edu.vallegrande.vgmscommercial.domain.models.Debt.builder()
                                   .pendingAmount(request.getPendingAmount())
                                   .lateFee(request.getLateFee())
                                   .updatedBy(headers.getUserId());

                         // Map debtStatus from String to enum if provided
                         if (request.getDebtStatus() != null) {
                              try {
                                   debtBuilder.debtStatus(
                                             pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.DebtStatus
                                                       .valueOf(
                                                                 request.getDebtStatus().toUpperCase()));
                              } catch (IllegalArgumentException e) {
                                   log.warn("Invalid debt status: {}", request.getDebtStatus());
                              }
                         }

                         return updateDebtUseCase.execute(id, debtBuilder.build());
                    })
                    .map(saved -> ResponseEntity
                              .ok(ApiResponse.success(debtMapper.toResponse(saved), "Updated successfully")));
     }

     @DeleteMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<Void>>> delete(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> deleteDebtUseCase.execute(id, headers.getOrganizationId()))
                    .then(Mono.just(ResponseEntity.ok(ApiResponse.<Void>success("Deleted successfully"))));
     }

     @PatchMapping("/{id}/restore")
     public Mono<ResponseEntity<ApiResponse<DebtResponse>>> restore(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> restoreDebtUseCase.execute(id, headers.getOrganizationId()))
                    .map(saved -> ResponseEntity
                              .ok(ApiResponse.success(debtMapper.toResponse(saved), "Restored successfully")));
     }

     @GetMapping("/count")
     public Mono<ResponseEntity<ApiResponse<Long>>> count(
               @RequestParam(required = false) String status,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getDebtUseCase.count(headers.getOrganizationId(), status))
                    .map(count -> ResponseEntity.ok(ApiResponse.success(count, "Count retrieved")));
     }
}
