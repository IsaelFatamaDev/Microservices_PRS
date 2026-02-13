package pe.edu.vallegrande.vgmsclaims.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsclaims.application.mappers.ComplaintMapper;
import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintRepository;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.repositories.ComplaintMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Complaint repository port implementation using MongoDB
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ComplaintRepositoryImpl implements IComplaintRepository {

    private final ComplaintMongoRepository complaintRepository;
    private final ComplaintMapper mapper;

    @Override
    public Mono<Complaint> save(Complaint complaint) {
        log.debug("Saving complaint: {}", complaint.getId());
        return complaintRepository.save(mapper.toDocument(complaint))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Complaint> findById(String id) {
        log.debug("Finding complaint by ID: {}", id);
        return complaintRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Complaint> findAll() {
        log.debug("Finding all the complaints");
        return complaintRepository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Complaint> findByOrganizationId(String organizationId) {
        log.debug("Finding complaints by organization: {}", organizationId);
        return complaintRepository.findByOrganizationId(organizationId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Complaint> findByUserId(String userId) {
        log.debug("Finding complaints by user: {}", userId);
        return complaintRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Complaint> findByStatus(String status) {
        log.debug("Finding complaints by status: {}", status);
        return complaintRepository.findByStatus(status)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Complaint> findByCategoryId(String categoryId) {
        log.debug("Finding complaints by category: {}", categoryId);
        return complaintRepository.findByCategoryId(categoryId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Deleting complaint: {}", id);
        return complaintRepository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return complaintRepository.existsById(id);
    }
}
