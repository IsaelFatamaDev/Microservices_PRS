package pe.edu.vallegrande.vgmsorganizations.application.usescases.parameter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.BusinessRuleException;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.OrganizationNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Parameter;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.parameter.ICreateParameterUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationRepository;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.parameter.IParameterEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.parameter.IParameterRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateParameterUseCaseImpl implements ICreateParameterUseCase {

    private final IParameterRepository parameterRepository;
    private final IOrganizationRepository organizationRepository;
    private final IParameterEventPublisher eventPublisher;

    @Override
    public Mono<Parameter> execute(Parameter parameter, String createdBy) {
        log.info("Creating parameter: {} for organization: {}", parameter.getParameterType(), parameter.getOrganizationId());

        return organizationRepository.findById(parameter.getOrganizationId())
            .switchIfEmpty(Mono.error(new OrganizationNotFoundException(parameter.getOrganizationId())))
            .flatMap(org -> parameterRepository.existsByParameterTypeAndOrganizationId(
                parameter.getParameterType(), parameter.getOrganizationId()
            ))
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new BusinessRuleException(
                        String.format("Parameter '%s' already exists for this organization", parameter.getParameterType())
                    ));
                }

                Parameter newParam = parameter.toBuilder()
                    .id(UUID.randomUUID().toString())
                    .recordStatus(RecordStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .createdBy(createdBy)
                    .updatedAt(LocalDateTime.now())
                    .updatedBy(createdBy)
                    .build();

                return parameterRepository.save(newParam);
            })
            .flatMap(saved -> eventPublisher.publishParameterCreated(saved, createdBy)
                .thenReturn(saved)
                .onErrorResume(e -> Mono.just(saved))
            );
    }
}