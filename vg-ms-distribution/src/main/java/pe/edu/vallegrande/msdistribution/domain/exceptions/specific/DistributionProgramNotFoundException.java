package pe.edu.vallegrande.msdistribution.domain.exceptions.specific;

import pe.edu.vallegrande.msdistribution.domain.exceptions.base.NotFoundException;

public class DistributionProgramNotFoundException extends NotFoundException {
    public DistributionProgramNotFoundException(String id) {
        super("Distribution Program with ID " + id + " not found", "DISTRIBUTION_PROGRAM_NOT_FOUND");
    }
}
