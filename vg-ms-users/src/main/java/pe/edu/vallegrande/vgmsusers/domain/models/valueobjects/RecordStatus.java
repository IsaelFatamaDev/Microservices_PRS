package pe.edu.vallegrande.vgmsusers.domain.models.valueobjects;

public enum RecordStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo");

    private final String displayName;

    RecordStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive(){
        return this.equals(ACTIVE);
    }
}
