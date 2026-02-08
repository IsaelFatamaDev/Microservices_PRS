package pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StreetType {

    JR("Jr.", "Jirón"),
    AV("Av.", "Avenida"),
    CALLE("Calle", "Calle"),
    PASAJE("Psje.", "Pasaje");

    private final String prefix;
    private final String displayName;
}
