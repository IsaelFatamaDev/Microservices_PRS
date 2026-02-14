package pe.edu.vallegrande.vgmsinfrastructure.application.usecases.transfer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.TransferNotAllowedException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.WaterBoxNotFoundException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxAssignment;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxTransfer;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.AssignmentStatus;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.ITransferWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IInfrastructureEventPublisher;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxAssignmentRepository;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxRepository;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxTransferRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferWaterBoxUseCaseImpl implements ITransferWaterBoxUseCase {

        private final IWaterBoxRepository waterBoxRepository;
        private final IWaterBoxAssignmentRepository assignmentRepository;
        private final IWaterBoxTransferRepository transferRepository;
        private final IInfrastructureEventPublisher eventPublisher;

        @Override
        public Mono<WaterBoxTransfer> execute(String waterBoxId, String fromUserId, String toUserId,
                        Double transferFee, String notes, String createdBy) {
                log.info("Transferring water box {} from user {} to user {}", waterBoxId, fromUserId, toUserId);
                return waterBoxRepository.findById(waterBoxId)
                                .switchIfEmpty(Mono.error(new WaterBoxNotFoundException(waterBoxId)))
                                .flatMap(waterBox -> assignmentRepository.findActiveByWaterBoxId(waterBoxId)
                                                .switchIfEmpty(Mono.error(new TransferNotAllowedException(
                                                                "No active assignment found for water box: "
                                                                                + waterBoxId)))
                                                .flatMap(currentAssignment -> {
                                                        if (!currentAssignment.getUserId().equals(fromUserId)) {
                                                                return Mono.error(new TransferNotAllowedException(
                                                                                "User " + fromUserId
                                                                                                + " is not the current assignee of water box "
                                                                                                + waterBoxId));
                                                        }
                                                        WaterBoxAssignment deactivated = currentAssignment
                                                                        .markAsTransferred(createdBy);
                                                        return assignmentRepository.save(deactivated);
                                                })
                                                .flatMap(deactivated -> {
                                                        LocalDateTime now = LocalDateTime.now();
                                                        WaterBoxAssignment newAssignment = WaterBoxAssignment.builder()
                                                                        .organizationId(deactivated.getOrganizationId())
                                                                        .waterBoxId(waterBoxId)
                                                                        .userId(toUserId)
                                                                        .assignmentDate(now)
                                                                        .assignmentStatus(AssignmentStatus.ACTIVE)
                                                                        .recordStatus(RecordStatus.ACTIVE)
                                                                        .createdAt(now)
                                                                        .createdBy(createdBy)
                                                                        .updatedAt(now)
                                                                        .updatedBy(createdBy)
                                                                        .build();
                                                        return assignmentRepository.save(newAssignment);
                                                })
                                                .flatMap(newAssignment -> {
                                                        LocalDateTime now = LocalDateTime.now();
                                                        WaterBoxTransfer transfer = WaterBoxTransfer.builder()
                                                                        .organizationId(newAssignment
                                                                                        .getOrganizationId())
                                                                        .waterBoxId(waterBoxId)
                                                                        .fromUserId(fromUserId)
                                                                        .toUserId(toUserId)
                                                                        .transferDate(now)
                                                                        .transferFee(transferFee != null ? transferFee
                                                                                        : 0.0)
                                                                        .notes(notes)
                                                                        .recordStatus("ACTIVE")
                                                                        .createdAt(now)
                                                                        .createdBy(createdBy)
                                                                        .build();
                                                        return transferRepository.save(transfer);
                                                }))
                                .flatMap(saved -> eventPublisher
                                                .publishWaterBoxTransferred(waterBoxId, fromUserId, toUserId, createdBy)
                                                .thenReturn(saved))
                                .doOnSuccess(saved -> log.info("Water box {} transferred from {} to {} successfully",
                                                waterBoxId, fromUserId, toUserId))
                                .doOnError(error -> log.error("Error transferring water box {}: {}", waterBoxId,
                                                error.getMessage()));
        }
}
