package pe.edu.vallegrande.vgmscommercial.application.usecases.receipt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.Receipt;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.receipt.IGetReceiptUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IReceiptRepository;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.ReceiptNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetReceiptUseCaseImpl implements IGetReceiptUseCase {

     private final IReceiptRepository receiptRepository;

     @Override
     public Mono<Receipt> findById(String id, String organizationId) {
          log.debug("Finding receipt by id: {}", id);
          return receiptRepository.findById(id)
                    .filter(r -> r.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new ReceiptNotFoundException(id)));
     }

     @Override
     public Flux<Receipt> findAll(String organizationId, String status, String userId, Integer page, Integer size) {
          log.info("Finding receipts - OrgId: {}, Status: {}, UserId: {}, Page: {}, Size: {}",
                    organizationId, status, userId, page, size);
          Flux<Receipt> receipts;
          if (status != null && !status.isEmpty()) {
               receipts = receiptRepository.findByOrganizationIdAndStatus(organizationId, status);
          } else {
               receipts = receiptRepository.findByOrganizationId(organizationId);
          }
          if (userId != null && !userId.isEmpty()) {
               receipts = receipts.filter(r -> r.getUserId().equals(userId));
          }
          if (page != null && size != null) {
               receipts = receipts.skip((long) page * size).take(size);
          }
          return receipts
                    .doOnNext(r -> log.debug("Returning receipt: {} for user: {} with {} details",
                                             r.getId(), r.getUserId(), r.getDetails() != null ? r.getDetails().size() : 0))
                    .doOnComplete(() -> log.info("Completed receipt search for organizationId: {}", organizationId));
     }

     @Override
     public Flux<Receipt> findByUserId(String userId, String organizationId) {
          log.debug("Finding receipts for user: {}", userId);
          return receiptRepository.findByUserId(userId)
                    .filter(r -> r.getOrganizationId().equals(organizationId));
     }

     @Override
     public Flux<Receipt> findByPeriod(Integer month, Integer year, String organizationId) {
          log.debug("Finding receipts for period: {}/{}", month, year);
          return receiptRepository.findByOrganizationIdAndPeriod(organizationId, month, year);
     }

     @Override
     public Mono<Long> count(String organizationId, String status) {
          return receiptRepository.countByOrganizationIdAndStatus(organizationId, status);
     }
}
