package pe.edu.vallegrande.vgmscommercial.application.usecases.pettycash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCashMovement;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.MovementType;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.pettycash.IRegisterMovementUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPettyCashRepository;
import pe.edu.vallegrande.vgmscommercial.domain.services.CommercialAuthorizationService;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.PettyCashNotFoundException;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterMovementUseCaseImpl implements IRegisterMovementUseCase {

     private final IPettyCashRepository pettyCashRepository;
     private final CommercialAuthorizationService authorizationService;

     @Override
     public Mono<PettyCashMovement> execute(PettyCashMovement movement) {
          log.info("Registering movement for petty cash: {}", movement.getPettyCashId());
          return pettyCashRepository.findById(movement.getPettyCashId())
                    .switchIfEmpty(Mono.error(new PettyCashNotFoundException(movement.getPettyCashId())))
                    .flatMap(pettyCash -> {
                         if (!pettyCash.isActive()) {
                              return Mono.error(new BusinessRuleException("Petty cash is not active"));
                         }
                         PettyCash updated;
                         if (MovementType.OUT.equals(movement.getMovementType())) {
                              return authorizationService.validatePettyCashWithdrawal(pettyCash, movement.getAmount())
                                        .flatMap(valid -> {
                                             PettyCash withdrawn = pettyCash.withdrawFunds(movement.getAmount(),
                                                       movement.getCreatedBy());
                                             return pettyCashRepository.save(withdrawn)
                                                       .then(pettyCashRepository.saveMovement(movement));
                                        });
                         } else {
                              updated = pettyCash.addFunds(movement.getAmount(), movement.getCreatedBy());
                              return pettyCashRepository.save(updated)
                                        .then(pettyCashRepository.saveMovement(movement));
                         }
                    })
                    .doOnSuccess(saved -> log.info("Movement registered: {}", saved.getId()));
     }
}
