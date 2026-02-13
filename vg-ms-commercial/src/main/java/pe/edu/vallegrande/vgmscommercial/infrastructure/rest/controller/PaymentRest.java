package pe.edu.vallegrande.vgmscommercial.infrastructure.rest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pe.edu.vallegrande.vgmscommercial.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.payment.CreatePaymentRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.payment.UpdatePaymentRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.PaymentResponse;
import pe.edu.vallegrande.vgmscommercial.application.mappers.PaymentMapper;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.payment.*;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPaymentRepository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.config.GatewayHeadersExtractor;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentRest {

     private final ICreatePaymentUseCase createPaymentUseCase;
     private final IGetPaymentUseCase getPaymentUseCase;
     private final IUpdatePaymentUseCase updatePaymentUseCase;
     private final IDeletePaymentUseCase deletePaymentUseCase;
     private final IRestorePaymentUseCase restorePaymentUseCase;
     private final IPaymentRepository paymentRepository;
     private final PaymentMapper paymentMapper;
     private final GatewayHeadersExtractor headersExtractor;

     @PostMapping
     public Mono<ResponseEntity<ApiResponse<PaymentResponse>>> create(
               @Valid @RequestBody CreatePaymentRequest request,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> paymentRepository.generateReceiptNumber(headers.getOrganizationId())
                              .flatMap(receiptNumber -> {
                                   var payment = paymentMapper.toDomain(request, headers.getOrganizationId(),
                                             headers.getUserId(), receiptNumber);
                                   return createPaymentUseCase.execute(payment);
                              }))
                    .map(saved -> ResponseEntity.status(HttpStatus.CREATED)
                              .body(ApiResponse.success(paymentMapper.toResponse(saved), "Created successfully")));
     }

     @GetMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<PaymentResponse>>> findById(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getPaymentUseCase.findById(id, headers.getOrganizationId()))
                    .map(payment -> ResponseEntity
                              .ok(ApiResponse.success(paymentMapper.toResponse(payment), "Found successfully")));
     }

     @GetMapping
     public Mono<ResponseEntity<ApiResponse<List<PaymentResponse>>>> findAll(
               @RequestParam(required = false) String status,
               @RequestParam(required = false) String userId,
               @RequestParam(required = false) Integer page,
               @RequestParam(required = false) Integer size,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getPaymentUseCase
                              .findAll(headers.getOrganizationId(), status, userId, page, size)
                              .map(paymentMapper::toResponse)
                              .collectList())
                    .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Retrieved successfully")));
     }

     @GetMapping("/user/{userId}")
     public Mono<ResponseEntity<ApiResponse<List<PaymentResponse>>>> findByUserId(
               @PathVariable String userId,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getPaymentUseCase.findByUserId(userId, headers.getOrganizationId())
                              .map(paymentMapper::toResponse)
                              .collectList())
                    .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Retrieved successfully")));
     }

     @PutMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<PaymentResponse>>> update(
               @PathVariable String id,
               @Valid @RequestBody UpdatePaymentRequest request,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> {
                         var payment = pe.edu.vallegrande.vgmscommercial.domain.models.Payment.builder()
                                   .notes(request.getNotes())
                                   .updatedBy(headers.getUserId())
                                   .build();
                         return updatePaymentUseCase.execute(id, payment);
                    })
                    .map(saved -> ResponseEntity
                              .ok(ApiResponse.success(paymentMapper.toResponse(saved), "Updated successfully")));
     }

     @DeleteMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<Void>>> delete(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> deletePaymentUseCase.execute(id, headers.getOrganizationId()))
                    .then(Mono.just(ResponseEntity.ok(ApiResponse.<Void>success("Deleted successfully"))));
     }

     @PatchMapping("/{id}/restore")
     public Mono<ResponseEntity<ApiResponse<PaymentResponse>>> restore(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> restorePaymentUseCase.execute(id, headers.getOrganizationId()))
                    .map(saved -> ResponseEntity
                              .ok(ApiResponse.success(paymentMapper.toResponse(saved), "Restored successfully")));
     }

     @GetMapping("/count")
     public Mono<ResponseEntity<ApiResponse<Long>>> count(
               @RequestParam(required = false) String status,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getPaymentUseCase.count(headers.getOrganizationId(), status))
                    .map(count -> ResponseEntity.ok(ApiResponse.success(count, "Count retrieved")));
     }
}
