package pe.edu.vallegrande.vgmsinfrastructure.application.usecases.waterbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.ConflictException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBox;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.ICreateWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IInfrastructureEventPublisher;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateWaterBoxUseCaseImpl implements ICreateWaterBoxUseCase {

    private final IWaterBoxRepository repository;
    private final IInfrastructureEventPublisher eventPublisher;

    @Override
    public Mono<WaterBox> execute(WaterBox waterBox, String createdBy) {
        log.info("Creating water box with code: {}", waterBox.getBoxCode());
        return repository.existsByBoxCode(waterBox.getBoxCode())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ConflictException("Water box with code '" + waterBox.getBoxCode() + "' already exists"));
                    }
                    LocalDateTime now = LocalDateTime.now();
                    WaterBox newWaterBox = waterBox.toBuilder()
                            .isActive(true)
                            .recordStatus(RecordStatus.ACTIVE)
                            .createdAt(now)
                            .createdBy(createdBy)
                            .updatedAt(now)
                            .updatedBy(createdBy)
                            .build();
                    return repository.save(newWaterBox);
                })
                .flatMap(saved -> eventPublisher.publishWaterBoxCreated(saved.getId(), saved.getBoxCode(), createdBy)
                        .thenReturn(saved))
                .doOnSuccess(saved -> log.info("Water box created successfully: {}", saved.getId()))
                .doOnError(error -> log.error("Error creating water box: {}", error.getMessage()));
    }
}
