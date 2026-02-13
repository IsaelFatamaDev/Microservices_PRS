package pe.edu.vallegrande.vgmscommercial.application.usecases.debt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.Debt;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.debt.ICreateDebtUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IDebtRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ICommercialEventPublisher;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IUserServiceClient;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateDebtUseCaseImpl implements ICreateDebtUseCase {

     private final IDebtRepository debtRepository;
     private final ICommercialEventPublisher eventPublisher;
     private final IUserServiceClient userServiceClient;

     @Override
     public Mono<Debt> execute(Debt debt) {
          log.info("Creating debt for user: {} period {}/{}", debt.getUserId(), debt.getPeriodMonth(),
                    debt.getPeriodYear());

          return userServiceClient.existsById(debt.getUserId(), debt.getOrganizationId())
                    .flatMap(exists -> {
                         if (!exists) {
                              return Mono.error(new BusinessRuleException("User not found: " + debt.getUserId()));
                         }
                         return debtRepository.save(debt);
                    })
                    .doOnSuccess(saved -> {
                         log.info("Debt created successfully: {}", saved.getId());
                         eventPublisher.publishDebtCreated(saved, saved.getCreatedBy());
                    });
     }
}
