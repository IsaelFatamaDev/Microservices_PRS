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
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.request.TransferWaterBoxRequest;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.response.WaterBoxTransferResponse;
import pe.edu.vallegrande.vgmsinfrastructure.application.mappers.WaterBoxTransferMapper;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IGetWaterBoxTransferUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.ITransferWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.security.GatewayHeadersExtractor;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/water-box-transfers")
@RequiredArgsConstructor
public class WaterBoxTransferRest {

    private final ITransferWaterBoxUseCase transferUseCase;
    private final IGetWaterBoxTransferUseCase getUseCase;
    private final WaterBoxTransferMapper mapper;
    private final GatewayHeadersExtractor headersExtractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<WaterBoxTransferResponse>> transfer(@Valid @RequestBody TransferWaterBoxRequest request,
                                                                 ServerHttpRequest httpRequest) {
        String username = headersExtractor.extractUsername(httpRequest);
        log.info("POST /water-box-transfers - Transferring water box {} from {} to {} by {}",
                request.getWaterBoxId(), request.getFromUserId(), request.getToUserId(), username);
        return transferUseCase.execute(
                        request.getWaterBoxId(),
                        request.getFromUserId(),
                        request.getToUserId(),
                        request.getTransferFee(),
                        request.getNotes(),
                        username)
                .map(mapper::toResponse)
                .map(ApiResponse::created);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<WaterBoxTransferResponse>> findById(@PathVariable String id) {
        log.info("GET /water-box-transfers/{}", id);
        return getUseCase.findById(id)
                .map(mapper::toResponse)
                .map(ApiResponse::ok);
    }

    @GetMapping
    public Mono<ApiResponse<List<WaterBoxTransferResponse>>> findAll() {
        log.info("GET /water-box-transfers");
        return getUseCase.findAll()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/water-box/{waterBoxId}")
    public Mono<ApiResponse<List<WaterBoxTransferResponse>>> findByWaterBoxId(@PathVariable String waterBoxId) {
        log.info("GET /water-box-transfers/water-box/{}", waterBoxId);
        return getUseCase.findByWaterBoxId(waterBoxId)
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::ok);
    }
}
