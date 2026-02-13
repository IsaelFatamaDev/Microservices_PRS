package pe.edu.vallegrande.vgmsclaims.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintResponse;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ResponseType;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintResponseRepository;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents.ComplaintResponseDocument;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.repositories.ComplaintResponseMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Complaint response repository port implementation using MongoDB
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ComplaintResponseRepositoryImpl implements IComplaintResponseRepository {

    private final ComplaintResponseMongoRepository responseRepository;

    @Override
    public Mono<ComplaintResponse> save(ComplaintResponse response) {
        log.debug("Saving response de complaint: {}", response.getId());
        ComplaintResponseDocument document = toDocument(response);
        document.prePersist();
        return responseRepository.save(document)
                .map(this::toDomain);
    }

    @Override
    public Mono<ComplaintResponse> findById(String id) {
        log.debug("Finding response by ID: {}", id);
        return responseRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Flux<ComplaintResponse> findByComplaintId(String complaintId) {
        log.debug("Finding responses by complaint: {}", complaintId);
        return responseRepository.findByComplaintId(complaintId)
                .map(this::toDomain);
    }

    @Override
    public Flux<ComplaintResponse> findByUserId(String userId) {
        log.debug("Finding responses by user: {}", userId);
        return responseRepository.findAll()
                .filter(doc -> userId.equals(doc.getRespondedByUserId()))
                .map(this::toDomain);
    }

    @Override
    public Mono<Long> countByComplaintId(String complaintId) {
        log.debug("Contando responses de complaint: {}", complaintId);
        return responseRepository.findByComplaintId(complaintId)
                .count();
    }

    @Override
    public Mono<Void> deleteByComplaintId(String complaintId) {
        log.debug("Deleting responses de complaint: {}", complaintId);
        return responseRepository.findByComplaintId(complaintId)
                .flatMap(doc -> responseRepository.deleteById(doc.getId()))
                .then();
    }

    // ========== Mapping methods ==========

    private ComplaintResponse toDomain(ComplaintResponseDocument document) {
        return ComplaintResponse.builder()
                .id(document.getId())
                .complaintId(document.getComplaintId())
                .responseDate(document.getResponseDate())
                .responseType(parseResponseType(document.getResponseType()))
                .message(document.getMessage())
                .respondedByUserId(document.getRespondedByUserId())
                .internalNotes(document.getInternalNotes())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    private ComplaintResponseDocument toDocument(ComplaintResponse domain) {
        return ComplaintResponseDocument.builder()
                .id(domain.getId())
                .complaintId(domain.getComplaintId())
                .responseDate(domain.getResponseDate())
                .responseType(domain.getResponseType() != null ? domain.getResponseType().name() : null)
                .message(domain.getMessage())
                .respondedByUserId(domain.getRespondedByUserId())
                .internalNotes(domain.getInternalNotes())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private ResponseType parseResponseType(String type) {
        if (type == null) return null;
        try {
            return ResponseType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
