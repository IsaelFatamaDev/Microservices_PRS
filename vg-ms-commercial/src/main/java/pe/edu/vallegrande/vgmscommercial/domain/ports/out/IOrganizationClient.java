package pe.edu.vallegrande.vgmscommercial.domain.ports.out;

import reactor.core.publisher.Mono;

public interface IOrganizationClient {
    Mono<Boolean> existsById(String organizationId);
    Mono<Double> getMonthlyFee(String organizationId);
    Mono<Integer> getGracePeriodDays(String organizationId);
    Mono<Double> getLateFeeRate(String organizationId);
}