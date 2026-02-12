package pe.edu.vallegrande.msdistribution.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionProgram;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.msdistribution.domain.ports.out.program.IDistributionProgramRepository;
import pe.edu.vallegrande.msdistribution.infrastructure.persistence.documents.DistributionProgramDocument;
import pe.edu.vallegrande.msdistribution.infrastructure.persistence.repositories.DistributionProgramMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DistributionProgramRepositoryImpl implements IDistributionProgramRepository {

    private final DistributionProgramMongoRepository mongoRepository;

    @Override
    public Mono<DistributionProgram> save(DistributionProgram program) {
        DistributionProgramDocument doc = toDocument(program);
        return mongoRepository.save(doc).map(this::toDomain);
    }

    @Override
    public Mono<DistributionProgram> findById(String id) {
        return mongoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Mono<DistributionProgram> update(DistributionProgram program) {
        DistributionProgramDocument doc = toDocument(program);
        return mongoRepository.save(doc).map(this::toDomain);
    }

    @Override
    public Flux<DistributionProgram> findAll() {
        return mongoRepository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<DistributionProgram> findByRecordStatus(RecordStatus status) {
        return mongoRepository.findAllByRecordStatus(status.name()).map(this::toDomain);
    }

    @Override
    public Flux<DistributionProgram> findByOrganizationId(String organizationId) {
        return mongoRepository.findByOrganizationId(organizationId).map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }

    // ========== MAPPERS ==========

    private DistributionProgramDocument toDocument(DistributionProgram domain) {
        DistributionProgramDocument doc = new DistributionProgramDocument();
        doc.setId(domain.getId());
        doc.setOrganizationId(domain.getOrganizationId());
        doc.setScheduleId(domain.getScheduleId());
        doc.setRouteId(domain.getRouteId());
        doc.setZoneId(domain.getZoneId());
        doc.setStreetId(domain.getStreetId());
        doc.setProgramDate(domain.getProgramDate());
        doc.setPlannedStartTime(domain.getPlannedStartTime());
        doc.setPlannedEndTime(domain.getPlannedEndTime());
        doc.setActualStartTime(domain.getActualStartTime());
        doc.setActualEndTime(domain.getActualEndTime());
        doc.setResponsibleUserId(domain.getResponsibleUserId());
        doc.setObservations(domain.getObservations());
        doc.setRecordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : "ACTIVE");
        doc.setCreatedAt(domain.getCreatedAt());
        doc.setCreatedBy(domain.getCreatedBy());
        doc.setUpdatedAt(domain.getUpdatedAt());
        doc.setUpdatedBy(domain.getUpdatedBy());
        return doc;
    }

    private DistributionProgram toDomain(DistributionProgramDocument doc) {
        return DistributionProgram.builder()
                .id(doc.getId())
                .organizationId(doc.getOrganizationId())
                .scheduleId(doc.getScheduleId())
                .routeId(doc.getRouteId())
                .zoneId(doc.getZoneId())
                .streetId(doc.getStreetId())
                .programDate(doc.getProgramDate())
                .plannedStartTime(doc.getPlannedStartTime())
                .plannedEndTime(doc.getPlannedEndTime())
                .actualStartTime(doc.getActualStartTime())
                .actualEndTime(doc.getActualEndTime())
                .responsibleUserId(doc.getResponsibleUserId())
                .observations(doc.getObservations())
                .recordStatus(doc.getRecordStatus() != null ? RecordStatus.valueOf(doc.getRecordStatus()) : RecordStatus.ACTIVE)
                .createdAt(doc.getCreatedAt())
                .createdBy(doc.getCreatedBy())
                .updatedAt(doc.getUpdatedAt())
                .updatedBy(doc.getUpdatedBy())
                .build();
    }
}
