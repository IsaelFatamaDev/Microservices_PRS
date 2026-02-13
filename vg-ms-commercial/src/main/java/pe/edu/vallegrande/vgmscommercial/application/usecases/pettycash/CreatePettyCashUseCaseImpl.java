package pe.edu.vallegrande.vgmscommercial.application.usecases.pettycash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.pettycash.ICreatePettyCashUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPettyCashRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IUserServiceClient;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePettyCashUseCaseImpl implements ICreatePettyCashUseCase {

     private final IPettyCashRepository pettyCashRepository;
     private final IUserServiceClient userServiceClient;

     @Override
     public Mono<PettyCash> execute(PettyCash pettyCash) {
          log.info("Creating petty cash for organization: {}", pettyCash.getOrganizationId());

          return pettyCashRepository.findActiveByOrganizationId(pettyCash.getOrganizationId())
                    .flatMap(existing -> Mono.<PettyCash>error(
                              new BusinessRuleException("Organization already has an active petty cash")))
                    .switchIfEmpty(Mono.defer(() -> userServiceClient
                              .existsById(pettyCash.getResponsibleUserId(), pettyCash.getOrganizationId())
                              .flatMap(exists -> {
                                   if (!exists) {
                                        return Mono.error(new BusinessRuleException(
                                                  "Responsible user not found: " + pettyCash.getResponsibleUserId()));
                                   }
                                   return pettyCashRepository.save(pettyCash);
                              })))
                    .doOnSuccess(saved -> log.info("Petty cash created: {}", saved.getId()));
     }
}
