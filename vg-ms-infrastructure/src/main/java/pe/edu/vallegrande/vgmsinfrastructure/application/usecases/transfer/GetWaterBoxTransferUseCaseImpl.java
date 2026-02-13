package pe.edu.vallegrande.vgmsinfrastructure.application.usecases.transfer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxTransfer;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IGetWaterBoxTransferUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxTransferRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetWaterBoxTransferUseCaseImpl implements IGetWaterBoxTransferUseCase {

    private final IWaterBoxTransferRepository repository;

    @Override
    public Mono<WaterBoxTransfer> findById(String id) {
        log.debug("Finding transfer by ID: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Transfer not found with ID: " + id)));
    }

    @Override
    public Flux<WaterBoxTransfer> findAll() {
        log.debug("Finding all transfers");
        return repository.findAll();
    }

    @Override
    public Flux<WaterBoxTransfer> findByWaterBoxId(String waterBoxId) {
        log.debug("Finding transfers by water box: {}", waterBoxId);
        return repository.findByWaterBoxId(waterBoxId);
    }
}
