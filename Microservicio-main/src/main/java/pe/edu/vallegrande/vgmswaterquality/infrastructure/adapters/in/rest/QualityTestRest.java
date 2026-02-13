package pe.edu.vallegrande.vgmswaterquality.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmswaterquality.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmswaterquality.application.dto.request.CreateQualityTestRequest;
import pe.edu.vallegrande.vgmswaterquality.application.dto.request.UpdateQualityTestRequest;
import pe.edu.vallegrande.vgmswaterquality.application.dto.response.QualityTestResponse;
import pe.edu.vallegrande.vgmswaterquality.application.mappers.QualityTestMapper;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.in.qualitytest.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/quality-tests")
@RequiredArgsConstructor
public class QualityTestRest {

    private final ICreateQualityTestUseCase createUseCase;
    private final IUpdateQualityTestUseCase updateUseCase;
    private final IGetQualityTestUseCase getUseCase;
    private final IDeleteQualityTestUseCase deleteUseCase;
    private final IRestoreQualityTestUseCase restoreUseCase;
    private final QualityTestMapper mapper;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<QualityTestResponse>>> create(
            @Valid @RequestBody CreateQualityTestRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return createUseCase.execute(mapper.toDomain(request), userId)
                .map(test -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(mapper.toResponse(test), "Prueba de calidad creada exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<QualityTestResponse>>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateQualityTestRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return updateUseCase.execute(id, mapper.toDomain(request), userId)
                .map(test -> ResponseEntity.ok(
                        ApiResponse.success(mapper.toResponse(test), "Prueba de calidad actualizada exitosamente")));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<QualityTestResponse>>> getById(@PathVariable String id) {
        return getUseCase.findByAll(id)
                .map(test -> ResponseEntity
                        .ok(ApiResponse.success(mapper.toResponse(test), "Prueba de calidad encontrada")));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<QualityTestResponse>>>> findAll() {
        return getUseCase.findAll()
                .map(mapper::toResponse)
                .collectList()
                .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Listado de pruebas de calidad")));
    }

    @GetMapping("/active")
    public Mono<ResponseEntity<ApiResponse<List<QualityTestResponse>>>> findAllActive() {
        return getUseCase.findAllActive()
                .map(mapper::toResponse)
                .collectList()
                .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Listado de pruebas activas")));
    }

    @GetMapping("/organization/{organizationId}")
    public Mono<ResponseEntity<ApiResponse<List<QualityTestResponse>>>> findByOrganization(
            @PathVariable String organizationId) {
        return getUseCase.findByOrganizationId(organizationId)
                .map(mapper::toResponse)
                .collectList()
                .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Pruebas por organización")));
    }

    @GetMapping("/organization/{organizationId}/active")
    public Mono<ResponseEntity<ApiResponse<List<QualityTestResponse>>>> findActiveByOrganization(
            @PathVariable String organizationId) {
        return getUseCase.findActiveByOrganizationId(organizationId)
                .map(mapper::toResponse)
                .collectList()
                .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Pruebas activas por organización")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> delete(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return deleteUseCase.execute(id, userId)
                .then(Mono.just(
                        ResponseEntity.ok(ApiResponse.<Void>success("Prueba de calidad eliminada exitosamente"))));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ResponseEntity<ApiResponse<QualityTestResponse>>> restore(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return restoreUseCase.execute(id, userId)
                .map(test -> ResponseEntity
                        .ok(ApiResponse.success(mapper.toResponse(test), "Prueba de calidad restaurada exitosamente")));
    }
}
