package pe.edu.vallegrande.msdistribution.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionRoute;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.msdistribution.domain.ports.out.route.IDistributionRouteRepository;
import pe.edu.vallegrande.msdistribution.infrastructure.persistence.documents.DistributionRouteDocument;
import pe.edu.vallegrande.msdistribution.infrastructure.persistence.repositories.DistributionRouteMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DistributionRouteRepositoryImpl implements IDistributionRouteRepository {

    private final DistributionRouteMongoRepository mongoRepository;

    @Override
    public Mono<DistributionRoute> save(DistributionRoute route) {
        return mongoRepository.save(toDocument(route)).map(this::toDomain);
    }

    @Override
    public Mono<DistributionRoute> findById(String id) {
        return mongoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Mono<DistributionRoute> update(DistributionRoute route) {
        return mongoRepository.save(toDocument(route)).map(this::toDomain);
    }

    @Override
    public Flux<DistributionRoute> findAll() {
        return mongoRepository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<DistributionRoute> findByRecordStatus(RecordStatus status) {
        return mongoRepository.findAllByRecordStatus(status.name()).map(this::toDomain);
    }

    @Override
    public Flux<DistributionRoute> findByOrganizationId(String organizationId) {
        return mongoRepository.findByOrganizationId(organizationId).map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }

    private DistributionRouteDocument toDocument(DistributionRoute domain) {
        DistributionRouteDocument doc = new DistributionRouteDocument();
        doc.setId(domain.getId());
        doc.setOrganizationId(domain.getOrganizationId());
        doc.setRouteName(domain.getRouteName());
        doc.setTotalEstimatedDuration(domain.getTotalEstimatedDuration());
        doc.setResponsibleUserId(domain.getResponsibleUserId());
        doc.setRecordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : "ACTIVE");
        doc.setCreatedAt(domain.getCreatedAt());
        doc.setCreatedBy(domain.getCreatedBy());
        doc.setUpdatedAt(domain.getUpdatedAt());
        doc.setUpdatedBy(domain.getUpdatedBy());
        if (domain.getZones() != null) {
            doc.setZones(domain.getZones().stream().map(z -> {
                DistributionRouteDocument.ZoneOrderEmbedded e = new DistributionRouteDocument.ZoneOrderEmbedded();
                e.setZoneId(z.getZoneId());
                e.setOrder(z.getOrder());
                e.setEstimatedDuration(z.getEstimatedDuration());
                return e;
            }).collect(Collectors.toList()));
        }
        return doc;
    }

    private DistributionRoute toDomain(DistributionRouteDocument doc) {
        return DistributionRoute.builder()
                .id(doc.getId())
                .organizationId(doc.getOrganizationId())
                .routeName(doc.getRouteName())
                .totalEstimatedDuration(doc.getTotalEstimatedDuration())
                .responsibleUserId(doc.getResponsibleUserId())
                .recordStatus(doc.getRecordStatus() != null ? RecordStatus.valueOf(doc.getRecordStatus()) : RecordStatus.ACTIVE)
                .createdAt(doc.getCreatedAt())
                .createdBy(doc.getCreatedBy())
                .updatedAt(doc.getUpdatedAt())
                .updatedBy(doc.getUpdatedBy())
                .zones(doc.getZones() != null ? doc.getZones().stream()
                        .map(z -> DistributionRoute.ZoneOrder.builder()
                                .zoneId(z.getZoneId())
                                .order(z.getOrder())
                                .estimatedDuration(z.getEstimatedDuration())
                                .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }
}
