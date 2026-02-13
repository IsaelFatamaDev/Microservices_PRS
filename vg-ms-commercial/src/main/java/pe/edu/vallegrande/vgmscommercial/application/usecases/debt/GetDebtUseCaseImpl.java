package pe.edu.vallegrande.vgmscommercial.application.usecases.debt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.Debt;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.debt.IGetDebtUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IDebtRepository;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.DebtNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetDebtUseCaseImpl implements IGetDebtUseCase {

     private final IDebtRepository debtRepository;

     @Override
     public Mono<Debt> findById(String id, String organizationId) {
          log.debug("Finding debt by id: {}", id);
          return debtRepository.findById(id)
                    .filter(d -> d.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new DebtNotFoundException(id)));
     }

     @Override
     public Flux<Debt> findAll(String organizationId, String status, String userId, Integer page, Integer size) {
          log.debug("Finding debts for organization: {}", organizationId);
          Flux<Debt> debts;
          if (status != null && !status.isEmpty()) {
               debts = debtRepository.findByOrganizationIdAndStatus(organizationId, status);
          } else {
               debts = debtRepository.findByOrganizationId(organizationId);
          }
          if (userId != null && !userId.isEmpty()) {
               debts = debts.filter(d -> d.getUserId().equals(userId));
          }
          if (page != null && size != null) {
               debts = debts.skip((long) page * size).take(size);
          }
          return debts;
     }

     @Override
     public Flux<Debt> findByUserId(String userId, String organizationId) {
          log.debug("Finding debts for user: {}", userId);
          return debtRepository.findByUserId(userId)
                    .filter(d -> d.getOrganizationId().equals(organizationId));
     }

     @Override
     public Flux<Debt> findPendingDebts(String organizationId) {
          log.debug("Finding pending debts for organization: {}", organizationId);
          return debtRepository.findPendingByOrganizationId(organizationId);
     }

     @Override
     public Mono<Double> getTotalDebtByUser(String userId, String organizationId) {
          log.debug("Getting total debt for user: {}", userId);
          return debtRepository.sumPendingAmountByUserId(userId);
     }

     @Override
     public Mono<Long> count(String organizationId, String status) {
          return debtRepository.countByOrganizationIdAndStatus(organizationId, status);
     }
}
