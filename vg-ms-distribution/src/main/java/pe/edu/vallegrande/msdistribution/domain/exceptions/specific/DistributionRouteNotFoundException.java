package pe.edu.vallegrande.msdistribution.domain.exceptions.specific;

import pe.edu.vallegrande.msdistribution.domain.exceptions.base.NotFoundException;

public class DistributionRouteNotFoundException extends NotFoundException {
    public DistributionRouteNotFoundException(String id) {
        super("Distribution Route with ID " + id + " not found", "DISTRIBUTION_ROUTE_NOT_FOUND");
    }
}
