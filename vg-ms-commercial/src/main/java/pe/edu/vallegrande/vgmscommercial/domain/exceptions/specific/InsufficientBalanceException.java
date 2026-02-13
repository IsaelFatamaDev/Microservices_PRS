package pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific;

import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;

public class InsufficientBalanceException extends BusinessRuleException {

    public InsufficientBalanceException(String details) {
        super(String.format("Insufficient balance. %s", details), "INSUFFICIENT_BALANCE");
    }
}