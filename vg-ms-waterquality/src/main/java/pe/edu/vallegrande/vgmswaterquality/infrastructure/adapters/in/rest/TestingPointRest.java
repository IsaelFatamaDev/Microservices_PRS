package pe.edu.vallegrande.vgmswaterquality.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmswaterquality.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmswaterquality.application.dto.request.CreateTestingPointRequest;
import pe.edu.vallegrande.vgmswaterquality.application.dto.request.UpdateTestingPointRequest;
import pe.edu.vallegrande.vgmswaterquality.application.dto.response.TestingPointResponse;
import pe.edu.vallegrande.vgmswaterquality.application.mappers.TestingPointMapper;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.in.testingpoint.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/testing-points")
@RequiredArgsConstructor
public class TestingPointRest {

    private final ICreateTestingPointUseCase createUseCase;
    private final IUpdateTestingPointUseCase updateUseCase;
    private final IDeleteTestingPointUseCase deleteUseCase;
    private final IRestoreTestingPointUseCase restoreUseCase;
    private final IGetTestingPointUseCase getUseCase;
    private final TestingPointMapper mapper;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<TestingPointResponse>>> create(
            @Valid @RequestBody CreateTestingPointRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return createUseCase.execute(mapper.toDomain(request), userId)
                .map(point -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(mapper.toResponse(point), "Punto de prueba creado exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<TestingPointResponse>>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateTestingPointRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return updateUseCase.execute(id, mapper.toDomain(request), userId)
                .map(point -> ResponseEntity
                        .ok(ApiResponse.success(mapper.toResponse(point), "Punto de prueba actualizado exitosamente")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<TestingPointResponse>>> delete(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId,
            @RequestParam(required = false) String reason) {
        return deleteUseCase.execute(id, userId, reason)
                .map(point -> ResponseEntity
                        .ok(ApiResponse.success(mapper.toResponse(point), "Punto de prueba eliminado exitosamente")));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ResponseEntity<ApiResponse<TestingPointResponse>>> restore(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return restoreUseCase.execute(id, userId)
                .map(point -> ResponseEntity
                        .ok(ApiResponse.success(mapper.toResponse(point), "Punto de prueba restaurado exitosamente")));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<TestingPointResponse>>> getById(@PathVariable String id) {
        return getUseCase.findByAll(id)
                .map(point -> ResponseEntity
                        .ok(ApiResponse.success(mapper.toResponse(point), "Punto de prueba encontrado")));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<TestingPointResponse>>>> findAll() {
        return getUseCase.findAll()
                .map(mapper::toResponse)
                .collectList()
                .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Listado de puntos de prueba")));
    }

    @GetMapping("/active")
    public Mono<ResponseEntity<ApiResponse<List<TestingPointResponse>>>> findAllActive() {
        return getUseCase.findAllActive()
                .map(mapper::toResponse)
                .collectList()
                .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Listado de puntos activos")));
    }

    @GetMapping("/organization/{organizationId}")
    public Mono<ResponseEntity<ApiResponse<List<TestingPointResponse>>>> findByOrganization(
            @PathVariable String organizationId) {
        return getUseCase.findByOrganizationId(organizationId)
                .map(mapper::toResponse)
                .collectList()
                .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Puntos por organización")));
    }

    @GetMapping("/organization/{organizationId}/active")
    public Mono<ResponseEntity<ApiResponse<List<TestingPointResponse>>>> findActiveByOrganization(
            @PathVariable String organizationId) {
        return getUseCase.findActiveByOrganizationId(organizationId)
                .map(mapper::toResponse)
                .collectList()
                .map(list -> ResponseEntity.ok(ApiResponse.success(list, "Puntos activos por organización")));
    }
}
