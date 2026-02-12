package pe.edu.vallegrande.msdistribution.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionSchedule;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.msdistribution.domain.ports.out.schedule.IDistributionScheduleRepository;
import pe.edu.vallegrande.msdistribution.infrastructure.persistence.documents.DistributionScheduleDocument;
import pe.edu.vallegrande.msdistribution.infrastructure.persistence.repositories.DistributionScheduleMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DistributionScheduleRepositoryImpl implements IDistributionScheduleRepository {

    private final DistributionScheduleMongoRepository mongoRepository;

    @Override
    public Mono<DistributionSchedule> save(DistributionSchedule schedule) {
        return mongoRepository.save(toDocument(schedule)).map(this::toDomain);
    }

    @Override
    public Mono<DistributionSchedule> findById(String id) {
        return mongoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Mono<DistributionSchedule> update(DistributionSchedule schedule) {
        return mongoRepository.save(toDocument(schedule)).map(this::toDomain);
    }

    @Override
    public Flux<DistributionSchedule> findAll() {
        return mongoRepository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<DistributionSchedule> findByRecordStatus(RecordStatus status) {
        return mongoRepository.findAllByRecordStatus(status.name()).map(this::toDomain);
    }

    @Override
    public Flux<DistributionSchedule> findByOrganizationId(String organizationId) {
        return mongoRepository.findByOrganizationId(organizationId).map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }

    private DistributionScheduleDocument toDocument(DistributionSchedule domain) {
        DistributionScheduleDocument doc = new DistributionScheduleDocument();
        doc.setId(domain.getId());
        doc.setOrganizationId(domain.getOrganizationId());
        doc.setZoneId(domain.getZoneId());
        doc.setStreetId(domain.getStreetId());
        doc.setScheduleName(domain.getScheduleName());
        doc.setDaysOfWeek(domain.getDaysOfWeek());
        doc.setStartTime(domain.getStartTime());
        doc.setEndTime(domain.getEndTime());
        doc.setDurationHours(domain.getDurationHours());
        doc.setRecordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : "ACTIVE");
        doc.setCreatedAt(domain.getCreatedAt());
        doc.setCreatedBy(domain.getCreatedBy());
        doc.setUpdatedAt(domain.getUpdatedAt());
        doc.setUpdatedBy(domain.getUpdatedBy());
        return doc;
    }

    private DistributionSchedule toDomain(DistributionScheduleDocument doc) {
        return DistributionSchedule.builder()
                .id(doc.getId())
                .organizationId(doc.getOrganizationId())
                .zoneId(doc.getZoneId())
                .streetId(doc.getStreetId())
                .scheduleName(doc.getScheduleName())
                .daysOfWeek(doc.getDaysOfWeek())
                .startTime(doc.getStartTime())
                .endTime(doc.getEndTime())
                .durationHours(doc.getDurationHours())
                .recordStatus(doc.getRecordStatus() != null ? RecordStatus.valueOf(doc.getRecordStatus()) : RecordStatus.ACTIVE)
                .createdAt(doc.getCreatedAt())
                .createdBy(doc.getCreatedBy())
                .updatedAt(doc.getUpdatedAt())
                .updatedBy(doc.getUpdatedBy())
                .build();
    }
}
