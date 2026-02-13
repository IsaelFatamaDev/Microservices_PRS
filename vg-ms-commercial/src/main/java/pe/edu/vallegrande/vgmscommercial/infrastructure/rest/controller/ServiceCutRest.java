package pe.edu.vallegrande.vgmscommercial.infrastructure.rest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pe.edu.vallegrande.vgmscommercial.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.servicecut.CreateServiceCutRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.servicecut.UpdateServiceCutRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.ServiceCutResponse;
import pe.edu.vallegrande.vgmscommercial.application.mappers.ServiceCutMapper;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.servicecut.*;
import pe.edu.vallegrande.vgmscommercial.infrastructure.config.GatewayHeadersExtractor;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/service-cuts")
@RequiredArgsConstructor
public class ServiceCutRest {

     private final ICreateServiceCutUseCase createServiceCutUseCase;
     private final IGetServiceCutUseCase getServiceCutUseCase;
     private final IUpdateServiceCutUseCase updateServiceCutUseCase;
     private final IDeleteServiceCutUseCase deleteServiceCutUseCase;
     private final IRestoreServiceCutUseCase restoreServiceCutUseCase;
     private final IExecuteServiceCutUseCase executeServiceCutUseCase;
     private final IReconnectServiceUseCase reconnectServiceUseCase;
     private final ServiceCutMapper serviceCutMapper;
     private final GatewayHeadersExtractor headersExtractor;

     @PostMapping
     public Mono<ResponseEntity<ApiResponse<ServiceCutResponse>>> create(
               @Valid @RequestBody CreateServiceCutRequest request,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> {
                         var serviceCut = serviceCutMapper.toDomain(request, headers.getOrganizationId(),
                                   headers.getUserId());
                         return createServiceCutUseCase.execute(serviceCut);
                    })
                    .map(saved -> ResponseEntity.status(HttpStatus.CREATED)
                              .body(ApiResponse.success(serviceCutMapper.toResponse(saved), "Created successfully")));
     }

     @GetMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<ServiceCutResponse>>> findById(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getServiceCutUseCase.findById(id, headers.getOrganizationId()))
                    .map(sc -> ResponseEntity
                              .ok(ApiResponse.success(serviceCutMapper.toResponse(sc), "Found successfully")));
     }

     @GetMapping
     public Mono<ResponseEntity<ApiResponse<List<ServiceCutResponse>>>> findAll(
               @RequestParam(required = false) String status,
               @RequestParam(required = false) String userId,
               @RequestParam(required = false) Integer page,
               @RequestParam(required = false) Integer size,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getServiceCutUseCase
                              .findAll(headers.getOrganizationId(), status, userId, page, size)
                              .map(serviceCutMapper::toResponse)
                              .collectList())
                    .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Retrieved successfully")));
     }

     @GetMapping("/user/{userId}")
     public Mono<ResponseEntity<ApiResponse<List<ServiceCutResponse>>>> findByUserId(
               @PathVariable String userId,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getServiceCutUseCase.findByUserId(userId, headers.getOrganizationId())
                              .map(serviceCutMapper::toResponse)
                              .collectList())
                    .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Retrieved successfully")));
     }

     @GetMapping("/pending")
     public Mono<ResponseEntity<ApiResponse<List<ServiceCutResponse>>>> findPending(ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getServiceCutUseCase.findPending(headers.getOrganizationId())
                              .map(serviceCutMapper::toResponse)
                              .collectList())
                    .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Retrieved successfully")));
     }

     @PutMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<ServiceCutResponse>>> update(
               @PathVariable String id,
               @Valid @RequestBody UpdateServiceCutRequest request,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> {
                         var sc = pe.edu.vallegrande.vgmscommercial.domain.models.ServiceCut.builder()
                                   .scheduledDate(request.getScheduledDate())
                                   .notes(request.getNotes())
                                   .updatedBy(headers.getUserId())
                                   .build();
                         return updateServiceCutUseCase.execute(id, sc);
                    })
                    .map(saved -> ResponseEntity
                              .ok(ApiResponse.success(serviceCutMapper.toResponse(saved), "Updated successfully")));
     }

     @PatchMapping("/{id}/execute")
     public Mono<ResponseEntity<ApiResponse<ServiceCutResponse>>> execute(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> executeServiceCutUseCase.execute(id, headers.getOrganizationId()))
                    .map(saved -> ResponseEntity
                              .ok(ApiResponse.success(serviceCutMapper.toResponse(saved), "Executed successfully")));
     }

     @PatchMapping("/{id}/reconnect")
     public Mono<ResponseEntity<ApiResponse<ServiceCutResponse>>> reconnect(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> reconnectServiceUseCase.execute(id, headers.getOrganizationId()))
                    .map(saved -> ResponseEntity
                              .ok(ApiResponse.success(serviceCutMapper.toResponse(saved), "Reconnected successfully")));
     }

     @DeleteMapping("/{id}")
     public Mono<ResponseEntity<ApiResponse<Void>>> delete(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> deleteServiceCutUseCase.execute(id, headers.getOrganizationId()))
                    .then(Mono.just(ResponseEntity.ok(ApiResponse.<Void>success("Deleted successfully"))));
     }

     @PatchMapping("/{id}/restore")
     public Mono<ResponseEntity<ApiResponse<ServiceCutResponse>>> restore(
               @PathVariable String id,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> restoreServiceCutUseCase.execute(id, headers.getOrganizationId()))
                    .map(saved -> ResponseEntity
                              .ok(ApiResponse.success(serviceCutMapper.toResponse(saved), "Restored successfully")));
     }

     @GetMapping("/count")
     public Mono<ResponseEntity<ApiResponse<Long>>> count(
               @RequestParam(required = false) String status,
               ServerWebExchange exchange) {
          return headersExtractor.extract(exchange)
                    .flatMap(headers -> getServiceCutUseCase.count(headers.getOrganizationId(), status))
                    .map(count -> ResponseEntity.ok(ApiResponse.success(count, "Count retrieved")));
     }
}
