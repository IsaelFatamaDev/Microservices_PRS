package pe.edu.vallegrande.vgmscommercial.domain.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.InsufficientBalanceException;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IDebtRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IOrganizationClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommercialAuthorizationService {

    private final IDebtRepository debtRepository;
    private final IOrganizationClient organizationClient;

    public Mono<Boolean> canScheduleServiceCut(String userId, String organizationId) {
        return debtRepository.sumPendingAmountByUserId(userId)
            .flatMap(totalDebt -> organizationClient.getMonthlyFee(organizationId)
                .map(monthlyFee -> {
                    double threshold = monthlyFee * 3;
                    boolean canCut = totalDebt >= threshold;
                    log.debug("User {} has debt {}, threshold {}, can schedule cut: {}",
                        userId, totalDebt, threshold, canCut);
                    return canCut;
                })
            );
    }

    public Mono<Boolean> validatePettyCashWithdrawal(PettyCash pettyCash, Double amount) {
        if (!pettyCash.hasAvailableBalance(amount)) {
            return Mono.error(new InsufficientBalanceException(
                String.format("Available: %.2f, Requested: %.2f",
                    pettyCash.getCurrentBalance(), amount)
            ));
        }
        return Mono.just(true);
    }

    public Mono<Double> calculateLateFee(String organizationId, Double pendingAmount, Integer daysOverdue) {
        return organizationClient.getLateFeeRate(organizationId)
            .map(rate -> {
                double fee = pendingAmount * (rate / 100) * daysOverdue;
                log.debug("Calculated late fee: {} for {} days overdue", fee, daysOverdue);
                return fee;
            });
    }
}
