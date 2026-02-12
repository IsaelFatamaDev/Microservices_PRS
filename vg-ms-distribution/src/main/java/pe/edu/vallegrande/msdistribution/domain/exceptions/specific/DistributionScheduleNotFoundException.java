package pe.edu.vallegrande.msdistribution.domain.exceptions.specific;

import pe.edu.vallegrande.msdistribution.domain.exceptions.base.NotFoundException;

public class DistributionScheduleNotFoundException extends NotFoundException {
    public DistributionScheduleNotFoundException(String id) {
        super("Distribution Schedule with ID " + id + " not found", "DISTRIBUTION_SCHEDULE_NOT_FOUND");
    }
}
