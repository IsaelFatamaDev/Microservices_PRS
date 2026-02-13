package pe.edu.vallegrande.vgmscommercial.infrastructure.rest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pe.edu.vallegrande.vgmscommercial.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.receipt.CreateReceiptRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.receipt.UpdateReceiptRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.ReceiptResponse;
import pe.edu.vallegrande.vgmscommercial.application.mappers.ReceiptMapper;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.receipt.*;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IReceiptRepository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.config.GatewayHeadersExtractor;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/receipts")
@RequiredArgsConstructor
public class ReceiptRest {

     private final ICreateReceiptUseCase createReceiptUseCase;
     private final IGetReceiptUseCase getReceiptUseCase;
     private final IUpdateReceiptUseCase updateReceiptUseCase;
     private final IDeleteReceiptUseCase deleteReceiptUseCase;
     private final IRestoreReceiptUseCase restoreReceiptUseCase;
     private final IReceiptRepository receiptRepository;
     private final ReceiptMapper receiptMapper;
     private final GatewayHeadersExtractor headersExtractor;

     @PostMapping
     public Mono<ResponseEntity<ApiResponse<ReceiptResponse>>> create(
               @Valid @RequestBody CreateReceiptRequest request,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> receiptRepository
                              .generateReceiptNumber(headers.getOrganizationId(), request.getPeriodYear())
                              .flatMap(receiptNumber -> {
                                   LocalDateTime dueDate = LocalDateTime.of(request.getPeriodYear(),
                                             request.getPeriodMonth(), 28, 23, 59);
                                   var receipt = receiptMapper.toDomain(request, headers.getOrganizationId(),
                                             headers.getUserId(), receiptNumber, dueDate);
                                   return createReceiptUseCase.execute(receipt);
                              }))
                    .map(saved -> ResponseEntity.status(HttpStatus.CREATED)
                              .body(ApiResponse.success(receiptMapper.toResponse(saved), "Created successfully")));
     }

     @GetMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<ReceiptResponse>>> findById(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getReceiptUseCase.findById(id, headers.getOrganizationId()))
                    .map(receipt -> ResponseEntity
                              .ok(ApiResponse.success(receiptMapper.toResponse(receipt), "Found successfully")));
     }

     @GetMapping
     public Mono<ResponseEntity<ApiResponse<List<ReceiptResponse>>>> findAll(
               @RequestParam(required = false) String status,
               @RequestParam(required = false) String userId,
               @RequestParam(required = false) Integer page,
               @RequestParam(required = false) Integer size,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getReceiptUseCase
                              .findAll(headers.getOrganizationId(), status, userId, page, size)
                              .map(receiptMapper::toResponse)
                              .collectList())
                    .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Retrieved successfully")));
     }

     @GetMapping("/user/{userId}")
     public Mono<ResponseEntity<ApiResponse<List<ReceiptResponse>>>> findByUserId(
               @PathVariable String userId,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getReceiptUseCase.findByUserId(userId, headers.getOrganizationId())
                              .map(receiptMapper::toResponse)
                              .collectList())
                    .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Retrieved successfully")));
     }

     @GetMapping("/period")
     public Mono<ResponseEntity<ApiResponse<List<ReceiptResponse>>>> findByPeriod(
               @RequestParam Integer month,
               @RequestParam Integer year,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getReceiptUseCase.findByPeriod(month, year, headers.getOrganizationId())
                              .map(receiptMapper::toResponse)
                              .collectList())
                    .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Retrieved successfully")));
     }

     @PutMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<ReceiptResponse>>> update(
               @PathVariable String id,
               @Valid @RequestBody UpdateReceiptRequest request,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> {
                         var receipt = pe.edu.vallegrande.vgmscommercial.domain.models.Receipt.builder()
                                   .notes(request.getNotes())
                                   .updatedBy(headers.getUserId())
                                   .build();
                         return updateReceiptUseCase.execute(id, receipt);
                    })
                    .map(saved -> ResponseEntity
                              .ok(ApiResponse.success(receiptMapper.toResponse(saved), "Updated successfully")));
     }

     @DeleteMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<Void>>> delete(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> deleteReceiptUseCase.execute(id, headers.getOrganizationId()))
                    .then(Mono.just(ResponseEntity.ok(ApiResponse.<Void>success("Deleted successfully"))));
     }

     @PatchMapping("/{id}/restore")
     public Mono<ResponseEntity<ApiResponse<ReceiptResponse>>> restore(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> restoreReceiptUseCase.execute(id, headers.getOrganizationId()))
                    .map(saved -> ResponseEntity
                              .ok(ApiResponse.success(receiptMapper.toResponse(saved), "Restored successfully")));
     }

     @GetMapping("/count")
     public Mono<ResponseEntity<ApiResponse<Long>>> count(
               @RequestParam(required = false) String status,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getReceiptUseCase.count(headers.getOrganizationId(), status))
                    .map(count -> ResponseEntity.ok(ApiResponse.success(count, "Count retrieved")));
     }
}
