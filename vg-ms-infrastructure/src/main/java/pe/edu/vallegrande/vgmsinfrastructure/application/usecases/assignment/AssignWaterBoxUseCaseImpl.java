package pe.edu.vallegrande.vgmsinfrastructure.application.usecases.assignment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.WaterBoxAlreadyAssignedException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.WaterBoxNotFoundException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxAssignment;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.AssignmentStatus;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IAssignWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IInfrastructureEventPublisher;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxAssignmentRepository;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignWaterBoxUseCaseImpl implements IAssignWaterBoxUseCase {

    private final IWaterBoxRepository waterBoxRepository;
    private final IWaterBoxAssignmentRepository assignmentRepository;
    private final IInfrastructureEventPublisher eventPublisher;

    @Override
    public Mono<WaterBoxAssignment> execute(String waterBoxId, String userId, String assignedBy) {
        log.info("Assigning water box {} to user {}", waterBoxId, userId);
        return waterBoxRepository.findById(waterBoxId)
                .switchIfEmpty(Mono.error(new WaterBoxNotFoundException(waterBoxId)))
                .flatMap(waterBox -> assignmentRepository.findActiveByWaterBoxId(waterBoxId)
                        .hasElement()
                        .flatMap(hasActive -> {
                            if (hasActive) {
                                return Mono.error(new WaterBoxAlreadyAssignedException(waterBoxId));
                            }
                            LocalDateTime now = LocalDateTime.now();
                            WaterBoxAssignment assignment = WaterBoxAssignment.builder()
                                    .organizationId(waterBox.getOrganizationId())
                                    .waterBoxId(waterBoxId)
                                    .userId(userId)
                                    .assignmentDate(now)
                                    .assignmentStatus(AssignmentStatus.ACTIVE)
                                    .recordStatus(RecordStatus.ACTIVE)
                                    .createdAt(now)
                                    .createdBy(assignedBy)
                                    .updatedAt(now)
                                    .updatedBy(assignedBy)
                                    .build();
                            return assignmentRepository.save(assignment);
                        }))
                .flatMap(saved -> eventPublisher.publishWaterBoxAssigned(waterBoxId, userId, assignedBy)
                        .thenReturn(saved))
                .doOnSuccess(saved -> log.info("Water box {} assigned to user {} successfully", waterBoxId, userId))
                .doOnError(error -> log.error("Error assigning water box {} to user {}: {}", waterBoxId, userId,
                        error.getMessage()));
    }
}
