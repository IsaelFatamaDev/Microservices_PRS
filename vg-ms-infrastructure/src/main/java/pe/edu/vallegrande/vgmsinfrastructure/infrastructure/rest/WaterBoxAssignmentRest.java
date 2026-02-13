package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.request.AssignWaterBoxRequest;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.response.WaterBoxAssignmentResponse;
import pe.edu.vallegrande.vgmsinfrastructure.application.mappers.WaterBoxAssignmentMapper;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IAssignWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IGetWaterBoxAssignmentUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.security.GatewayHeadersExtractor;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/water-box-assignments")
@RequiredArgsConstructor
public class WaterBoxAssignmentRest {

    private final IAssignWaterBoxUseCase assignUseCase;
    private final IGetWaterBoxAssignmentUseCase getUseCase;
    private final WaterBoxAssignmentMapper mapper;
    private final GatewayHeadersExtractor headersExtractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<WaterBoxAssignmentResponse>> assign(@Valid @RequestBody AssignWaterBoxRequest request,
                                                                 ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("POST /water-box-assignments - Assigning water box {} to user {} by {}",
                request.getWaterBoxId(), request.getUserId(), username);
        return assignUseCase.execute(request.getWaterBoxId(), request.getUserId(), username)
                .map(mapper::toResponse)
                .map(ApiResponse::created);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<WaterBoxAssignmentResponse>> findById(@PathVariable String id) {
        log.info("GET /water-box-assignments/{}", id);
        return getUseCase.findById(id)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @GetMapping
    public Mono<ApiResponse<List<WaterBoxAssignmentResponse>>> findAll() {
        log.info("GET /water-box-assignments");
        return getUseCase.findAll()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/water-box/{waterBoxId}")
    public Mono<ApiResponse<List<WaterBoxAssignmentResponse>>> findByWaterBoxId(@PathVariable String waterBoxId) {
        log.info("GET /water-box-assignments/water-box/{}", waterBoxId);
        return getUseCase.findByWaterBoxId(waterBoxId)
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/user/{userId}")
    public Mono<ApiResponse<List<WaterBoxAssignmentResponse>>> findByUserId(@PathVariable String userId) {
        log.info("GET /water-box-assignments/user/{}", userId);
        return getUseCase.findByUserId(userId)
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }
}
